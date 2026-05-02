# Template Full-Stack Auth-First (Angular + Spring Boot)

Template **auth-first prêt à cloner** pour démarrer rapidement un projet Angular + Spring Boot avec sécurité, i18n et CI.

## Stack cible

- Frontend: Angular 21
- Backend: Spring Boot 4, Java 25, Maven Wrapper
- Base de données: MySQL 8 + migrations Flyway
- Cache/session sécurité: Redis 7
- Outils UI optionnels: Tailwind CSS / Preline (installation à la demande)

## Fonctionnalités incluses

- Auth backend complète: `register`, `login`, `refresh`, `logout`
- Endpoint protégé: `GET /api/me`
- JWT access/refresh + blacklist des refresh tokens dans Redis
- i18n frontend (`fr`/`en`) et backend (`messages*.properties`)
- Navbar frontend auth-aware + switch de langue
- CI GitHub Actions: `frontend-ci`, `backend-ci`, `docker-ci`, `security`

## Lancement local (Docker)

```bash
cp .env.example .env
docker compose up -d --build
```

Services exposés par défaut:

- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Adminer: `http://localhost:8081`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

## Développement local

### Frontend

```bash
cd frontend
npm ci
npm run lint
npm run test
npm run build
npm start
```

### Backend

```bash
./mvnw -f backend/pom.xml clean verify
./mvnw -f backend/pom.xml spring-boot:run
```

## IntelliJ (important)

- Ouvrir le dossier racine `template/` (pas uniquement `frontend/`)
- Recharger le projet Maven si nécessaire (`pom.xml` racine + `backend/pom.xml`)

## Structure

- `frontend/`: app Angular (`core`, `shared`, `features`, `auth`, `layout`)
- `backend/`: API Spring Boot (`config`, `security`, `auth`, `user`, `common`, `exception`)
- `docs/`: documentation architecture, API, sécurité, déploiement

## Endpoints auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/me`
