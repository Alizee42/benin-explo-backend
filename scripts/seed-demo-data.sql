-- Jeu de donnees de demonstration COMPLET pour environnement de test/local.
-- Script autonome, PAS une migration Flyway : a lancer/annuler a la main quand besoin.
-- Usage : psql -h localhost -U postgres -d benin_explo -f scripts/seed-demo-data.sql
-- Pour retirer ces donnees : scripts/cleanup-demo-data.sql
-- Idempotent (WHERE NOT EXISTS) : peut etre rejoue plusieurs fois sans dupliquer.
-- S'appuie sur les zones/villes deja creees par V2__seed_geo_reference_data.sql (migration officielle).
--
-- Contenu : categories, activites (9 villes couvertes), hebergements, circuits,
-- actualites, 2 utilisateurs clients de test, reservations circuit/hebergement et
-- une demande de circuit personnalise, avec des statuts varies pour tester le
-- flux admin (EN_ATTENTE / CONFIRMEE / ANNULEE / etc.).
--
-- Ne cree pas de compte admin : utiliser app.bootstrap.admin.* (voir DataInitializer) pour ca.
--
-- Comptes clients de test crees ici (mot de passe identique pour les deux) :
--   client1.demo@beninexplo.local / Test1234!
--   client2.demo@beninexplo.local / Test1234!
-- (hash BCrypt genere avec le meme PasswordEncoder que l'application)

-- ============================================================
-- 1. categories_activites
-- ============================================================
INSERT INTO categories_activites (nom, description, created_at, updated_at)
SELECT 'Nature', 'Randonnees, parcs et decouvertes naturelles', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM categories_activites WHERE nom = 'Nature');

INSERT INTO categories_activites (nom, description, created_at, updated_at)
SELECT 'Culture', 'Sites historiques, musees et traditions locales', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM categories_activites WHERE nom = 'Culture');

INSERT INTO categories_activites (nom, description, created_at, updated_at)
SELECT 'Aventure', 'Activites sportives et sensations fortes', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM categories_activites WHERE nom = 'Aventure');

INSERT INTO categories_activites (nom, description, created_at, updated_at)
SELECT 'Detente', 'Plages, lagunes et moments de repos', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM categories_activites WHERE nom = 'Detente');

-- ============================================================
-- 2. activites (2-3 par ville, reparties sur les 3 zones)
-- ============================================================

-- --- Cotonou (Sud) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Visite du marche Dantokpa', 'ACTIVITE', 'Le plus grand marche a ciel ouvert d''Afrique de l''Ouest.',
       v.id_ville, 120, 5000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Cotonou' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Visite du marche Dantokpa');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Balade en pirogue sur la lagune', 'ACTIVITE', 'Decouverte de la lagune de Cotonou et des villages lacustres en pirogue traditionnelle.',
       v.id_ville, 150, 9000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Cotonou' AND c.nom = 'Detente'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Balade en pirogue sur la lagune');

-- --- Porto-Novo (Sud) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Musee ethnographique de Porto-Novo', 'ACTIVITE', 'Collection d''objets et de masques retracant l''histoire des royaumes du Benin.',
       v.id_ville, 90, 3000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Porto-Novo' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Musee ethnographique de Porto-Novo');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Visite du lac Nokoue et village d''Ganvie', 'ACTIVITE', 'La "Venise de l''Afrique" : village lacustre construit sur pilotis.',
       v.id_ville, 180, 12000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Porto-Novo' AND c.nom = 'Nature'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Visite du lac Nokoue et village d''Ganvie');

-- --- Ouidah (Sud) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Route des esclaves', 'ACTIVITE', 'Parcours memoriel retracant l''histoire de la traite negriere.',
       v.id_ville, 180, 8000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Ouidah' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Route des esclaves');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Foret sacree de Kpasse', 'ACTIVITE', 'Foret sacree emblematique du culte vodoun.',
       v.id_ville, 90, 4000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Ouidah' AND c.nom = 'Nature'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Foret sacree de Kpasse');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Plage de Ouidah', 'ACTIVITE', 'Detente en bord d''ocean Atlantique, acces direct depuis la ville.',
       v.id_ville, 120, 0, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Ouidah' AND c.nom = 'Detente'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Plage de Ouidah');

-- --- Abomey-Calavi (Sud) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Universite et campus d''Abomey-Calavi', 'ACTIVITE', 'Decouverte du plus grand campus universitaire du pays et de son quartier animes.',
       v.id_ville, 90, 2000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Abomey-Calavi' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Universite et campus d''Abomey-Calavi');

