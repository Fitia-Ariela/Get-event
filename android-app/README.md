# GetEvent Mobile (Android)

Application Android (Jetpack Compose) connectee au backend Ktor:

Android App -> API -> Backend Ktor -> Fichiers JSON

## Ouvrir dans Android Studio

1. Ouvrir le dossier `android-app`.
2. Laisser Android Studio synchroniser Gradle.
3. Demarrer un emulateur Android.
4. Lancer l'application `app`.

## Configuration backend

1. **Demarrer le serveur** (obligatoire avant l'app) :
   ```powershell
   cd ..\ktor-getevent-backend
   .\gradlew.bat run
   ```
   Le serveur ecoute sur `http://0.0.0.0:8080` (visible depuis l'emulateur via `10.0.2.2`).

2. **URL API** (BuildConfig) :
   - Emulateur : `http://10.0.2.2:8080/` (par defaut)
   - Appareil physique : ajouter dans `local.properties` :
     ```
     api.base.url=http://192.168.x.x:8080
     ```
     (remplacer par l'IP LAN de votre PC)

3. **Compte admin par defaut** (apres premier demarrage du backend) :
   - Email : `admin@getevent.local`
   - Mot de passe : `admin123`

## Arborescence principale

- `app/api`: AuthApi, EventApi, ReservationApi, TicketApi, UserApi
- `app/model`: User, Event, Reservation, Ticket, Location
- `app/repository`: `GetEventRepository`
- `app/ui/login`, `app/ui/student`, `app/ui/boardmember`, `app/ui/admin`
- `app/navigation`
- `app/utils`

## Navigation par role

- `ADMIN` -> `AdminDashboard`
- `BOARD_MEMBER` -> `ScanTicketScreen` (via dashboard bureau)
- `STUDENT` -> `StudentHome`

## Ecrans inclus

- Etudiant: Login, Register, Home Event, Event Detail, Reserve Ticket, My Reservation, My Ticket QR, Profile
- Bureau: Dashboard, QR Scanner, Reservation List, Validate Ticket
- Admin: Dashboard, CRUD Event, CRUD Location, Manage Users, Reservation List, Statistics
