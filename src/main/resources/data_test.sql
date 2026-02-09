-- ================================================================
-- DONNÉES DE TEST POUR BENIN EXPLO
-- ================================================================

-- Supprimer les données existantes (dans l'ordre STRICT des dépendances FK)
DELETE FROM circuit_perso_jour_activites;
DELETE FROM circuit_personnalise_jours;
DELETE FROM circuits_personnalises;
DELETE FROM circuit_activites;
DELETE FROM medias;
DELETE FROM circuits;
DELETE FROM activites;
DELETE FROM villes;
DELETE FROM zones;

-- ================================================================
-- ZONES
-- ================================================================
INSERT INTO zones (nom, description) VALUES
('Sud', 'Zone côtière avec plages et patrimoine historique'),
('Centre', 'Zone centrale avec culture et artisanat'),
('Nord', 'Zone touristique avec parcs nationaux');

-- ================================================================
-- VILLES
-- ================================================================
INSERT INTO villes (nom, zone_id) VALUES
-- Villes du Sud
('Cotonou', 1),
('Ouidah', 1),
('Grand-Popo', 1),

-- Villes du Centre
('Abomey', 2),
('Bohicon', 2),
('Dassa-Zoumé', 2),

-- Villes du Nord
('Parakou', 3),
('Natitingou', 3),
('Djougou', 3);

-- ================================================================
-- ACTIVITÉS (liées uniquement à Ville via FK)
-- ================================================================
-- Note: activites n'a PAS de zone_id, seulement ville_id

-- Activités de Cotonou (ville_id=1)
INSERT INTO activites (nom, description, duree_interne, ville_id) VALUES
('Visite du Marché Dantokpa', 'Plus grand marché d''Afrique de l''Ouest', 180, (SELECT id_ville FROM villes WHERE nom='Cotonou')),
('Tour de la ville moderne', 'Architecture coloniale et moderne', 120, (SELECT id_ville FROM villes WHERE nom='Cotonou')),
('Plage de Fidjrossè', 'Détente sur la plage urbaine', 240, (SELECT id_ville FROM villes WHERE nom='Cotonou')),

-- Activités de Ouidah (ville_id=2)
('Route des Esclaves', 'Parcours historique mémoriel', 180, (SELECT id_ville FROM villes WHERE nom='Ouidah')),
('Forêt sacrée', 'Découverte du vaudou traditionnel', 120, (SELECT id_ville FROM villes WHERE nom='Ouidah')),
('Musée d''Histoire', 'Patrimoine de l''esclavage', 120, (SELECT id_ville FROM villes WHERE nom='Ouidah')),
('Plage de Ouidah', 'Plage historique', 180, (SELECT id_ville FROM villes WHERE nom='Ouidah')),

-- Activités de Grand-Popo (ville_id=3)
('Excursion en pirogue', 'Découverte des lagunes', 240, (SELECT id_ville FROM villes WHERE nom='Grand-Popo')),
('Plage de Mono', 'Plage sauvage et tranquille', 240, (SELECT id_ville FROM villes WHERE nom='Grand-Popo')),

-- Activités d'Abomey (ville_id=4)
('Palais Royaux', 'Site UNESCO patrimoine mondial', 180, (SELECT id_ville FROM villes WHERE nom='Abomey')),
('Musée Historique', 'Histoire du royaume du Dahomey', 120, (SELECT id_ville FROM villes WHERE nom='Abomey')),
('Marché artisanal', 'Artisanat traditionnel', 120, (SELECT id_ville FROM villes WHERE nom='Abomey')),

-- Activités de Dassa-Zoumé (ville_id=6)
('Randonnée des 41 collines', 'Trek panoramique', 300, (SELECT id_ville FROM villes WHERE nom='Dassa-Zoumé')),
('Grotte de Dassa', 'Site spirituel', 120, (SELECT id_ville FROM villes WHERE nom='Dassa-Zoumé')),

-- Activités de Parakou (ville_id=7)
('Marché de Parakou', 'Marché traditionnel du Nord', 120, (SELECT id_ville FROM villes WHERE nom='Parakou')),
('Quartier artisanal', 'Découverte de l''artisanat local', 180, (SELECT id_ville FROM villes WHERE nom='Parakou')),

-- Activités de Natitingou (ville_id=8)
('Parc de la Pendjari', 'Safari et observation des animaux', 480, (SELECT id_ville FROM villes WHERE nom='Natitingou')),
('Tata Somba', 'Architecture traditionnelle', 180, (SELECT id_ville FROM villes WHERE nom='Natitingou')),
('Cascade de Tanougou', 'Baignade naturelle', 240, (SELECT id_ville FROM villes WHERE nom='Natitingou')),

-- Activités de Djougou (ville_id=9)
('Marché de Djougou', 'Grand marché du Nord', 120, (SELECT id_ville FROM villes WHERE nom='Djougou')),
('Villages Yom', 'Découverte culturelle', 240, (SELECT id_ville FROM villes WHERE nom='Djougou'));

-- ================================================================
-- CIRCUITS PRÉDÉFINIS
-- ================================================================
-- Note: La table circuits a une structure complexe (img, galerie, programme, etc.)
-- Ces données devront être ajoutées manuellement via l'interface admin

-- Circuit Sud: Cotonou
INSERT INTO circuits (nom, description, duree_indicative, actif) VALUES
('Patrimoine du Sud', 
'Découverte de l''histoire et des plages du sud du Bénin. De Cotonou à Ouidah, explorez la route des esclaves et les traditions vaudou.',
'3 jours', true);

-- Circuit Centre: Abomey
INSERT INTO circuits (nom, description, duree_indicative, actif) VALUES
('Royaume du Dahomey',
'Plongez dans l''histoire royale du Bénin à travers les palais d''Abomey, site UNESCO.',
'2 jours', true);

-- Circuit Nord: Natitingou
INSERT INTO circuits (nom, description, duree_indicative, actif) VALUES
('Safari Pendjari',
'Aventure sauvage dans le parc de la Pendjari, l''un des derniers sanctuaires d''Afrique de l''Ouest.',
'4 jours', true);

-- Circuit Complet
INSERT INTO circuits (nom, description, duree_indicative, actif) VALUES
('Grand Tour du Bénin',
'Circuit complet du Sud au Nord : Histoire, Culture, Nature et Safari.',
'10 jours', true);

-- ================================================================
-- CIRCUITS PERSONNALISÉS (exemples de demandes)
-- ================================================================

-- Demande EN_ATTENTE: Famille Sud
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours, 
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client, statut, prix_estime, date_creation
) VALUES (
    'MENSAH', 'Kofi',
    'kofi.mensah@example.com', '+229 96 12 34 56',
    4, 3,
    true, 'HOTEL',
    true, 'VOITURE',
    true, false, false,
    'Nous souhaitons un circuit familial relaxant avec les enfants.',
    'EN_ATTENTE', 200000, CURRENT_DATE
);