-- --- Abomey (Centre) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Palais royaux d''Abomey', 'ACTIVITE', 'Site UNESCO, ancienne capitale du royaume du Dahomey.',
       v.id_ville, 150, 10000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Abomey' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Palais royaux d''Abomey');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Atelier artisanat de bronze', 'ACTIVITE', 'Initiation aux techniques traditionnelles de fonte du bronze avec les artisans locaux.',
       v.id_ville, 120, 15000, 'Moyen', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Abomey' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Atelier artisanat de bronze');

-- --- Dassa-Zoume (Centre) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Grottes sacrees de Dassa-Zoume', 'ACTIVITE', 'Site religieux et naturel au coeur des collines du centre du pays.',
       v.id_ville, 150, 6000, 'Moyen', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Dassa-Zoume' AND c.nom = 'Nature'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Grottes sacrees de Dassa-Zoume');

-- --- Parakou (Nord) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Marche central de Parakou', 'ACTIVITE', 'Grand carrefour commercial du nord du Benin, artisanat et produits locaux.',
       v.id_ville, 90, 3000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Parakou' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Marche central de Parakou');

-- --- Natitingou (Nord) ---
INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Randonnee dans les montagnes de l''Atacora', 'ACTIVITE', 'Randonnee au coeur du massif montagneux du nord.',
       v.id_ville, 240, 12000, 'Difficile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Natitingou' AND c.nom = 'Aventure'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Randonnee dans les montagnes de l''Atacora');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Safari au parc de la Pendjari', 'ACTIVITE', 'Observation de la faune sauvage (elephants, lions, antilopes).',
       v.id_ville, 300, 25000, 'Moyen', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Natitingou' AND c.nom = 'Nature'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Safari au parc de la Pendjari');

INSERT INTO activites (nom, type, description, ville_id, duree_interne, poids, difficulte, categorie_id, created_at, updated_at)
SELECT 'Visite des tata somba', 'ACTIVITE', 'Decouverte des habitations traditionnelles fortifiees, classees au patrimoine.',
       v.id_ville, 120, 7000, 'Facile', c.id_categorie, now(), now()
FROM villes v, categories_activites c
WHERE v.nom = 'Natitingou' AND c.nom = 'Culture'
  AND NOT EXISTS (SELECT 1 FROM activites WHERE nom = 'Visite des tata somba');

-- ============================================================
-- 3. hebergements (gamme de prix variee)
-- ============================================================
INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, created_at, updated_at)
SELECT 'Hotel du Lac', 'Hotel', 'Cotonou', 'Haie Vive', 'Hotel confortable en bord de lagune, proche du centre-ville.', 35000, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM hebergements WHERE nom = 'Hotel du Lac');

INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, created_at, updated_at)
SELECT 'Residence Les Cocotiers', 'Residence', 'Cotonou', 'Fidjrosse', 'Appartements meubles proches de la plage, ideal sejour prolonge.', 45000, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM hebergements WHERE nom = 'Residence Les Cocotiers');

INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, created_at, updated_at)
SELECT 'Auberge de Ouidah', 'Auberge', 'Ouidah', 'Centre-ville', 'Auberge familiale a deux pas de la route des esclaves.', 18000, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM hebergements WHERE nom = 'Auberge de Ouidah');

INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, created_at, updated_at)
SELECT 'Case Royale d''Abomey', 'Maison d''hotes', 'Abomey', 'Quartier des palais', 'Maison d''hotes traditionnelle a cote des palais royaux.', 22000, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM hebergements WHERE nom = 'Case Royale d''Abomey');

INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, created_at, updated_at)
SELECT 'Hotel des Collines', 'Hotel', 'Dassa-Zoume', 'Centre-ville', 'Hotel simple et propre au pied des collines sacrees.', 20000, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM hebergements WHERE nom = 'Hotel des Collines');

INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, created_at, updated_at)
SELECT 'Lodge Pendjari Safari', 'Lodge', 'Natitingou', 'Entree du parc', 'Lodge en pleine nature aux portes du parc national de la Pendjari.', 55000, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM hebergements WHERE nom = 'Lodge Pendjari Safari');

