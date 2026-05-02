**# 🏁 RaceControl

**RaceControl** est une plateforme complète de gestion de ligues de simracing.
Elle permet aux organisateurs de créer, gérer et automatiser leurs championnats, tout en offrant aux pilotes une expérience claire, moderne et professionnelle.

---

## 🚀 Objectif

RaceControl vise à simplifier la gestion des ligues de simracing en centralisant :

- Gestion des ligues et saisons
- Organisation des courses
- Inscriptions pilotes
- Résultats et classements automatiques
- Système de pénalités et réclamations (protests)
- Communication entre organisateurs et pilotes

---

## 🧱 Stack technique

### Frontend
- Angular
- Tailwind CSS
- Preline UI

### Backend
- Java
- Spring Boot
- Spring Security (JWT)
- Hibernate / JPA
- MySQL
- Redis
- MinIO (S3 compatible)
- Flyway
- MapStruct

### DevOps
- Docker & Docker Compose

---

## 📦 Fonctionnalités principales

### 👥 Gestion des ligues
- Création de ligues publiques ou privées
- Gestion des membres et rôles :
  - Owner
  - Admin
  - Steward
  - Driver

---

### 🏆 Saisons & championnats
- Création de saisons
- Barème de points configurable
- Gestion des équipes
- Multi-catégories (évolution future)

---

### 📅 Calendrier des courses
- Planification des événements
- Format personnalisable (qualif, course, etc.)
- Gestion météo et paramètres

---

### 🏎️ Inscriptions pilotes
- Inscription aux courses
- Attribution voiture / numéro / équipe
- Liste principale + liste d’attente

---

### 📊 Résultats & classements
- Saisie manuelle ou import CSV
- Calcul automatique des points
- Classements :
  - Pilotes
  - Équipes
- Statistiques avancées

---

### ⚖️ Pénalités & réclamations
- Système de sanctions
- Dépôt de protest avec preuve (MinIO)
- Traitement par les stewards
- Impact automatique sur les résultats

---

### 🔔 Notifications
- Événements importants :
  - Validation d’inscription
  - Résultats publiés
  - Pénalités
  - Réclamations

---

### 🌐 Pages publiques
- Consultation d’une ligue sans compte
- Classements publics
- Résultats et calendrier

---

## 🏗️ Architecture du projet

```

/
├── frontend/          # Application Angular
├── backend/           # API Spring Boot
├── docker-compose.yml
├── .env.example
├── docs/
└── .github/

````

---

## ⚙️ Prérequis

- Node.js
- Java (version utilisée dans le projet)
- Docker & Docker Compose
- MySQL (si hors Docker)

---

## ▶️ Lancement du projet

### Avec Docker (recommandé)

```bash
cp .env.example .env
docker compose up --build
````

---

### Backend seul

```bash
cd backend
./mvnw spring-boot:run
```

---

### Frontend seul

```bash
cd frontend
npm install
npm start
```

---

## 🌍 URLs utiles (dev)

* Frontend : [http://localhost:4200](http://localhost:4200)
* Backend API : [http://localhost:8080](http://localhost:8080)
* Swagger : [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)
* MinIO : [http://localhost:9001](http://localhost:9001)
* Adminer : [http://localhost:8081](http://localhost:8081)

---

## 🔐 Authentification

### Exemple register

```bash
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
  "username": "driver1",
  "email": "driver1@mail.com",
  "password": "password"
}'
```

### Exemple login

```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "driver1@mail.com",
  "password": "password"
}'
```

---

## 📊 Exemple API

### Créer une ligue

```bash
POST /api/leagues
```

### Créer une saison

```bash
POST /api/leagues/{leagueId}/seasons
```

### Créer une course

```bash
POST /api/seasons/{seasonId}/races
```

### Voir les classements

```bash
GET /api/seasons/{seasonId}/standings
```

---

## 🧪 Tests

### Backend

```bash
cd backend
./mvnw test
```

### Frontend

```bash
cd frontend
npm test
```

---

## 📚 Documentation

Voir le dossier `docs/` :

* architecture.md
* api.md
* security.md
* deployment.md

---

## 🛠️ Variables d’environnement

Voir `.env.example` :

* DB_HOST
* DB_NAME
* DB_USER
* DB_PASSWORD
* REDIS_HOST
* MINIO_ACCESS_KEY
* MINIO_SECRET_KEY
* JWT_SECRET

---

## 📌 Roadmap

* Live telemetry
* WebSocket temps réel
* Export PDF / CSV
* Intégration APIs jeux (iRacing, ACC)
* Application mobile

---

## 🤝 Contribution

* Fork
* Branch feature
* Pull Request

---

## 📄 Licence

MIT

---

## 🏁 RaceControl

Une plateforme moderne pour gérer vos ligues de simracing comme un pro.