-- Jours pour la demande 1 (Sud: Cotonou -> Ouidah -> Grand-Popo)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi'), (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Cotonou')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi'), (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Ouidah')),
(3, (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi'), (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Grand-Popo'));

-- Activités jour 1 (Cotonou) - activités 1,2
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi')
  AND j.numero_jour = 1
  AND a.nom IN ('Visite du Marché Dantokpa', 'Plage de Fidjrossè');

-- Activités jour 2 (Ouidah) - activités 4,5,7
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi')
  AND j.numero_jour = 2
  AND a.nom IN ('Route des Esclaves', 'Forêt sacrée', 'Plage de Ouidah');

-- Activités jour 3 (Grand-Popo) - activités 8,9
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='MENSAH' AND prenom_client='Kofi')
  AND j.numero_jour = 3
  AND a.nom IN ('Excursion en pirogue', 'Plage de Mono');

-- ================================================================

-- Demande ACCEPTE: Couple Culture Centre
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client, statut, prix_estime, prix_final, date_creation
) VALUES (
    'KOUDOU', 'Aïcha',
    'aicha.koudou@example.com', '+229 97 23 45 67',
    2, 2,
    true, 'AUBERGE',
    true, 'TAXI',
    true, false, false,
    'Circuit culturel pour notre anniversaire de mariage.',
    'ACCEPTE', 140000, 135000, CURRENT_DATE
);

