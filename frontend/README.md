# Frontend – AI Salgsoppgave

React 18 + TypeScript + Tailwind, satt opp med Create React App.

Se [hoved-README-en](../README.md) for hva prosjektet er, og
[`docs/ARKITEKTUR.md`](../docs/ARKITEKTUR.md#frontend) for hvordan frontend er bygget
opp – ruter, context-providere og konvensjoner.

## Kom i gang

Enkleste vei er å kjøre hele stacken med Docker fra rotkatalogen:

```bash
cp .env.example .env && docker compose up
```

Vil du kjøre bare frontend, med hot reload:

```bash
npm install
npm start        # http://localhost:3000
```

Frontend snakker med backend via `REACT_APP_BACKEND_URL`. Lokalt kommer den fra
`.env.development` og peker på `http://localhost:8080`, så
[backend må kjøre](../README.md#utvikling-uten-docker) for at appen skal virke.

## Kommandoer

| Kommando        | Beskrivelse                                               |
| --------------- | --------------------------------------------------------- |
| `npm start`     | Utviklingsserver med hot reload på port 3000               |
| `npm run build` | Produksjonsbygg til `build/`                               |
| `npm test`      | Testkjører i watch-modus – det finnes ingen testfiler ennå |

## Konfigurasjon

Miljøvariabler ligger i `.env` (produksjon) og `.env.development` (lokalt), begge i
Git. De leses **kun** gjennom `utils/EnvironmentsManager.ts` – ikke les
`process.env` direkte andre steder.

| Variabel                 | Beskrivelse           |
| ------------------------ | --------------------- |
| `REACT_APP_BACKEND_URL`  | Adressen til backend  |
| `REACT_APP_FRONTEND_URL` | Adressen til frontend |

Begge er offentlige URL-er, ikke hemmeligheter. I Docker settes de som
**byggeargumenter**, fordi Create React App baker dem inn i bundlen på byggetidspunktet
– en kjørende container kan altså ikke endre dem.

Det finnes **ingen sporing** i appen: PostHog og Facebook Pixel er fjernet, både koden
og avhengighetene.

## Verdt å vite

- **Det finnes ikke noe API-klientlag.** Komponentene kaller `axios` direkte med
  `getBackendURL() + "/api/v1/..."`. `axios.defaults.withCredentials = true` settes i
  `App.tsx`, fordi innloggingen er sesjonsbasert med informasjonskapsler.
- **`types/` og `enums/` speiler backend for hånd.** Endrer du en entitet eller en
  enum i backend, må du endre den her også.
- **Autentiseringen er implisitt.** `UserProvider` kaller `/api/v1/user/me` ved
  montering og sender deg til `/register` ved feil. Det er den eneste rutevakten.
