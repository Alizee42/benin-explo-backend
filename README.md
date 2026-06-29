# Bénin Explo — Backend

API REST Spring Boot pour la plateforme de tourisme **Bénin Explo**. Gère les circuits, hébergements, réservations, paiements PayPal, stockage d'images Cloudinary, emails transactionnels et authentification JWT.

## Stack technique

| Technologie | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.11 |
| Spring Security + JWT (JJWT) | 0.12.6 |
| PostgreSQL | 15+ |
| Flyway | Migrations V1 → V19 |
| Cloudinary SDK | 1.39.0 |
| Spring Mail | (géré par Boot) |
| springdoc-openapi (Swagger UI) | 2.6.0 |
| Build | Maven (wrapper inclus) |

## Prérequis

- Java 21
- Maven 3.9+
- PostgreSQL 15+
- Compte [Cloudinary](https://cloudinary.com) (gratuit, requis pour les uploads d'images)
- Compte [PayPal Developer](https://developer.paypal.com) (optionnel en développement)

## Installation et lancement

```bash
git clone <url-du-repo>
cd benin-explo-backend

# 1. Créer la base de données
psql -U postgres -c "CREATE DATABASE benin_explo;"

# 2. Créer un fichier .env à la racine du projet
```

Contenu du fichier `.env` :

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/benin_explo
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=ton_mot_de_passe

JWT_SECRET=une_cle_secrete_longue_et_aleatoire_minimum_32_caracteres

CLOUDINARY_CLOUD_NAME=ton_cloud_name
CLOUDINARY_API_KEY=ta_cle_api
CLOUDINARY_API_SECRET=ton_secret_api
CLOUDINARY_FOLDER=benin-explo-dev

# Optionnel en développement
CONTACT_MAIL_ENABLED=false
PAYPAL_ENABLED=false
PAYPAL_CLIENT_ID=
PAYPAL_CLIENT_SECRET=
PAYPAL_MODE=sandbox

APP_BOOTSTRAP_ADMIN_EMAIL=admin@example.com
APP_BOOTSTRAP_ADMIN_PASSWORD=motdepasse_admin
```

```bash
# 3. Lancer l'application
mvn spring-boot:run
# API disponible sur http://localhost:8080
```

Flyway applique automatiquement toutes les migrations au démarrage.

## Documentation API

Swagger UI disponible sur : `http://localhost:8080/swagger-ui.html`

## Principaux endpoints

| Méthode | Route | Accès | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Inscription |
| `POST` | `/auth/login` | Public | Connexion — retourne un JWT |
| `GET` | `/api/circuits` | Public | Liste des circuits |
| `GET` | `/api/hebergements` | Public | Liste des hébergements |
| `GET` | `/api/actualites` | Public | Actualités |
| `POST` | `/api/contact` | Public | Formulaire de contact |
| `POST` | `/api/reservations` | Auth | Réservation d'un circuit |
| `POST` | `/api/reservations-hebergement` | Auth | Réservation d'un hébergement |
| `POST` | `/api/paiements/circuit/paypal/create-order` | Auth | Paiement PayPal circuit |
| `POST` | `/api/media/upload` | Admin | Upload d'image |

Routes admin protégées par le rôle `ADMIN` — préfixe `/api/admin/*`.

## Migrations Flyway

| Version | Description |
|---|---|
| V1 | Schéma complet baseline |
| V2 | Données géographiques (pays, villes) |
| V16 | Tables paiements hébergements |
| V17 | Référence réservations hébergements |
| V18 | Tables paiements circuits et circuits personnalisés |
| V19 | Tables tombola et participations |

## Variables d'environnement (production)

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | Credentials PostgreSQL |
| `JWT_SECRET` | Clé secrète JWT (min. 32 caractères) |
| `CLOUDINARY_CLOUD_NAME` / `API_KEY` / `API_SECRET` | Credentials Cloudinary |
| `CLOUDINARY_FOLDER` | Dossier de stockage (`benin-explo`) |
| `CONTACT_MAIL_ENABLED` | Activer l'envoi d'emails (`true` / `false`) |
| `SPRING_MAIL_HOST` / `USERNAME` / `PASSWORD` | Serveur SMTP |
| `PAYPAL_CLIENT_ID` / `CLIENT_SECRET` | Credentials PayPal |
| `PAYPAL_MODE` | `sandbox` ou `live` |
| `APP_BOOTSTRAP_ADMIN_EMAIL` / `PASSWORD` | Premier compte administrateur |

## Déploiement

```bash
mvn clean package -DskipTests
java -jar target/benin-explo-backend-*.jar
```

Le port est configurable via la variable `PORT` (défaut : `8080`). Compatible Render, Railway ou tout serveur Java.
