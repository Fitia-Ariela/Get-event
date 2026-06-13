# GetEvent Backend

API REST Ktor pour la plateforme de gestion des événements de la semaine de la télécommunication.

## Démarrage

```bash
./gradlew run
```

Le serveur écoute sur `http://localhost:8080` (interface `0.0.0.0`, accessible depuis l’émulateur Android). Les données sont persistées dans `data/store.json`.

### Client Android (émulateur)

- URL backend : `http://10.0.2.2:8080/` (`10.0.2.2` = localhost de la machine hôte).
- Les routes publiques (`POST /api/auth/register`, `POST /api/auth/login`, `GET /api/events`, etc.) ne nécessitent pas de JWT.
- L’erreur *« CLEARTEXT communication to 10.0.2.2 not permitted »* vient de la **politique réseau Android** (HTTP non chiffré), pas du serveur Ktor. L’app mobile doit autoriser le cleartext en dev via `network_security_config.xml` (voir le projet `android-app`).

## Compte administrateur par défaut

| Champ | Valeur |
|-------|--------|
| Email | `admin@getevent.local` |
| Mot de passe | `admin123` |

## Rôles

| Rôle | Droits |
|------|--------|
| `STUDENT` | Voir les événements, réserver, payer (événements privés), consulter ses tickets |
| `BOARD_MEMBER` | Voir les listes d'étudiants, statistiques et événements (lecture seule) |
| `ADMIN` | CRUD complet sur événements, lieux et utilisateurs |

## Flux métier

1. **Événement public** : l'étudiant réserve → réservation confirmée → ticket généré automatiquement.
2. **Événement privé** : l'étudiant réserve → réservation en attente → paiement via `POST /api/transactions/pay` → ticket généré.

## Endpoints principaux

### Auth
- `POST /api/auth/login` — Connexion (retourne un JWT)
- `POST /api/auth/register` — Inscription étudiant (public)
- `GET /api/auth/me` — Profil connecté (JWT requis)

### Événements & lieux
- `GET /api/events` — Liste des événements (public)
- `GET /api/events/{id}` — Détail
- `POST /api/events` — Créer (ADMIN, JWT)
- `GET /api/locations` — Liste des lieux
- `POST /api/locations` — Créer un lieu (ADMIN)

### Réservations
- `POST /api/reservations` — Réserver (STUDENT)
- `GET /api/reservations/me` — Mes réservations
- `GET /api/reservations` — Toutes (ADMIN / BOARD_MEMBER)

### Paiements & tickets
- `POST /api/transactions/pay` — Payer un événement privé (STUDENT)
- `GET /api/tickets/reservation/{reservationId}` — Ticket d'une réservation

### Utilisateurs
- `GET /api/users` — Liste (ADMIN / BOARD_MEMBER)
- `GET /api/users/stats` — Nombre d'étudiants par rôle

## Authentification

Envoyer le header sur les routes protégées :

```
Authorization: Bearer <token>
```

## Structure du projet

```
├── model/          # Entités (diagramme UML)
├── dto/            # Requêtes / réponses API
├── datastore/      # MemoryStore + JSON
├── repository/     # Accès aux données
├── service/        # Logique métier
├── controller/     # Handlers HTTP
├── routes/         # Définition des routes
├── security/       # JWT
└── di/             # Injection de dépendances Ktor
```
