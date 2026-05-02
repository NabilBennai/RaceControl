# API

Base URL: `/api`

## Authentification

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

### Payload `register` / `login`

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### Payload `refresh` / `logout`

```json
{
  "refreshToken": "<jwt-refresh>"
}
```

## Utilisateur connecté

- `GET /api/me`
- Header requis: `Authorization: Bearer <jwt-access>`

Réponse exemple:

```json
{
  "email": "user@example.com",
  "role": "ROLE_USER"
}
```

## Erreurs uniformisées

Format retourné:

```json
{
  "timestamp": "2026-05-02T15:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "message fonctionnel",
  "path": "/api/auth/login"
}
```

## CORS

Origines autorisées configurées via variable d'environnement:

- `CORS_ALLOWED_ORIGINS=http://localhost:4200`

Valeurs multiples possibles, séparées par des virgules.