-- ============================================================
-- 4. circuits (actifs, prets a etre reserves)
-- ============================================================
INSERT INTO circuits (nom, description, resume, duree_indicative, prix_indicatif, formule_proposee, actif, ville_id, points_forts, inclus, non_inclus, created_at, updated_at)
SELECT 'Decouverte du Sud Benin', 'Circuit de 3 jours entre Cotonou, Ouidah et Porto-Novo, entre histoire et culture vodoun.',
       'Cotonou, Ouidah, Porto-Novo en 3 jours', '3 jours / 2 nuits', 120000, 'Circuit accompagne', true, v.id_ville,
       'Route des esclaves, marche Dantokpa, village lacustre de Ganvie',
       'Transport, guide, hebergement 2 nuits, petit-dejeuner',
       'Repas du midi et du soir, depenses personnelles',
       now(), now()
FROM villes v
WHERE v.nom = 'Cotonou'
  AND NOT EXISTS (SELECT 1 FROM circuits WHERE nom = 'Decouverte du Sud Benin');

INSERT INTO circuits (nom, description, resume, duree_indicative, prix_indicatif, formule_proposee, actif, ville_id, points_forts, inclus, non_inclus, created_at, updated_at)
SELECT 'Route royale d''Abomey', 'Circuit culturel de 2 jours a la decouverte des palais royaux et de l''artisanat local.',
       'Palais royaux et artisanat, 2 jours', '2 jours / 1 nuit', 80000, 'Circuit accompagne', true, v.id_ville,
       'Palais royaux UNESCO, atelier de bronze, marche artisanal',
       'Transport, guide, hebergement 1 nuit, petit-dejeuner',
       'Repas du midi et du soir, entrees sur les sites',
       now(), now()
FROM villes v
WHERE v.nom = 'Abomey'
  AND NOT EXISTS (SELECT 1 FROM circuits WHERE nom = 'Route royale d''Abomey');

INSERT INTO circuits (nom, description, resume, duree_indicative, prix_indicatif, formule_proposee, actif, ville_id, points_forts, inclus, non_inclus, created_at, updated_at)
SELECT 'Safari et montagnes du Nord', 'Circuit aventure de 4 jours entre randonnee dans l''Atacora et safari a la Pendjari.',
       'Randonnee et safari, 4 jours', '4 jours / 3 nuits', 220000, 'Circuit accompagne avec guide', true, v.id_ville,
       'Safari au parc de la Pendjari, tata somba, randonnee en montagne',
       'Transport 4x4, guide, hebergement 3 nuits, tous les repas',
       'Depenses personnelles, pourboires',
       now(), now()
FROM villes v
WHERE v.nom = 'Natitingou'
  AND NOT EXISTS (SELECT 1 FROM circuits WHERE nom = 'Safari et montagnes du Nord');

INSERT INTO circuits (nom, description, resume, duree_indicative, prix_indicatif, formule_proposee, actif, ville_id, points_forts, inclus, non_inclus, created_at, updated_at)
SELECT 'Escapade lagunaire', 'Circuit court de 1 jour autour de Porto-Novo et du lac Nokoue.',
       'Porto-Novo et lac Nokoue en 1 jour', '1 jour', 45000, 'Excursion a la journee', true, v.id_ville,
       'Village lacustre de Ganvie, musee ethnographique',
       'Transport, guide, dejeuner',
       'Boissons, depenses personnelles',
       now(), now()
FROM villes v
WHERE v.nom = 'Porto-Novo'
  AND NOT EXISTS (SELECT 1 FROM circuits WHERE nom = 'Escapade lagunaire');

INSERT INTO circuits (nom, description, resume, duree_indicative, prix_indicatif, formule_proposee, actif, ville_id, points_forts, inclus, non_inclus, created_at, updated_at)
SELECT 'Collines sacrees et artisanat du Centre', 'Circuit de 2 jours entre Dassa-Zoume et Abomey, sites naturels et artisanat.',
       'Dassa-Zoume et Abomey en 2 jours', '2 jours / 1 nuit', 90000, 'Circuit accompagne', true, v.id_ville,
       'Grottes sacrees, palais royaux, ateliers d''artisanat',
       'Transport, guide, hebergement 1 nuit, petit-dejeuner',
       'Repas du midi et du soir',
       now(), now()
FROM villes v
WHERE v.nom = 'Dassa-Zoume'
  AND NOT EXISTS (SELECT 1 FROM circuits WHERE nom = 'Collines sacrees et artisanat du Centre');

