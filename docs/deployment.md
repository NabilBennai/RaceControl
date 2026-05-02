# Déploiement

- Développement local: Docker Compose (`docker compose up -d --build`)
- CI: GitHub Actions (`frontend-ci`, `backend-ci`, `docker-ci`, `security`)
- Build backend reproductible: Maven Wrapper (`./mvnw -f backend/pom.xml ...`)
- Services Docker exposés en local:
  - MySQL: `3306`
  - Redis: `6379`
  - Adminer: `8081`
  - MinIO API: `9000`
  - MinIO Console: `9001`
- Déploiement cible adaptable (GHCR/Kubernetes ou autre infra).
