# Architecture

## Frontend (`/frontend`)

Architecture modulaire Angular 21:

- `core/`: services transverses, guards, interceptors
- `shared/`: composants/utilitaires réutilisables
- `features/`: fonctionnalités métier
- `auth/`: écrans login/register
- `layout/`: layout applicatif unique (`AppLayoutComponent`) + vues métier lazy

Principes:

- appels HTTP uniquement via services dédiés
- routes sensibles protégées par guards (`authGuard`, `adminGuard`)
- i18n `fr/en` via `@ngx-translate/core`
- composants séparés en fichiers `*.ts`, `*.html`, `*.scss`

## Backend (`/backend`)

Architecture Spring Boot 4:

- `config/`, `security/`, `auth/`, `user/`, `common/`, `exception/`

Principes:

- exposition API via DTOs (pas d'entités JPA en sortie)
- erreurs API uniformisées via `GlobalExceptionHandler`
- i18n backend via `messages.properties` et `messages_en.properties`
- migrations SQL gérées via Flyway (`src/main/resources/db/migration`)
- authentification JWT stateless + blacklist refresh token Redis