-- Jours pour la demande 2 (Centre: Abomey -> Dassa)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha'), (SELECT id_zone FROM zones WHERE nom='Centre'), (SELECT id_ville FROM villes WHERE nom='Abomey')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha'), (SELECT id_zone FROM zones WHERE nom='Centre'), (SELECT id_ville FROM villes WHERE nom='Dassa-Zoumé'));

-- Activités jour 1 (Abomey) - activités 10,11,12
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha')
  AND j.numero_jour = 1
  AND a.nom IN ('Palais Royaux', 'Musée Historique', 'Marché artisanal');

-- Activités jour 2 (Dassa-Zoumé) - activités 13,14
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='KOUDOU' AND prenom_client='Aïcha')
  AND j.numero_jour = 2
  AND a.nom IN ('Randonnée des 41 collines', 'Grotte de Dassa');

-- ================================================================

-- Demande REFUSE: Demande irréaliste
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, avec_transport, avec_guide, avec_chauffeur, pension_complete,
    message_client, statut, prix_estime, date_creation
) VALUES (
    'DUPONT', 'Jean',
    'jean.dupont@example.com', '+33 6 12 34 56 78',
    20, 1,
    false, false, false, false, false,
    'Budget maximum 50000 XOF pour 20 personnes',
    'REFUSE', 50000, CURRENT_DATE
);

-- Jour pour la demande 3
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='DUPONT' AND prenom_client='Jean'), (SELECT id_zone FROM zones WHERE nom='Sud'), (SELECT id_ville FROM villes WHERE nom='Cotonou'));

INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='DUPONT' AND prenom_client='Jean')
  AND j.numero_jour = 1
  AND a.nom = 'Visite du Marché Dantokpa';

-- ================================================================

-- Demande EN_TRAITEMENT: Safari Nord
INSERT INTO circuits_personnalises (
    nom_client, prenom_client, email_client, telephone_client,
    nombre_personnes, nombre_jours,
    avec_hebergement, type_hebergement,
    avec_transport, type_transport,
    avec_guide, avec_chauffeur, pension_complete,
    message_client, statut, prix_estime, date_creation
) VALUES (
    'JOHNSON', 'Michael',
    'michael.johnson@example.com', '+1 555 123 4567',
    6, 5,
    true, 'HOTEL',
    true, 'VOITURE',
    true, true, false,
    'We want an authentic safari experience with cultural immersion.',
    'EN_TRAITEMENT', 350000, CURRENT_DATE
);

-- Jours pour la demande 4 (Nord complet)
INSERT INTO circuit_personnalise_jours (numero_jour, circuit_personnalise_id, zone_id, ville_id) VALUES
(1, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'), (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Parakou')),
(2, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'), (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Natitingou')),
(3, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'), (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Natitingou')),
(4, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'), (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Natitingou')),
(5, (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael'), (SELECT id_zone FROM zones WHERE nom='Nord'), (SELECT id_ville FROM villes WHERE nom='Djougou'));

-- Activités demande 4
-- Jour 1: Parakou
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour = 1
  AND a.nom IN ('Marché de Parakou', 'Quartier artisanal');

-- Jours 2-4: Natitingou (Pendjari, Tata Somba, Cascade)
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour IN (2, 3, 4)
  AND a.nom IN ('Parc de la Pendjari', 'Tata Somba', 'Cascade de Tanougou');

-- Jour 5: Djougou
INSERT INTO circuit_perso_jour_activites (jour_id, activite_id) 
SELECT j.id, a.id_activite 
FROM circuit_personnalise_jours j, activites a
WHERE j.circuit_personnalise_id = (SELECT id FROM circuits_personnalises WHERE nom_client='JOHNSON' AND prenom_client='Michael')
  AND j.numero_jour = 5
  AND a.nom IN ('Marché de Djougou', 'Villages Yom');

-- ================================================================
-- STATISTIQUES
-- ================================================================
SELECT 'RÉSUMÉ DES DONNÉES INSÉRÉES' as info;
SELECT COUNT(*) as total_zones FROM zones;
SELECT COUNT(*) as total_villes FROM villes;
SELECT COUNT(*) as total_activites FROM activites;
SELECT COUNT(*) as total_circuits FROM circuits;
SELECT COUNT(*) as total_demandes FROM circuits_personnalises;
SELECT statut, COUNT(*) as nombre FROM circuits_personnalises GROUP BY statut;
