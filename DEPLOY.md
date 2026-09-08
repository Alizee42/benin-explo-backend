# Déploiement backend — VPS IONOS + HTTPS (Caddy)

Ce guide met en place un reverse proxy [Caddy](https://caddyserver.com/) devant le backend Spring Boot,
qui obtient et renouvelle automatiquement un certificat Let's Encrypt. Objectif : ne plus exposer
le backend en HTTP brut sur le port 8081, et servir tout en HTTPS via un nom de domaine.

## Prérequis

- Un nom de domaine (ex: `api.tondomaine.com`) déjà acheté.
- Un enregistrement DNS de type **A** pour ce domaine pointant vers l'IP du VPS (`217.160.69.180`).
  → Vérifier la propagation avant de continuer : `nslookup api.tondomaine.com`
- Docker + Docker Compose installés sur le VPS.
- Ports **80** et **443** ouverts sur le pare-feu du VPS (obligatoires pour Let's Encrypt).

## Étapes

1. **Cloner/mettre à jour le repo sur le VPS**
   ```bash
   git clone https://github.com/Alizee42/benin-explo-backend.git
   cd benin-explo-backend
   ```

2. **Créer le fichier `.env`** à partir de `.env.example`, avec les vraies valeurs
   (notamment `API_DOMAIN=api.tondomaine.com` et tous les secrets de prod) :
   ```bash
   cp .env.example .env
   nano .env
   ```

3. **Arrêter l'ancien conteneur backend** s'il tourne encore en `docker run` manuel
   (celui exposé directement sur le port 8081) :
   ```bash
   docker ps
   docker stop <nom-ou-id-du-conteneur>
   docker rm <nom-ou-id-du-conteneur>
   ```

4. **Lancer la stack** (backend + Caddy) :
   ```bash
   docker compose up -d --build
   ```

5. **Vérifier les logs Caddy** pour confirmer l'obtention du certificat :
   ```bash
   docker compose logs -f caddy
   ```
   Un message `certificate obtained successfully` doit apparaître. Si erreur `too many redirects`
   ou `connection refused`, vérifier que le DNS pointe bien vers le VPS et que le port 80 est ouvert.

6. **Tester** :
   ```bash
   curl -I https://api.tondomaine.com/actuator/health
   ```
   Doit répondre `200 OK` avec un certificat valide (pas d'avertissement navigateur).

## Une fois le HTTPS validé côté backend

- Mettre à jour [`netlify.toml`](../Frontend-benin-explo/netlify.toml) : remplacer
  `http://217.160.69.180:8081` par `https://api.tondomaine.com` dans les 3 redirects
  (`/api/*`, `/auth/*`, `/uploads/*`).
- Redéployer le frontend sur Netlify.
- Vérifier en prod que le login, les réservations et le paiement PayPal fonctionnent bien
  de bout en bout (mixed-content résolu).

## Notes

- Le backend n'est plus exposé publiquement sur le port 8080/8081 — seul Caddy (80/443) l'est,
  et il route en interne vers `backend:8080` via le réseau Docker `internal`.
- Les uploads sont conservés dans le volume Docker nommé `uploads` (persistant entre redéploiements).
- Pour changer de domaine plus tard, il suffit de modifier `API_DOMAIN` dans `.env` et de relancer
  `docker compose up -d` — Caddy renégociera automatiquement un nouveau certificat.
