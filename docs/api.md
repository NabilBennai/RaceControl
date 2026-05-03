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

## Ligues

- `GET /api/leagues`
- `POST /api/leagues`
- `GET /api/leagues/{leagueId}`
- `PUT /api/leagues/{leagueId}`
- `DELETE /api/leagues/{leagueId}`

## Saisons

- `POST /api/leagues/{leagueId}/seasons`
- `GET /api/leagues/{leagueId}/seasons`
- `GET /api/seasons/{seasonId}`
- `PUT /api/seasons/{seasonId}`
- `PATCH /api/seasons/{seasonId}/activate`
- `PATCH /api/seasons/{seasonId}/finish`

## Équipes

- `POST /api/seasons/{seasonId}/teams`
- `GET /api/seasons/{seasonId}/teams`
- `PUT /api/teams/{teamId}`
- `DELETE /api/teams/{teamId}`
- `POST /api/teams/{teamId}/members`
- `DELETE /api/teams/{teamId}/members/{memberId}`

## Courses (US-07)

- `POST /api/seasons/{seasonId}/races`
- `GET /api/seasons/{seasonId}/races`
- `GET /api/races/{raceId}`
- `PUT /api/races/{raceId}`
- `DELETE /api/races/{raceId}`
- `PATCH /api/races/{raceId}/status`

## Inscriptions course (US-08)

- `POST /api/races/{raceId}/registrations`
- `GET /api/races/{raceId}/registrations`
- `DELETE /api/races/{raceId}/registrations/me`
- `PATCH /api/races/{raceId}/registrations/{registrationId}/status`
