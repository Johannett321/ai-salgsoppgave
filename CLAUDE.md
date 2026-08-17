# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

AI Salgsoppgave — a Norwegian service that analyses a real estate prospectus
("salgsoppgave"). A user pastes a finn.no listing URL (or uploads a PDF); the backend finds and downloads
the prospectus PDF, embeds it into pgvector, runs a chain of LLM "providers" to extract address, key
metrics and remarks plus a summary, and lets the user chat with the document (RAG).

**The service is free.** There is no payment, subscription or quota — Stripe and all subscription code
were removed when the project was open sourced. There is also **no analytics**: PostHog and Facebook Pixel
are gone, code and dependencies alike. Do not reintroduce either.

Domain vocabulary and all LLM prompts are in Norwegian: *salgsoppgave* = prospectus,
*bemerkning* = remark/defect, *bolig* = home, *megler* = broker. User-facing docs are written in
Norwegian; the audience is Norway.

Two deployables: `backend/` (Spring Boot 3.4, Java 21, Maven) and `frontend/` (Create React App, React 18 +
TypeScript + Tailwind + MUI).

## Commands

```bash
# Everything at once — this is the documented path
cp .env.example .env      # add ANTHROPIC_API_KEY and OPENAI_API_KEY
docker compose up

# Just the database, when running backend/frontend natively
docker compose up -d postgres

cd backend
./mvnw spring-boot:run                   # no Spring profiles needed
./mvnw test
./mvnw test -Dtest=MetricsProviderTest
./mvnw package -DskipTests

cd frontend
npm install && npm start                 # port 3000
npm run build
```

Liquibase runs automatically at boot.

## Configuration

**All configuration is environment variables with defaults in `application.properties`.** There are no
`application-dev.properties` / `application-secret.properties` files and no `dev`/`secret` profiles — they
were removed because the app could not start without a hand-created secrets file.

- `ANTHROPIC_API_KEY` and `OPENAI_API_KEY` are the only values that really matter. Everything else
  (OAuth2, mail, Mailchimp, Discord) defaults to empty/placeholder and is simply switched off.
- The app **starts fine without any keys**; only the analysis itself fails.
- `ENVIRONMENT` (`PRODUCTION` | `STAGING` | `DEV`) drives `EnvironmentUtils`, which hardcodes
  frontend/backend URLs and CORS origins. Unset ⇒ DEVELOPMENT.
- Frontend config is CRA `.env` / `.env.development` (both committed, only public URLs). In Docker they
  are passed as **build args**, because CRA bakes them into the bundle. Read them only through
  `utils/EnvironmentsManager.ts`.
- Never commit secrets. Root `.env` is git-ignored; `frontend/.env` is committed and must stay
  non-sensitive.

## Backend architecture

### The provider pipeline (`provider/`, `service/DataProvidersManagerService.java`)

This is the core of the app and the thing to understand first.

1. `SalgsoppgaveJobService.createJob(finnUrl)` deduplicates by URL, persists a `SalgsoppgaveJob`, and does
   the download on a `CompletableFuture` so the HTTP request returns immediately. `FinnScraper` (jsoup +
   an LLM call to pick the right link) resolves the finn.no listing to the "komplett salgsoppgave" PDF,
   and `PdfUtils` extracts its text into `SalgsoppgaveJob.pdfContent`.
2. It then calls `DataProvidersManagerService.notifySalgsoppgaveJobCreatedListeners(job)`, which spawns a
   raw `Thread` and invokes every registered listener **sequentially, sorted by `getOrder()`**.
3. Each `DataProvider` subclass registers itself in its constructor via
   `dataProvidersManagerService.subscribeToNewSalgsoppgaveJob(this)` — there is no explicit registry, so
   adding a `@Component` that extends `DataProvider` is enough to insert it into the pipeline. Current
   order: `AddressProvider` (0) → `TextSplitterProvider` / `MetricsProvider` (10) → `SummaryProvider` (90)
   → `RemarksProvider` (101). `TextSplitterProvider` is what populates the vector store, so anything that
   calls `searchVectorStoreFor` must have a higher order than 10.
4. A provider signals completion by calling `markSalgsoppgaveJobAsFinished(job)`, which appends to its own
   `finished` list. A `@Scheduled(fixedRate = 2000)` job in the manager intersects the `finished` lists of
   all providers; a job present in **every** list is flipped to `JobStatus.COMPLETED` and removed from the
   lists. Consequence: a provider that returns without calling `markSalgsoppgaveJobAsFinished` leaves the
   job stuck in `LLM_IN_PROGRESS` forever.
