# Sikkerhet

## Melde fra om en sårbarhet

Har du funnet en sårbarhet, **ikke opprett en offentlig issue**. Bruk i stedet
GitHubs [private sårbarhetsrapportering](https://docs.github.com/en/code-security/security-advisories/guidance-on-reporting-and-writing-information-about-vulnerabilities/privately-reporting-a-security-vulnerability)
under fanen **Security** i dette repoet.

Ta med hva du fant, hvordan det kan reproduseres, og hva du mener konsekvensen er.

Prosjektet vedlikeholdes ikke aktivt, så det finnes ingen garantert svartid og ingen
sikkerhetsoppdateringer. Meldingen er likevel nyttig for alle som kjører en egen
instans.

## Støttede versjoner

Ingen. Koden distribueres som den er – se
[ansvarsfraskrivelsen i lisensen](LICENSE).

## Kjente svakheter

Disse er bevisste eller kjente, og dokumentert framfor å bli skjult. Kjører du en
egen instans, bør du håndtere dem selv:

- **CSRF-beskyttelse er slått av** i `WebSecurityConfig`. Det ligger en TODO om det i
  koden.
- **Tillatte CORS-opphav er delvis hardkodet** i
  `WebSecurityConfig.corsConfigurationSource()`.
- **Åpne endepunkter oppdages ved refleksjon** fra `@NoLogin`, `@NoCors` og
  `@NoSecurity`. Det gjør det lett å eksponere et endepunkt ved et uhell, og
  vanskelig å se hele angrepsflaten på ett sted. Gå gjennom annotasjonene med
  `grep -rn "@NoLogin\|@NoCors\|@NoSecurity" backend/src/main/java`.
- **Sesjonene ligger i minnet** i webserveren. Alle blir logget ut ved omstart, og
  appen tåler ikke flere instanser uten sticky sessions.
- **Avhengighetene er ikke nødvendigvis ferske.** Kjør
  `./mvnw versions:display-dependency-updates` og `npm audit` før du setter noe i
  produksjon.

## Hemmeligheter

Hemmeligheter skal aldri i Git.

- Alt av nøkler hører hjemme i rot-`.env`, som er git-ignorert. Bruk
  [`.env.example`](.env.example) som mal.
- `application.properties` ligger i Git og skal bare inneholde standardverdier og
  `${MILJØVARIABEL:standard}`-referanser.
- `frontend/.env` ligger i Git og skal bare inneholde offentlige URL-er.
- `pinggy.sh` er git-ignorert fordi den inneholder et personlig token.
- `backend/.dockerignore` sørger for at en lokal hemmelighetsfil ikke havner i et
  Docker-image.
- Drifter du tjenesten selv, injiser alt som miljøvariabler fra plattformens
  hemmelighetshåndtering – ikke som en fil på disk. Se
  [`docs/ARKITEKTUR.md`](docs/ARKITEKTUR.md#på-egen-server).

Har du sjekket inn en hemmelighet ved et uhell: **rull nøkkelen først**, og rydd
deretter i historikken. Å fjerne den i en ny commit er ikke nok – den ligger fortsatt
i historikken, og må regnes som kompromittert i det øyeblikket den ble dyttet opp.
