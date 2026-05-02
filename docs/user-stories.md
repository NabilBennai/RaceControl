Voici un découpage solide pour **SimLeague Manager**.

# MVP 1 — Base produit

## US-01 — Création de compte

**En tant que pilote**, je veux créer un compte pour rejoindre ou gérer des ligues.

### Fonctionnel

* Formulaire inscription : pseudo, email, mot de passe.
* Validation email unique.
* Mot de passe hashé.
* Rôle par défaut : `USER`.

### Backend

Entités :

* `User`
* `Role`

Endpoints :

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
GET /api/me
```

DTO :

* `RegisterRequest`
* `LoginRequest`
* `AuthResponse`
* `CurrentUserResponse`

### Frontend

Pages :

* `/register`
* `/login`
* `/dashboard`

Services :

* `AuthService`
* `TokenService`
* `AuthInterceptor`
* `AuthGuard`

---

## US-02 — Création d’une ligue

**En tant qu’utilisateur**, je veux créer une ligue pour organiser mes championnats.

### Fonctionnel

* Nom de ligue.
* Description.
* Jeu : F1, ACC, iRacing, LMU, etc.
* Visibilité : publique ou privée.
* Le créateur devient `OWNER`.

### Backend

Entités :

* `League`
* `LeagueMember`

Enums :

```java
LeagueVisibility { PUBLIC, PRIVATE }
LeagueRole { OWNER, ADMIN, STEWARD, DRIVER }
GamePlatform { F1, ACC, IRACING, LMU, RF2, OTHER }
```

Endpoints :

```http
POST /api/leagues
GET /api/leagues
GET /api/leagues/{leagueId}
PUT /api/leagues/{leagueId}
DELETE /api/leagues/{leagueId}
```

Règles :

* Seul `OWNER` peut supprimer la ligue.
* `OWNER` et `ADMIN` peuvent modifier.

### Frontend

Pages :

* `/leagues`
* `/leagues/new`
* `/leagues/:id`

Composants :

* `LeagueCard`
* `LeagueForm`
* `LeagueHeader`

---

## US-03 — Rejoindre une ligue

**En tant que pilote**, je veux rejoindre une ligue publique ou privée.

### Fonctionnel

* Ligue publique : demande d’inscription.
* Ligue privée : inscription via code d’invitation.
* Statut membre : `PENDING`, `APPROVED`, `REJECTED`.

### Backend

Entités :

* `LeagueJoinRequest`
* `LeagueInvitationCode`

Enums :

```java
MembershipStatus { PENDING, APPROVED, REJECTED, BANNED }
```

Endpoints :

```http
POST /api/leagues/{leagueId}/join
POST /api/leagues/join-by-code
GET /api/leagues/{leagueId}/members
PATCH /api/leagues/{leagueId}/members/{memberId}/approve
PATCH /api/leagues/{leagueId}/members/{memberId}/reject
```

### Frontend

Pages :

* `/leagues/:id/join`
* `/leagues/:id/members`

---

# MVP 2 — Championnats et saisons

## US-04 — Création d’une saison

**En tant qu’admin de ligue**, je veux créer une saison pour organiser un championnat.

### Fonctionnel

* Nom de saison.
* Date début/fin.
* Statut : brouillon, active, terminée.
* Une ligue peut avoir plusieurs saisons.

### Backend

Entité :

* `Season`

Enums :

```java
SeasonStatus { DRAFT, ACTIVE, FINISHED, ARCHIVED }
```

Endpoints :

```http
POST /api/leagues/{leagueId}/seasons
GET /api/leagues/{leagueId}/seasons
GET /api/seasons/{seasonId}
PUT /api/seasons/{seasonId}
PATCH /api/seasons/{seasonId}/activate
PATCH /api/seasons/{seasonId}/finish
```

Règles :

* Une seule saison active par ligue si tu veux simplifier.
* Seuls `OWNER` / `ADMIN` peuvent gérer.

### Frontend

Pages :

* `/leagues/:leagueId/seasons`
* `/seasons/:seasonId`

---

## US-05 — Système de points configurable

**En tant qu’admin**, je veux définir un barème de points personnalisé.

### Fonctionnel

Exemple :

* P1 = 25
* P2 = 18
* P3 = 15
* Fastest lap = +1
* Pole position = +1

### Backend

Entités :

* `PointSystem`
* `PointRule`

Enums :

```java
PointRuleType {
  FINISH_POSITION,
  POLE_POSITION,
  FASTEST_LAP,
  CLEAN_RACE,
  PARTICIPATION
}
```

Endpoints :

```http
POST /api/seasons/{seasonId}/point-system
GET /api/seasons/{seasonId}/point-system
PUT /api/point-systems/{pointSystemId}
```

Règles :

* Le barème est lié à une saison.
* Les points sont recalculables après modification.

### Frontend

Composants :

* `PointSystemEditor`
* `PointRuleTable`

---

## US-06 — Gestion des équipes

**En tant qu’admin**, je veux créer des équipes et affecter des pilotes.

### Fonctionnel

* Nom équipe.
* Couleur.
* Logo optionnel.
* Plusieurs pilotes par équipe.
* Classement équipes plus tard.

### Backend

Entités :

* `Team`
* `TeamMember`

Endpoints :

```http
POST /api/seasons/{seasonId}/teams
GET /api/seasons/{seasonId}/teams
PUT /api/teams/{teamId}
DELETE /api/teams/{teamId}
POST /api/teams/{teamId}/members
DELETE /api/teams/{teamId}/members/{memberId}
```

### Frontend

Pages :

* `/seasons/:seasonId/teams`

---

# MVP 3 — Calendrier de course

## US-07 — Création d’une course

**En tant qu’admin**, je veux créer une course dans le calendrier de la saison.

### Fonctionnel

* Circuit.
* Date.
* Heure.
* Format : qualification + course.
* Nombre de tours ou durée.
* Météo.
* Catégorie voiture.

### Backend

Entités :

* `Race`
* `Track`

Enums :

```java
RaceStatus { SCHEDULED, REGISTRATION_OPEN, LIVE, FINISHED, CANCELLED }
SessionType { PRACTICE, QUALIFYING, RACE }
WeatherType { DRY, WET, MIXED, DYNAMIC }
```

Endpoints :

```http
POST /api/seasons/{seasonId}/races
GET /api/seasons/{seasonId}/races
GET /api/races/{raceId}
PUT /api/races/{raceId}
DELETE /api/races/{raceId}
PATCH /api/races/{raceId}/status
```

### Frontend

Pages :

* `/seasons/:seasonId/calendar`
* `/races/:raceId`

Composants :

* `RaceCard`
* `RaceTimeline`
* `RaceStatusBadge`

---

## US-08 — Inscription à une course

**En tant que pilote**, je veux m’inscrire à une course.

### Fonctionnel

* Choix voiture.
* Numéro.
* Équipe si disponible.
* Statut inscription : confirmée, liste d’attente, refusée.

### Backend

Entité :

* `RaceRegistration`

Enums :

```java
RegistrationStatus { CONFIRMED, WAITLISTED, CANCELLED, REJECTED }
```

Endpoints :

```http
POST /api/races/{raceId}/registrations
GET /api/races/{raceId}/registrations
DELETE /api/races/{raceId}/registrations/me
PATCH /api/races/{raceId}/registrations/{registrationId}/status
```

Règles :

* Un pilote ne peut s’inscrire qu’une seule fois.
* Seuls les membres approuvés de la ligue peuvent s’inscrire.

### Frontend

Composants :

* `RaceRegistrationButton`
* `RegistrationList`

---

# MVP 4 — Résultats et classements

## US-09 — Saisie manuelle des résultats

**En tant qu’admin ou steward**, je veux saisir les résultats d’une course.

### Fonctionnel

* Position.
* Pilote.
* Temps total.
* Meilleur tour.
* Pole position.
* DNF / DSQ.
* Incidents.

### Backend

Entités :

* `RaceResult`
* `RaceResultLine`

Enums :

```java
ResultStatus { CLASSIFIED, DNF, DNS, DSQ }
```

Endpoints :

```http
POST /api/races/{raceId}/results
GET /api/races/{raceId}/results
PUT /api/races/{raceId}/results
DELETE /api/races/{raceId}/results
```

Règles :

* Les résultats déclenchent le calcul des points.
* Si une pénalité existe, le classement doit être recalculé.

### Frontend

Pages :

* `/races/:raceId/results/edit`
* `/races/:raceId/results`

Composants :

* `ResultEditorTable`
* `ResultStatusSelect`

---

## US-10 — Import CSV de résultats

**En tant qu’admin**, je veux importer un fichier CSV de résultats pour gagner du temps.

### Fonctionnel

* Upload fichier.
* Preview avant validation.
* Mapping colonnes.
* Rapport d’erreurs.

### Backend

Entités :

* `ResultImport`
* `ResultImportError`

Stockage :

* Fichier original dans MinIO.

Endpoints :

```http
POST /api/races/{raceId}/results/import
GET /api/imports/{importId}
POST /api/imports/{importId}/confirm
```

Règles :

* Aucun résultat n’est sauvegardé avant confirmation.
* Les erreurs doivent être lisibles.

### Frontend

Pages :

* `/races/:raceId/results/import`

Étapes UI :

1. Upload
2. Preview
3. Correction
4. Confirmation

---

## US-11 — Calcul automatique des classements

**En tant que pilote**, je veux voir le classement automatiquement mis à jour.

### Fonctionnel

Classements :

* pilotes
* équipes
* catégories

Données :

* points
* victoires
* podiums
* poles
* fastest laps
* pénalités

### Backend

Entités :

* `DriverStanding`
* `TeamStanding`

Service :

* `StandingCalculationService`

Endpoints :

```http
GET /api/seasons/{seasonId}/standings/drivers
GET /api/seasons/{seasonId}/standings/teams
POST /api/seasons/{seasonId}/standings/recalculate
```

Optimisation :

* Redis pour cache classement.

Règles :

* Recalcul après :

  * résultat publié
  * pénalité modifiée
  * barème modifié

### Frontend

Pages :

* `/seasons/:seasonId/standings`

Composants :

* `DriverStandingTable`
* `TeamStandingTable`

---

# MVP 5 — Pénalités et réclamations

## US-12 — Création d’une pénalité

**En tant que steward**, je veux ajouter une pénalité à un pilote.

### Fonctionnel

Types :

* avertissement
* +5 secondes
* +10 secondes
* drive-through converti
* retrait de points
* disqualification

### Backend

Entité :

* `Penalty`

Enums :

```java
PenaltyType {
  WARNING,
  TIME_PENALTY,
  POINTS_DEDUCTION,
  POSITION_DROP,
  DISQUALIFICATION
}
```

Endpoints :

```http
POST /api/races/{raceId}/penalties
GET /api/races/{raceId}/penalties
PUT /api/penalties/{penaltyId}
DELETE /api/penalties/{penaltyId}
```

Règles :

* Une pénalité impacte les résultats.
* Toute modification recalcule le classement.

### Frontend

Pages :

* `/races/:raceId/stewarding`

---

## US-13 — Déposer une réclamation

**En tant que pilote**, je veux déposer une réclamation après une course.

### Fonctionnel

* Course concernée.
* Pilote accusé.
* Tour/minute.
* Description.
* Upload preuve vidéo/image.
* Statut : ouvert, en revue, accepté, rejeté.

### Backend

Entités :

* `Protest`
* `ProtestEvidence`

Enums :

```java
ProtestStatus { OPEN, UNDER_REVIEW, ACCEPTED, REJECTED, CANCELLED }
```

Endpoints :

```http
POST /api/races/{raceId}/protests
GET /api/races/{raceId}/protests
GET /api/protests/{protestId}
PATCH /api/protests/{protestId}/status
POST /api/protests/{protestId}/evidence
```

Stockage :

* MinIO pour preuves.

### Frontend

Pages :

* `/races/:raceId/protests`
* `/protests/:protestId`

---

## US-14 — Décision steward

**En tant que steward**, je veux traiter une réclamation.

### Fonctionnel

* Voir les preuves.
* Accepter/refuser.
* Ajouter commentaire.
* Créer une pénalité liée.

### Backend

Entité :

* `StewardDecision`

Endpoints :

```http
POST /api/protests/{protestId}/decision
GET /api/protests/{protestId}/decision
```

Règles :

* Une protest acceptée peut générer une pénalité.
* Décision visible par les membres de la ligue.

---

# MVP 6 — Communication

## US-15 — Annonces de ligue

**En tant qu’admin**, je veux publier des annonces pour ma ligue.

### Fonctionnel

* Titre.
* Contenu.
* Important ou non.
* Visible sur dashboard.

### Backend

Entité :

* `Announcement`

Endpoints :

```http
POST /api/leagues/{leagueId}/announcements
GET /api/leagues/{leagueId}/announcements
PUT /api/announcements/{announcementId}
DELETE /api/announcements/{announcementId}
```

### Frontend

Composants :

* `AnnouncementCard`
* `AnnouncementEditor`

---

## US-16 — Notifications

**En tant qu’utilisateur**, je veux recevoir des notifications importantes.

### Fonctionnel

Notifications pour :

* inscription validée
* nouvelle course
* résultat publié
* pénalité reçue
* protest traitée

### Backend

Entité :

* `Notification`

Endpoints :

```http
GET /api/notifications
PATCH /api/notifications/{id}/read
PATCH /api/notifications/read-all
```

Redis optionnel :

* cache compteur non lu.

### Frontend

Composants :

* `NotificationBell`
* `NotificationDropdown`

---

# MVP 7 — Pages publiques

## US-17 — Page publique de ligue

**En tant que visiteur**, je veux consulter une ligue publique sans compte.

### Fonctionnel

Visible :

* présentation ligue
* calendrier
* classements
* résultats
* équipes

### Backend

Endpoints publics :

```http
GET /api/public/leagues/{slug}
GET /api/public/leagues/{slug}/seasons
GET /api/public/seasons/{seasonId}/standings
GET /api/public/races/{raceId}/results
```

### Frontend

Pages :

* `/public/leagues/:slug`
* `/public/seasons/:id/standings`

---

# MVP 8 — Administration avancée

## US-18 — Gestion des rôles dans une ligue

**En tant qu’owner**, je veux attribuer des rôles aux membres.

### Fonctionnel

Rôles :

* Owner
* Admin
* Steward
* Driver

### Backend

Endpoints :

```http
PATCH /api/leagues/{leagueId}/members/{memberId}/role
DELETE /api/leagues/{leagueId}/members/{memberId}
```

Règles :

* Un owner ne peut pas se retirer s’il est le seul owner.
* Un steward ne peut pas modifier les rôles.

---

## US-19 — Paramètres de ligue

**En tant qu’admin**, je veux configurer les règles générales.

### Fonctionnel

* Inscriptions ouvertes/fermées.
* Protest autorisée pendant X heures.
* Nombre max pilotes par course.
* Barème actif.
* Tie-breakers.

### Backend

Entité :

* `LeagueSettings`

Endpoints :

```http
GET /api/leagues/{leagueId}/settings
PUT /api/leagues/{leagueId}/settings
```

---

# Roadmap avancée

## US-20 — Export CSV/PDF

**En tant qu’admin**, je veux exporter les classements et résultats.

Backend :

```http
GET /api/seasons/{seasonId}/standings/export.csv
GET /api/races/{raceId}/results/export.csv
```

---

## US-21 — Calendrier iCal

**En tant que pilote**, je veux synchroniser les courses avec mon calendrier.

Backend :

```http
GET /api/seasons/{seasonId}/calendar.ics
GET /api/users/me/calendar.ics
```

---

## US-22 — Multi-championnats dans une saison

**En tant qu’admin**, je veux gérer plusieurs catégories dans une même saison.

Exemples :

* GT3 Pro
* GT3 Silver
* F1 Division 1
* F1 Division 2

Entités :

* `Championship`
* `Category`

---

## US-23 — Licences pilotes

**En tant qu’organisateur**, je veux classer les pilotes par niveau.

Exemples :

* Rookie
* Bronze
* Silver
* Gold
* Pro

Entité :

* `DriverLicense`

---

# Ordre de développement conseillé

1. Auth
2. Ligues
3. Membres
4. Saisons
5. Courses
6. Inscriptions
7. Résultats manuels
8. Barème de points
9. Classements
10. Pénalités
11. Protests
12. Notifications
13. Pages publiques
14. Import CSV
15. Exports

Ce découpage te donne un projet réaliste, vendable, et assez complet pour exploiter tout ton template.
