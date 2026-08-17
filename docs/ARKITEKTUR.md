# Arkitektur

Utdypende beskrivelse av hvordan AI Salgsoppgave er satt sammen. For en rask
introduksjon og oppstartsveiledning, se [README](../README.md).

Prosjektet består av to enheter som bygges og driftes hver for seg:

- `backend/` – Spring Boot 3.4, Spring AI 1.0.0, Java 21, Maven
- `frontend/` – Create React App, React 18 + TypeScript + Tailwind + MUI

## Innhold

- [Providerkjeden](#providerkjeden)
- [Sikkerhet](#sikkerhet)
- [Persistens](#persistens)
- [Sidekanaler](#sidekanaler)
- [API-oversikt](#api-oversikt)
- [Frontend](#frontend)
- [Drift](#drift)
- [Svake punkter](#svake-punkter)

## Providerkjeden

Dette er kjernen i applikasjonen, og det du bør forstå først. Koden ligger i
`provider/` og `service/DataProvidersManagerService.java`.

### 1. Jobben opprettes

`SalgsoppgaveJobService.createJob(finnUrl)`:

1. Dedupliserer på URL – samme annonse analyseres ikke to ganger.
2. Lagrer en `SalgsoppgaveJob`.
3. Utfører nedlastingen i en `CompletableFuture`, slik at HTTP-kallet returnerer med
   én gang og frontend kan begynne å vise framdrift.

`FinnScraper` løser opp Finn-annonsen til riktig PDF. Den bruker jsoup til å hente
sidene, og et LLM-kall til å velge hvilken lenke som faktisk er «komplett
salgsoppgave» – meglernettsteder er ikke standardiserte nok til at en ren
regel-basert tilnærming holder. `PdfUtils` trekker deretter ut teksten til
`SalgsoppgaveJob.pdfContent`.

### 2. Lytterne varsles

`DataProvidersManagerService.notifySalgsoppgaveJobCreatedListeners(job)` starter en
rå `Thread` og kaller hver registrert lytter **sekvensielt, sortert på `getOrder()`**.

### 3. Providerne kjører

| Order | Provider               | Ansvar                                                    |
| ----: | ---------------------- | --------------------------------------------------------- |
|     0 | `AddressProvider`      | Adresse, og koordinater via `GeocodingService`             |
|    10 | `TextSplitterProvider` | Deler opp teksten og fyller vektorlageret                  |
|    10 | `MetricsProvider`      | Prisantydning, totalpris, bruksareal, byggeår              |
|    90 | `SummaryProvider`      | Oppsummeringen som vises i dashbordet                      |
|   101 | `RemarksProvider`      | Bemerkninger – TG2- og TG3-funn fra tilstandsrapporten     |

Hver `DataProvider`-subklasse registrerer seg selv i konstruktøren sin med
`dataProvidersManagerService.subscribeToNewSalgsoppgaveJob(this)`. Det finnes ikke noe
eksplisitt register: å legge til en `@Component` som utvider `DataProvider` er nok til
å sette den inn i kjeden.

`TextSplitterProvider` har order 10 og er den som fyller vektorlageret. **Alt som
kaller `searchVectorStoreFor` må ha høyere order enn 10**, ellers søker det i et tomt
lager.

### 4. Fullføring

En provider signaliserer at den er ferdig ved å kalle
`markSalgsoppgaveJobAsFinished(job)`, som legger jobben i providerens egen
`finished`-liste. En `@Scheduled(fixedRate = 2000)`-jobb i manageren finner snittet av
alle providernes lister; en jobb som ligger i *alle* listene settes til
`JobStatus.COMPLETED` og fjernes fra listene.

> **Konsekvens:** en provider som returnerer uten å kalle
> `markSalgsoppgaveJobAsFinished`, etterlater jobben i `LLM_IN_PROGRESS` for alltid.
> Dette er den vanligste feilen når man legger til en ny provider.

### 5. Statuser

`JobStatus` er framdriftsindikatoren frontend viser. Providerne endrer og lagrer den
underveis:

```
NOT_STARTED → VISITING_FINN → VISITING_MEGLER → DOWNLOADING_PROSPECT
            → CREATING_EMBEDDINGS → LLM_IN_PROGRESS → COMPLETED
                                                    ↘ FAILED
```

### Hjelpemetodene i DataProvider

Basisklassen gir de delte LLM-verktøyene:

| Metode                                       | Hva den gjør                                                                 |
| -------------------------------------------- | ---------------------------------------------------------------------------- |
| `llmExtractToDouble` / `llmExtractToString`   | Kjører en prompt som skal gi én enkelt verdi, med gjentakelse ved feil        |
| `getTextUsingSearchTerm`                      | Nøkkelordvindu over den rå PDF-teksten                                       |
| `searchVectorStoreFor`                        | Likhetssøk i pgvector, filtrert på `salgsoppgaveJob`-metadata, terskel 0.4    |

Alle prompter er skrevet på norsk, fordi dokumentene er norske.

## Sikkerhet

Sesjonsbasert, ikke token-basert.

- Sesjonene ligger **i minnet i webserveren**. Spring Session er ikke på klassestien,
  så `spring.session.store-type` hadde ingen effekt og er fjernet. Konsekvensen er at
  alle blir logget ut ved omstart, og at appen ikke kan skaleres over flere instanser
  uten sticky sessions.
- Informasjonskapsler sendes på tvers av opphav, så frontend setter
  `axios.defaults.withCredentials = true`.
- Innlogging er skjemainnlogging (`/login`, `email`/`password`, BCrypt) i tillegg til
  Google- og Facebook-OAuth2.

### Åpne endepunkter oppdages ved refleksjon

Dette er uvanlig nok til å være verdt å merke seg. Offentlige endepunkter listes
**ikke** opp i filterkjeden. I stedet skanner `NoSecurityProcessor.findNoSecurityPaths()`
pakken `com.johansvartdal.SpringAI.controller` ved oppstart etter metoder annotert med:

| Annotasjon    | Virkning                     |
| ------------- | ---------------------------- |
| `@NoLogin`    | Hopper over autentisering    |
| `@NoCors`     | Tillater alle opphav         |
| `@NoSecurity` | Begge deler                  |

Stien utledes av klassens `@RequestMapping` pluss metodens mapping, og
`WebSecurityConfig` mater dem inn i `permitAll()` og CORS-konfigurasjonen.

**Å eksponere et endepunkt gjør du altså ved å sette på en annotasjon – ingenting
annet.** To forbehold: annotasjonen virker bare på metoder i en klasse som har
`@RequestMapping` med en eksplisitt sti, og bare den første verdien i `path` brukes.

### Forbehold

- CSRF er slått av (det ligger en TODO om det i koden).
- Tillatte opphav utover `EnvironmentUtils.getFrontendUrl()` er hardkodet i
  `WebSecurityConfig.corsConfigurationSource()`.

## Persistens

- **Liquibase eier skjemaet.** `db/changelog/db.changelog-master.xml` gjør `includeAll`
  over `migrations/1.0`, så en ny migrasjon er en ny XML-fil i den katalogen.
- Det er **ingen `ddl-auto`**. Endrer du en entitet, må du skrive et tilhørende
  changeset – ellers går skjema og kode fra hverandre.
- `liquibase-maven-plugin` i `backend/pom.xml` er ferdig oppsatt mot den lokale
  utviklingsdatabasen for manuelle `./mvnw liquibase:*`-mål.

### Feilhåndtering

Feil blir til HTTP-status via `@ResponseStatus` på unntaksklassene i `exception/`
(`NotFoundException` → 404, `InsufficientQuotaException` → 429, …). Det finnes ingen
`@ControllerAdvice`.

## Sidekanaler

Tjenesten er gratis. Det finnes ingen abonnement, betaling eller månedskvote –
Stripe, `Subscription`, `StripeSession` og kvotelogikken er fjernet fra kodebasen.

Den eneste gjenværende begrensningen er en enkel misbruksgrense i `ChatService`:
maks 100 chat-meldinger per bruker per døgn. Den beskytter API-budsjettet til den som
drifter instansen, og har ingenting med betaling å gjøre.

| Tjeneste                | Rolle                                        | Påkrevd |
| ----------------------- | -------------------------------------------- | ------- |
| `DiscordMessageService` | Varsler om feil og nyregistreringer           | nei     |
| `MailChimpService`      | Nyhetsbrevliste                              | nei     |
| `EmailService`          | Transaksjonsmail, for eksempel nytt passord   | nei     |
| `GeocodingService`      | Adresse → koordinater for kartet              | nei     |

Alle fire er slått av når de tilhørende miljøvariablene står tomme.

## API-oversikt

Alt ligger under `/api/v1`. Endepunkter merket **åpent** er annotert `@NoLogin`.

### `/api/v1/job`

| Metode | Sti                        | Beskrivelse                          |
| ------ | -------------------------- | ------------------------------------ |
| GET    | `/{id}`                    | Hent én jobb                         |
| GET    | `/history`                 | Alle jobber for innlogget bruker     |
| POST   | `/from-finn-url`           | Start analyse fra en Finn-lenke      |
| POST   | `/{job}/manual-pdf`        | Last opp PDF manuelt                 |

### `/api/v1/chat`

| Metode | Sti                    | Beskrivelse                         |
| ------ | ---------------------- | ----------------------------------- |
| POST   | `/search`              | Fritekstsøk                         |
| POST   | `/job/{id}/question`   | Still spørsmål til en salgsoppgave  |

### `/api/v1/user`

| Metode | Sti                            | Beskrivelse                    |
| ------ | ------------------------------ | ------------------------------ |
| GET    | `/me`                          | Innlogget bruker               |
| PUT    | `/` og `/update`               | Oppdater bruker                |
| GET    | `/authenticated`               | Er jeg innlogget? **åpent**    |
| POST   | `/register`                    | Registrering **åpent**         |
| POST   | `/forgot-password`             | Be om nytt passord **åpent**   |
| PUT    | `/reset-password`              | Sett nytt passord **åpent**    |

### `/api/v1/marketing`

| Metode | Sti                          | Beskrivelse                  |
| ------ | ---------------------------- | ---------------------------- |
| POST   | `/download`                  | Last ned markedsføringsfil **åpent** |
| GET    | `/download-file/{filename}`  | Hent fil **åpent**           |

## Frontend

`App.tsx` inneholder alle rutene. Alt inni `<Layout/>` er innlogget område, og pakkes
inn i denne rekkefølgen (`components/Layout.tsx`):

```
UserProvider → SalgsoppgaveJobProvider
```

`/view/:id` legger i tillegg på `DashboardWrapper`.

**Autentiseringen er implisitt:** `UserProvider` kaller `/api/v1/user/me` når den
monteres, og gjør `navigate("/register")` ved enhver feil. Den omdirigeringen er den
eneste rutevakten i appen.

### Ruter

| Sti                    | Side                | Krever innlogging |
| ---------------------- | ------------------- | ----------------- |
| `/login`, `/register`  | Innlogging          | nei               |
| `/forgot`, `/change-password` | Passord      | nei               |
| `/privacy-policy`, `/terms-of-service` | Juridisk | nei         |
| `/`                    | Søk / lim inn lenke | ja                |
| `/upload`              | Last opp PDF        | ja                |
| `/view/:id`            | Dashbord            | ja                |
| `/historikk`           | Historikk           | ja                |
| `/lagret`              | Lagrede boliger     | ja                |

### Konvensjoner

- **Det finnes ikke noe API-klientlag.** Komponentene kaller `axios` direkte med
  `getBackendURL() + "/api/v1/..."`.
- `types/` speiler backend-entitetene og `enums/` dupliserer backend-enumen
  `JobStatus`. **Endrer du den ene siden, må du endre den andre.**
- Styling er Tailwind (merkefargene `main` og `main-darker` i `tailwind.config.js`),
  med MUI og framer-motion til enkeltkomponenter.
- **Ingen sporing.** PostHog og Facebook Pixel er fjernet, både koden og
  avhengighetene. Appen sender ingen data til tredjepart.

## Drift

### Lokalt

`docker-compose.yml` bygger begge enhetene fra kildekode og starter PostgreSQL med
pgvector. `docker compose up` er alt som trengs; se
[README](../README.md#kom-i-gang).

### På egen server

Det følger **ingen CI/CD med prosjektet**. Den opprinnelige GitHub Actions-flyten
bygget bilder til AWS ECR og rullet dem ut på én EC2-maskin, men den var knyttet til
infrastruktur som ikke finnes lenger, og er fjernet. Skal du drifte tjenesten selv,
setter du opp din egen utrulling.

`docker-compose.prod.yml` ligger igjen som et utgangspunkt. Den kjører ferdigbygde
bilder i stedet for å bygge fra kildekode, og forventer at `IMAGE_REGISTRY` peker på
registeret ditt – et hvilket som helst container-register virker:

```bash
export IMAGE_REGISTRY=ghcr.io/<brukernavn>
docker compose -f docker-compose.prod.yml up -d
```

Alt av konfigurasjon injiseres som miljøvariabler. Se
[`.env.example`](../.env.example) for hele listen; i produksjon bør de komme fra
hemmelighetshåndteringen til plattformen din, ikke fra en fil på disk.

Den enkleste veien er ofte å bare kjøre `docker-compose.yml` på serveren og bygge
der. Da slipper du et register helt.

## Svake punkter

Ærlig liste over ting den som overtar bør vite om.

- **Providerkjeden kjører på en rå `Thread`** uten pool, retry eller
  dødmannsknapp. Faller en provider av uten å markere seg ferdig, henger jobben.
- **Fullføring via listesnitt** er skjørt. En kø eller en tilstandsmaskin per jobb
  ville vært mer robust. Listene ligger dessuten i minnet, så jobber som er
  underveis ved en omstart blir aldri fullført.
- **Sesjonene ligger i minnet.** Alle blir logget ut ved omstart, og appen kan ikke
  skaleres til flere instanser uten sticky sessions.
- **CSRF er slått av.**
- **Postnummer er lagret som tall**, så postnummer med ledende null vises feil –
  `0474` blir til `474`. Feltet bør være tekst.
- **`JobStatus` er duplisert** mellom backend og frontend uten kodegenerering.
- **Ingen frontend-tester.** `npm test` er satt opp, men det finnes ingen testfiler.
- **`EnvironmentUtils` hardkoder URL-er** per miljø i stedet for å lese konfigurasjon.
- **Frontend gjør polling** hvert sekund mot `/api/v1/job/{id}` mens en analyse
  pågår, i stedet for å bruke websockets eller server-sent events.
