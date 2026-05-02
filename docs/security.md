# Sécurité

- Auth stateless avec JWT access/refresh
- Filtre `JwtAuthenticationFilter` pour authentifier les routes protégées
- Spring Security avec `SessionCreationPolicy.STATELESS`
- CORS activé et configurable via `CORS_ALLOWED_ORIGINS`
- Blacklist des refresh tokens dans Redis (`bl:<token>`)
- Endpoints publics: `/api/auth/**`, `/actuator/health`
- Endpoint protégé: `/api/me`
- Erreurs API uniformisées via `GlobalExceptionHandler`
