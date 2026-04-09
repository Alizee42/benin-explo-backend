# Bénin Explo — Backend

API REST du projet **Bénin Explo**, une plateforme de tourisme dédiée au Bénin. Gestion des circuits, hébergements, réservations, paiements PayPal et messagerie.

---

## Stack technique

| Technologie | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.11 |
| PostgreSQL | 15+ |
| Flyway | Migrations V1 → V19 |
| Spring Security + JWT | JJWT 0.12.6 |
| Cloudinary | Stockage des images |
| Spring Mail | Emails transactionnels |
| springdoc-openapi (Swagger) | Documentation API |

---

## Prérequis

- Java 21
- Maven 3.9+
- PostgreSQL 15+
- Compte [Cloudinary](https://cloudinary.com) (gratuit)
- Compte [PayPal Developer](https://developer.paypal.com) (optionnel en dev)

---

## Installation locale

### 1. Cloner le dépôt

```bash
git clone <url-du-repo>
cd benin-explo-backend
```

### 2. Créer la base de données

```sql
CREATE DATABASE benin_explo;
```

### 3. Configurer les variables d'environnement

Créer un fichier `.env` à la racine du projet :

```properties
# Base de données
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/benin_explo
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=ton_mot_de_passe

# JWT
JWT_SECRET=une_cle_secrete_longue_et_aleatoire_minimum_32_caracteres

# Cloudinary
CLOUDINARY_CLOUD_NAME=ton_cloud_name
CLOUDINARY_API_KEY=ta_cle_api
CLOUDINARY_API_SECRET=ton_secret_api
CLOUDINARY_FOLDER=benin-explo-dev

# Email (optionnel en dev)
CONTACT_MAIL_ENABLED=false
SPRING_MAIL_HOST=sandbox.smtp.mailtrap.io
SPRING_MAIL_PORT=2525
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=

# PayPal (optionnel en dev)
PAYPAL_CLIENT_ID=
PAYPAL_CLIENT_SECRET=
PAYPAL_MODE=sandbox
```

### 4. Lancer l'application

```bash
mvn spring-boot:run
```

L'API démarre sur `http://localhost:8080`.  
Flyway applique automatiquement les migrations au démarrage.

---

## Documentation API

Swagger UI disponible à l'adresse :

```
http://localhost:8080/swagger-ui.html
```

---

## Principales routes

| Méthode | Chemin | Description |
|---|---|---|
| `POST` | `/auth/register` | Inscription |
| `POST` | `/auth/login` | Connexion (retourne un JWT) |
| `GET` | `/api/circuits` | Liste des circuits |
| `GET` | `/api/hebergements` | Liste des hébergements |
| `GET` | `/api/actualites` | Actualités (public) |
| `POST` | `/api/contact` | Envoi du formulaire de contact |
| `POST` | `/api/reservations` | Réservation d'un circuit |
| `POST` | `/api/reservations-hebergement` | Réservation d'un hébergement |
| `POST` | `/api/paiements/circuit/paypal/create-order` | Paiement PayPal circuit |
| `POST` | `/api/media/upload` | Upload d'image (admin) |

Routes admin protégées par le rôle `ADMIN` — préfixe `/api/admin/*` et `/admin/*`.

---

## Migrations Flyway

Les migrations se trouvent dans `src/main/resources/db/migration/`.

| Version | Description |
|---|---|
| V1 | Schéma complet baseline |
| V2 | Données géographiques (pays, villes) |
| V16 | Tables paiements hébergements |
| V17 | Référence réservations hébergements |
| V18 | Tables paiements circuits & circuits personnalisés |
| V19 | Tables tombola & participations |

---

## Variables d'environnement (production)

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe PostgreSQL |
| `JWT_SECRET` | Clé secrète JWT (min. 32 caractères) |
| `CLOUDINARY_CLOUD_NAME` | Nom du cloud Cloudinary |
| `CLOUDINARY_API_KEY` | Clé API Cloudinary |
| `CLOUDINARY_API_SECRET` | Secret API Cloudinary |
| `CLOUDINARY_FOLDER` | Dossier de stockage (`benin-explo`) |
| `CONTACT_MAIL_ENABLED` | Activer l'envoi d'emails (`true`/`false`) |
| `SPRING_MAIL_HOST` | Serveur SMTP |
| `SPRING_MAIL_USERNAME` | Identifiant SMTP |
| `SPRING_MAIL_PASSWORD` | Mot de passe SMTP |
| `PAYPAL_CLIENT_ID` | Client ID PayPal |
| `PAYPAL_CLIENT_SECRET` | Secret PayPal |
| `PAYPAL_MODE` | `sandbox` ou `live` |
| `APP_BOOTSTRAP_ADMIN_EMAIL` | Email du premier compte admin |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | Mot de passe du premier compte admin |

---

## Déploiement

Le projet est configuré pour être déployé sur **Render** (ou tout serveur Java).

```bash
mvn clean package -DskipTests
java -jar target/benin-explo-backend-*.jar
```

Le port est configurable via la variable d'environnement `PORT` (défaut : `8080`).