-- ============================================================
-- 5. actualites
-- ============================================================
INSERT INTO actualite (titre, contenu, resume, date_publication, a_la_une, publiee, created_at, updated_at)
SELECT 'Ouverture de la saison des safaris a la Pendjari', 'La saison seche est ideale pour observer la faune du parc national de la Pendjari. Reservez des maintenant votre safari accompagne.', 'La saison des safaris demarre au parc de la Pendjari.', now(), true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM actualite WHERE titre = 'Ouverture de la saison des safaris a la Pendjari');

INSERT INTO actualite (titre, contenu, resume, date_publication, a_la_une, publiee, created_at, updated_at)
SELECT 'Nouveau circuit : Collines sacrees et artisanat du Centre', 'Nous lancons un nouveau circuit de 2 jours au coeur du Benin, entre Dassa-Zoume et Abomey.', 'Un nouveau circuit de 2 jours dans le centre du pays.', now(), false, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM actualite WHERE titre = 'Nouveau circuit : Collines sacrees et artisanat du Centre');

INSERT INTO actualite (titre, contenu, resume, date_publication, a_la_une, publiee, created_at, updated_at)
SELECT 'Conseils pour bien preparer son voyage au Benin', 'Vaccins recommandes, meilleure periode pour voyager, ce qu''il faut savoir avant de partir.', 'Nos conseils pratiques avant de partir au Benin.', now(), false, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM actualite WHERE titre = 'Conseils pour bien preparer son voyage au Benin');

-- ============================================================
-- 6. utilisateurs de test (clients, pas admin)
-- Mot de passe pour les deux : Test1234!  (hash BCrypt genere avec le PasswordEncoder de l'app)
-- ============================================================
INSERT INTO utilisateurs (nom, prenom, email, telephone, mot_de_passe, role, date_creation, created_at, updated_at)
SELECT 'Dupont', 'Marie', 'client1.demo@beninexplo.local', '+33 6 00 00 00 01',
       '$2a$10$AG485Cjsepyu7i4Pqhf/kegivIsZ3E7Tc9q5gByVvlAUDIgYB6R8a', 'CLIENT', now(), now(), now()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'client1.demo@beninexplo.local');

INSERT INTO utilisateurs (nom, prenom, email, telephone, mot_de_passe, role, date_creation, created_at, updated_at)
SELECT 'Martin', 'Luc', 'client2.demo@beninexplo.local', '+33 6 00 00 00 02',
       '$2a$10$AG485Cjsepyu7i4Pqhf/kegivIsZ3E7Tc9q5gByVvlAUDIgYB6R8a', 'CLIENT', now(), now(), now()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'client2.demo@beninexplo.local');

-- ============================================================
-- 7. reservations de circuit (statuts varies pour tester le flux admin)
-- ============================================================
INSERT INTO reservations (nom, prenom, email, telephone, date_reservation, statut, nombre_personnes, commentaires, reference_reservation, circuit_id, utilisateur_id, created_at, updated_at)
SELECT 'Dupont', 'Marie', 'client1.demo@beninexplo.local', '+33 6 00 00 00 01', CURRENT_DATE + INTERVAL '15 days', 'EN_ATTENTE', 2,
       'Nous souhaitons un guide francophone si possible.', 'DEMO-RES-001', c.id_circuit, u.id, now(), now()
FROM circuits c, utilisateurs u
WHERE c.nom = 'Decouverte du Sud Benin' AND u.email = 'client1.demo@beninexplo.local'
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE reference_reservation = 'DEMO-RES-001');

INSERT INTO reservations (nom, prenom, email, telephone, date_reservation, statut, nombre_personnes, commentaires, reference_reservation, circuit_id, utilisateur_id, created_at, updated_at)
SELECT 'Martin', 'Luc', 'client2.demo@beninexplo.local', '+33 6 00 00 00 02', CURRENT_DATE + INTERVAL '30 days', 'CONFIRMEE', 4,
       'Voyage en famille, deux enfants de 8 et 12 ans.', 'DEMO-RES-002', c.id_circuit, u.id, now(), now()
FROM circuits c, utilisateurs u
WHERE c.nom = 'Safari et montagnes du Nord' AND u.email = 'client2.demo@beninexplo.local'
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE reference_reservation = 'DEMO-RES-002');

