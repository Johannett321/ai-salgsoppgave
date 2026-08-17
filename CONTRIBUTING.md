# Bidra til AI Salgsoppgave

Takk for at du vurderer å bidra.

Prosjektet vedlikeholdes ikke aktivt. Du er hjertelig velkommen til å sende inn
endringer, men regn med at det kan ta tid før noen svarer. Vil du ta prosjektet i en
helt egen retning, er det bare å opprette en fork – lisensen er MIT.

## Komme i gang

```bash
cp .env.example .env     # legg inn de to API-nøklene
docker compose up
```

Se [Kom i gang](README.md#kom-i-gang) i README for detaljer, og
[Utvikling uten Docker](README.md#utvikling-uten-docker) hvis du vil ha hot reload og
debugger.

## Aldri sjekk inn hemmeligheter

Dette er den ene regelen som virkelig betyr noe.

- Rot-`.env` er git-ignorert. Alle nøkler og passord hører hjemme der.
- `frontend/.env` **ligger i Git** og skal bare inneholde offentlige URL-er.
- `application.properties` ligger i Git og skal bare inneholde standardverdier og
  `${MILJØVARIABEL:standard}`-referanser – aldri en faktisk nøkkel.
- `pinggy.sh` er git-ignorert fordi den inneholder et personlig token. Bruk
  `pinggy.sh.example` som mal.
- Sjekk `git diff` før du committer.

Oppdager du at en hemmelighet er sjekket inn, se [SECURITY.md](SECURITY.md).

## Ting vi bevisst ikke vil ha tilbake

- **Betaling.** Tjenesten er gratis. Ikke legg inn Stripe, abonnement eller kvoter.
- **Sporing.** Ingen PostHog, Google Analytics, Facebook Pixel eller liknende.
- **Spring AI som SNAPSHOT.** Bruk en stabil utgivelse. En bevegelig snapshot er
  grunnen til at prosjektet en periode ikke lot seg bygge i det hele tatt.

## Retningslinjer for kode

- **Skriv kode som ligner koden rundt.** Følg kommentartetthet, navngiving og idiomer
  som allerede finnes i filen du endrer.
- **Domenespråket er norsk.** Entiteter, felter og alle LLM-prompter bruker norske
  begreper – *salgsoppgave*, *bemerkning*, *bolig*, *megler*. Behold det.
  Rammeverkskode og klassenavn er på engelsk. Ikke oversett det ene til det andre.
- **Endrer du en entitet, skriv en Liquibase-migrasjon.** Det finnes ingen
  `ddl-auto`; en ny migrasjon er en ny XML-fil i
  `backend/src/main/resources/db/changelog/migrations/1.0/`.
- **Endrer du en enum eller en entitet, oppdater begge sider.** `frontend/src/types/`
  og `frontend/src/enums/` speiler backend for hånd.
- **Ny konfigurasjon er en miljøvariabel med standardverdi**, satt i
  `application.properties` som `${MIN_VARIABEL:standard}`, og dokumentert i
  `.env.example`. Appen skal fortsatt starte uten at den er satt.
- **Legger du til en ny provider,** husk å kalle `markSalgsoppgaveJobAsFinished(job)`
  på alle veier ut av metoden – ellers henger jobben i `LLM_IN_PROGRESS` for alltid.
  Skal provideren søke i vektorlageret, må `getOrder()` være høyere enn 10.
- **Eksponerer du et endepunkt,** bruk `@NoLogin`, `@NoCors` eller `@NoSecurity` på
  kontrollermetoden. Det er alt som skal til – stiene oppdages ved refleksjon under
  oppstart.

## Tester

```bash
cd backend
./mvnw test
./mvnw test -Dtest=MetricsProviderTest
```

Trenger testene en database, start den med `docker compose up -d postgres`.

Backend-testene leser PDF-er fra katalogen `salgsoppgave.datadir` peker på. Frontend
har ingen tester ennå; nye er velkomne.

## Pull requests

1. Lag en gren fra `main`.
2. Hold endringen så liten som mulig – én ting per PR.
3. Beskriv hva du har endret og hvorfor, og hvordan du har testet det.
4. Sørg for at `./mvnw test` er grønn, og at `docker compose up` fortsatt starter
   hele stacken.

## Feilmeldinger

Opprett en issue med:

- hva du forventet, og hva som faktisk skjedde
- stegene for å reprodusere
- relevante logglinjer (**husk å fjerne nøkler og personopplysninger**)
- versjoner: Java, Node, operativsystem