5. `JobStatus` is the frontend's progress indicator; providers mutate and save it as they go
   (`VISITING_FINN` → `VISITING_MEGLER` → `DOWNLOADING_PROSPECT` → `CREATING_EMBEDDINGS` →
   `LLM_IN_PROGRESS` → `COMPLETED` / `FAILED`).

`DataProvider` supplies the shared LLM helpers: `llmExtractToDouble` / `llmExtractToString` (retrying
prompts that ask for a single value), `getTextUsingSearchTerm` (keyword window over the raw PDF text), and
`searchVectorStoreFor` (pgvector similarity search filtered by `salgsoppgaveJob` metadata, topK,
threshold 0.4).

### Spring AI

Pinned to the **stable `1.0.0` release** via `spring-ai-bom`. It was previously pinned to a moving
`1.0.0-SNAPSHOT`, which silently broke the build when the snapshot relocated `PgVectorStore`. **Do not
move back to a snapshot version.**

Chat model, embedding model and vector store all come from Spring AI **auto-configuration**, driven by
`application.properties` — there are no hand-built `@Bean` definitions for them. Key settings:
`spring.ai.model.chat=anthropic` and `spring.ai.model.embedding=openai` (both providers offer both, so
without these the injection is ambiguous), plus
`spring.ai.vectorstore.pgvector.dimensions=1536` and `initialize-schema=false`.

The pgvector extension is enabled by `db/changelog/migrations/1.0/00000000-enable-pgvector.xml`, which must
run before the changeset that creates `vector_store`.

### Security (`authentication/`, `annotation/`)

Session-based, not token-based. Sessions live **in memory** — Spring Session is not on the classpath, so
they are lost on restart. Cookies are sent cross-origin, so the frontend sets
`axios.defaults.withCredentials = true`. Login is form login (`/login`, `email`/`password`, BCrypt) plus
optional Google/Facebook OAuth2.

Public endpoints are **discovered by reflection at startup**, not listed in the filter chain:
`NoSecurityProcessor.findNoSecurityPaths()` scans `com.johansvartdal.SpringAI.controller` for methods
annotated `@NoLogin` (skip authentication), `@NoCors` (allow all origins) or `@NoSecurity` (both), derives
the path from the class `@RequestMapping` + method mapping, and `WebSecurityConfig` feeds those paths into
`permitAll()` and the CORS configuration source. So exposing an endpoint means adding an annotation to the
controller method — nothing else. Note the annotation only works on methods inside a class carrying
`@RequestMapping` with an explicit path, and only the first path value is used.

CSRF is disabled (there's a TODO about it).

### Persistence and other services

- Liquibase owns the schema: `db/changelog/db.changelog-master.xml` does `includeAll` over
  `migrations/1.0`, so new migrations are new XML files dropped in that directory, and **file name decides
  order**. There is no `ddl-auto`, so entity changes require a matching changeset.
- Errors surface as HTTP status via `@ResponseStatus` on the exception classes in `exception/`
  (`NotFoundException` → 404, `InsufficientQuotaException` → 429); there is no `@ControllerAdvice`.
  `InsufficientQuotaException` now only backs the 100-messages-per-day chat rate limit in `ChatService` —
  it is abuse protection, not billing.
- Side channels, all optional and off when their env vars are empty: `DiscordMessageService`,
  `MailChimpService`, `EmailService`, `GeocodingService`.
- API surface is `/api/v1/{job,chat,user,marketing}`.

## Frontend architecture

`App.tsx` holds all routes. Everything inside `<Layout/>` is authenticated and wrapped, in order, by
`UserProvider` → `SalgsoppgaveJobProvider` (`components/Layout.tsx`); `/view/:id` adds `DashboardWrapper`.
Auth is implicit: `UserProvider` calls `/api/v1/user/me` on mount and `navigate("/register")` on any
error — that redirect is the only route guard.

There is no API client layer; components call `axios` directly with
`getBackendURL() + "/api/v1/..."`. `types/` mirrors the backend entities and `enums/JobStatus.ts`
duplicates the backend enum — keep both sides in sync when changing either.

Styling is Tailwind (brand colors `main` / `main-darker` in `tailwind.config.js`) with MUI and
framer-motion used for individual components.
