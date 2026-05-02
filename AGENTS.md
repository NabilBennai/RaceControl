# AGENTS.md

Ce fichier décrit les conventions à respecter par les agents qui contribuent à ce repository.

## Objectif du projet
Template full-stack **auth-first** prêt à cloner pour démarrer rapidement des projets Angular + Spring Boot.

## Règles générales
- Tout contenu applicatif et documentation utilisateur doit être en **français**.
- Ne jamais committer de secrets réels.
- Toujours privilégier du code lisible, maintenable et modulaire.
- Toute nouvelle fonctionnalité doit inclure une mise à jour de la documentation concernée.

## Frontend (`/frontend`)
- Stack cible: Angular 21 + Tailwind 4 + Preline.
- Architecture à conserver:
  - `core/` (services transverses, guards, interceptors)
  - `shared/` (composants/utilitaires réutilisables)
  - `features/` (fonctionnalités métier)
  - `auth/` (écrans/login/register/refresh)
  - `layout/` (public/private/admin)
- i18n obligatoire (`fr` et `en`) avec clés explicites.
- Les appels API passent par des services dédiés (pas directement dans les composants).
- Toute route protégée doit utiliser guard(s) adapté(s).

## Backend (`/backend`)
- Stack cible: Spring Boot 4 + Java 25 + Maven.
- Architecture à conserver:
  - `config/`, `security/`, `auth/`, `user/`, `common/`, `exception/`
- Ne jamais exposer directement les entités: passer par DTOs.
- Toutes les erreurs API doivent être uniformisées.
- Les messages fonctionnels doivent être internationalisables (`messages.properties`, `messages_en.properties`).
- Auth: maintenir `register/login/refresh/logout` et `/api/me`.

## Base de données & migration
- Toute évolution de schéma doit passer par Flyway (`/backend/src/main/resources/db/migration`).
- Les migrations doivent être idempotentes/robustes pour l'environnement dev.

## Qualité & tests
- Ajouter des tests unitaires/integration pour toute logique critique.
- Vérifier au minimum avant commit:
  - Frontend: lint + test + build
  - Backend: `mvn clean verify`
- Si une vérification est impossible (limite environnement), le documenter explicitement.

## CI/CD
- Maintenir les workflows GitHub Actions existants.
- Ne pas casser les jobs `frontend-ci`, `backend-ci`, `docker-ci`, `security`.

## Docker
- Conserver `.env.example` complet et sans secret réel.
- Toute nouvelle variable d'env doit être documentée dans README.

## Documentation
- Mettre à jour `README.md` et `/docs` en cas de changement structurel, technique ou d'exécution.