INSERT INTO reservations (nom, prenom, email, telephone, date_reservation, statut, nombre_personnes, commentaires, reference_reservation, circuit_id, utilisateur_id, created_at, updated_at)
SELECT 'Dupont', 'Marie', 'client1.demo@beninexplo.local', '+33 6 00 00 00 01', CURRENT_DATE - INTERVAL '10 days', 'ANNULEE', 1,
       'Empechement de derniere minute.', 'DEMO-RES-003', c.id_circuit, u.id, now(), now()
FROM circuits c, utilisateurs u
WHERE c.nom = 'Route royale d''Abomey' AND u.email = 'client1.demo@beninexplo.local'
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE reference_reservation = 'DEMO-RES-003');

-- ============================================================
-- 8. reservations d'hebergement (statuts varies)
-- ============================================================
INSERT INTO reservations_hebergement (id_hebergement, utilisateur_id, nom_client, prenom_client, email_client, telephone_client, reference_reservation, date_arrivee, date_depart, nombre_nuits, nombre_personnes, prix_total, statut, commentaires, date_creation, created_at, updated_at)
SELECT h.id_hebergement, u.id, 'Dupont', 'Marie', 'client1.demo@beninexplo.local', '+33 6 00 00 00 01', 'DEMO-HEB-001',
       CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '23 days', 3, 2, 105000, 'EN_ATTENTE',
       'Chambre avec vue sur la lagune si disponible.', CURRENT_DATE, now(), now()
FROM hebergements h, utilisateurs u
WHERE h.nom = 'Hotel du Lac' AND u.email = 'client1.demo@beninexplo.local'
  AND NOT EXISTS (SELECT 1 FROM reservations_hebergement WHERE reference_reservation = 'DEMO-HEB-001');

INSERT INTO reservations_hebergement (id_hebergement, utilisateur_id, nom_client, prenom_client, email_client, telephone_client, reference_reservation, date_arrivee, date_depart, nombre_nuits, nombre_personnes, prix_total, statut, commentaires, date_creation, created_at, updated_at)
SELECT h.id_hebergement, u.id, 'Martin', 'Luc', 'client2.demo@beninexplo.local', '+33 6 00 00 00 02', 'DEMO-HEB-002',
       CURRENT_DATE + INTERVAL '35 days', CURRENT_DATE + INTERVAL '38 days', 3, 4, 165000, 'CONFIRMEE',
       NULL, CURRENT_DATE, now(), now()
FROM hebergements h, utilisateurs u
WHERE h.nom = 'Lodge Pendjari Safari' AND u.email = 'client2.demo@beninexplo.local'
  AND NOT EXISTS (SELECT 1 FROM reservations_hebergement WHERE reference_reservation = 'DEMO-HEB-002');

-- ============================================================
-- 9. demande de circuit personnalise (EN_ATTENTE, a traiter par l'admin)
-- ============================================================
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client, message_client,
    nombre_jours, nombre_personnes, date_creation, date_voyage_souhaitee,
    avec_hebergement, type_hebergement, avec_transport, type_transport, avec_guide, avec_chauffeur, pension_complete,
    prix_activites_estime, prix_hebergement_estime, prix_transport_estime, prix_guide_estime, prix_chauffeur_estime, prix_pension_complete_estime,
    prix_estime, devise_prix_estime, statut, reference_reservation, utilisateur_id, created_at, updated_at
)
SELECT 'Martin', 'Luc', 'client2.demo@beninexplo.local', '+33 6 00 00 00 02',
       'Nous aimerions un circuit combinant culture et nature, avec un guide francophone, pour un couple sans enfants.',
       5, 2, CURRENT_DATE, CURRENT_DATE + INTERVAL '60 days',
       true, 'Hotel', true, 'Vehicule prive avec chauffeur', true, true, false,
       40000, 150000, 60000, 50000, 30000, 0,
       330000, 'EUR', 'EN_ATTENTE', 'DEMO-PERSO-001', u.id, now(), now()
FROM utilisateurs u
WHERE u.email = 'client2.demo@beninexplo.local'
  AND NOT EXISTS (SELECT 1 FROM circuits_personnalises WHERE reference_reservation = 'DEMO-PERSO-001');

-- ============================================================
-- 10. parametres_site (une ligne unique de configuration)
-- ============================================================
INSERT INTO parametres_site (email_contact, telephone_contact, adresse_agence, created_at, updated_at)
SELECT 'contact@beninexplo.com', '+229 00 00 00 00', 'Cotonou, Benin', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM parametres_site);
