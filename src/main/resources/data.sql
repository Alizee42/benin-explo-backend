-- Insertion des villes du Bénin
-- D'abord, s'assurer que les zones existent
INSERT INTO zones (nom, description) VALUES
('Sud', 'Région sud du Bénin'),
('Centre', 'Région centre du Bénin'),
('Nord', 'Région nord du Bénin')
ON DUPLICATE KEY UPDATE nom = nom;

-- Créer la table hebergements si elle n'existe pas
CREATE TABLE IF NOT EXISTS hebergements (
    id_hebergement BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    localisation VARCHAR(255) NOT NULL,
    quartier VARCHAR(255),
    description TEXT,
    prix_par_nuit DOUBLE PRECISION NOT NULL,
    image_urls TEXT -- JSON array of image URLs
);

-- Créer la table de jointure hebergement_medias
-- CREATE TABLE IF NOT EXISTS hebergement_medias (
--     id_hebergement BIGINT NOT NULL,
--     id_media BIGINT NOT NULL,
--     PRIMARY KEY (id_hebergement, id_media),
--     FOREIGN KEY (id_hebergement) REFERENCES hebergements(id_hebergement) ON DELETE CASCADE,
--     FOREIGN KEY (id_media) REFERENCES medias(id_media) ON DELETE CASCADE
-- );

-- Insertion des villes
INSERT INTO villes (nom, zone_id) VALUES
-- Sud
('Cotonou', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Porto-Novo', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Ouidah', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Abomey-Calavi', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Allada', (SELECT id_zone FROM zones WHERE nom = 'Sud')),
('Lokossa', (SELECT id_zone FROM zones WHERE nom = 'Sud')),

-- Centre
('Abomey', (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Bohicon', (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Covè', (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Dassa-Zoumè', (SELECT id_zone FROM zones WHERE nom = 'Centre')),
('Savalou', (SELECT id_zone FROM zones WHERE nom = 'Centre')),

-- Nord
('Parakou', (SELECT id_zone FROM zones WHERE nom = 'Nord')),
('Djougou', (SELECT id_zone FROM zones WHERE nom = 'Nord')),
('Natitingou', (SELECT id_zone FROM zones WHERE nom = 'Nord')),
('Kandi', (SELECT id_zone FROM zones WHERE nom = 'Nord')),
('Malanville', (SELECT id_zone FROM zones WHERE nom = 'Nord'))
ON DUPLICATE KEY UPDATE nom = nom;

-- Insertion de médias pour les images (si nécessaire)
INSERT INTO medias (id_media, url, type, description) VALUES
(1, '/uploads/coucherSoleil.avif', 'image', 'Coucher de soleil sur la plage'),
(2, '/uploads/686364_QuatreTiers.ori.jpg', 'image', 'Architecture traditionnelle'),
(3, '/uploads/BENIN Explo - Logo Final.jpg', 'image', 'Logo Bénin Explo'),
(4, '/uploads/culture.jpg', 'image', 'Culture locale'),
(5, '/uploads/elephant.jpg', 'image', 'Éléphant dans la savane'),
(6, '/uploads/esclaves.jpg', 'image', 'Histoire des esclaves'),
(7, '/uploads/faune.jpg', 'image', 'Faune africaine'),
(8, '/uploads/Flag_map_of_Benin.svg.png', 'image', 'Carte du Bénin'),
(9, '/uploads/marche_assigame.jpeg.webp', 'image', 'Marché traditionnel'),
(10, '/uploads/palais.jpg', 'image', 'Palais royal'),
(11, '/uploads/pendjari-national-park.jpg', 'image', 'Parc national de la Pendjari'),
(12, '/uploads/roy.jpg', 'image', 'Royauté'),
(13, '/uploads/royal-palaces-of-abomey.jpg', 'image', 'Palais royaux d''Abomey'),
(14, '/uploads/templepython.jpg', 'image', 'Temple python'),
(15, '/uploads/village.jpg', 'image', 'Village traditionnel'),
(16, '/uploads/villagois.jpg', 'image', 'Villageois'),
(17, '/uploads/124905.jpg.webp', 'image', 'Image décorative'),
(18, '/uploads/masque.jpg.webp', 'image', 'Masque traditionnel'),
(19, '/uploads/grandPopo.png', 'image', 'Grand-Popo'),
(20, '/uploads/63298b61-0e5e-48f1-bf65-d0b64831b771.avif', 'image', 'Image uploadée 1'),
(21, '/uploads/e04d97af-fef2-4e7a-8f97-0766cc8ae4cd.avif', 'image', 'Image uploadée 2')
ON DUPLICATE KEY UPDATE url = url;

-- Insertion des hébergements avec medias
INSERT INTO hebergements (nom, type, localisation, quartier, description, prix_par_nuit, image_urls) VALUES
('Hôtel du Lac', 'Hôtel', 'Cotonou', 'Quartier du Lac', 'Magnifique hôtel au bord du lac Nokoué avec vue panoramique. Chambres confortables, restaurant traditionnel et piscine.', 85000, '["/uploads/coucherSoleil.avif", "/uploads/686364_QuatreTiers.ori.jpg"]'),
('Villa Paradis', 'Villa', 'Ouidah', 'Plage Fidjrossè', 'Villa de luxe avec piscine privée, jardin tropical et accès direct à la plage. Idéal pour les familles.', 120000, '["/uploads/BENIN Explo - Logo Final.jpg", "/uploads/culture.jpg", "/uploads/elephant.jpg"]'),
('Auberge du Palais', 'Auberge', 'Abomey', 'Centre historique', 'Auberge traditionnelle dans le quartier du Palais Royal. Atmosphère authentique et décoration locale.', 45000, '["/uploads/esclaves.jpg", "/uploads/faune.jpg"]'),
('Eco-Lodge Pendjari', 'Eco-lodge', 'Natitingou', 'Parc National', 'Lodge écologique au cœur du parc de la Pendjari. Observation de la faune sauvage et nuits sous tente.', 95000, '["/uploads/Flag_map_of_Benin.svg.png", "/uploads/marche_assigame.jpeg.webp"]'),
('Résidence Porto-Novo', 'Résidence', 'Porto-Novo', 'Quartier Administratif', 'Résidence moderne avec chambres climatisées et petit-déjeuner continental. Proche du Palais de la Présidence.', 65000, '["/uploads/palais.jpg"]'),
('Campement Brousse', 'Campement', 'Djougou', 'Région Nord', 'Campement traditionnel en pleine brousse avec sanitaires écologiques. Expérience authentique de la vie nomade.', 35000, '["/uploads/pendjari-national-park.jpg"]'),
('Hôtel de la Plage', 'Hôtel', 'Grand-Popo', 'Front de mer', 'Hôtel moderne face à l''océan Atlantique. Chambres avec balcon, restaurant de fruits de mer.', 78000, '["/uploads/roy.jpg", "/uploads/royal-palaces-of-abomey.jpg"]'),
('Maison d''hôtes Bohicon', 'Maison d''hôtes', 'Bohicon', 'Centre-ville', 'Charmante maison d''hôtes avec jardin fleuri. Cuisine familiale et accueil chaleureux.', 55000, '["/uploads/templepython.jpg", "/uploads/village.jpg"]'),
('Lodge Montagne', 'Lodge', 'Dassa-Zoumè', 'Pied de montagne', 'Lodge de montagne avec vue sur les collines. Chambres spacieuses et restaurant panoramique.', 72000, '["/uploads/villagois.jpg"]'),
('Gîte Rural Parakou', 'Gîte', 'Parakou', 'Quartier résidentiel', 'Gîte rural confortable avec jardin potager. Idéal pour les séjours prolongés.', 42000, '["/uploads/124905.jpg.webp", "/uploads/masque.jpg.webp"]')
ON DUPLICATE KEY UPDATE nom = nom;

-- Associer des medias aux hébergements
-- INSERT INTO hebergement_medias (id_hebergement, id_media) VALUES
-- (1, 1), (1, 2), -- Hôtel du Lac
-- (2, 3), (2, 4), (2, 5), -- Villa Paradis
-- (3, 6), (3, 7), -- Auberge du Palais
-- (4, 8), (4, 9), -- Eco-Lodge Pendjari
-- (5, 10), -- Résidence Porto-Novo
-- (6, 1), -- Campement Brousse
-- (7, 2), (7, 3), -- Hôtel de la Plage
-- (8, 4), (8, 5), -- Maison d'hôtes Bohicon
-- (9, 6), -- Lodge Montagne
-- (10, 7), (10, 8) -- Gîte Rural Parakou
-- ON DUPLICATE KEY UPDATE id_hebergement = id_hebergement;

-- Insertion des activités avec prix
INSERT INTO activites (nom, description, duree_interne, poids, difficulte, zone_id, ville, image_principale_id) VALUES
-- Sud
('Musée Fondation Zinsou', 'Découvrez l''art contemporain africain dans ce musée unique.', 60, 15, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Cotonou'),
('Visite Lac Nokoué', 'Balade en pirogue sur le lac et découverte des villages flottants.', 120, 20, 'Nature', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Cotonou'),
('Coucher de soleil à Fidjrossè', 'Fin de journée sur la plage, dégustation de brochettes locales et jus frais face à l''océan.', 90, 12, 'Détente', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Ouidah'),
('Marché de Dantokpa', 'Immersion dans le plus grand marché de la sous-région, artisanat, tissus et épices.', 180, 10, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Cotonou'),
('Musée Ethnographique Porto-Novo', 'Exploration des collections d''art et d''histoire africaine.', 90, 8, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Porto-Novo'),

-- Centre
('Palais Royal Abomey', 'Visite guidée du palais royal et des fresques historiques.', 120, 18, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Centre'), 'Abomey'),
('Atelier de tisserands à Abomey', 'Découverte des techniques de tissage traditionnelles et rencontre avec les artisans.', 150, 14, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Centre'), 'Abomey'),

-- Nord
('Safari Parc de la Pendjari', 'Observation de la faune et randonnée dans le parc de la Pendjari.', 480, 50, 'Aventure', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou'),
('Randonnée Monts Atacora', 'Ascension des montagnes avec guides locaux.', 360, 35, 'Aventure', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou'),
('Visite des Tata Somba', 'Découverte des habitats traditionnels fortifiés et échanges avec les habitants.', 240, 25, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou'),
('Nuit en campement en brousse', 'Soirée autour du feu, nuit sous tente et observation du ciel étoilé.', 720, 40, 'Nature', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou'),

-- Plus d'activités
('Visite du Marché aux Fétiches', 'Exploration du marché traditionnel des fétiches et objets rituels.', 60, 5, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Ouidah'),
('Balade en bateau sur la lagune', 'Croisière paisible sur la lagune de Porto-Novo avec guide local.', 90, 22, 'Nature', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Porto-Novo'),
('Découverte des plages de Grand-Popo', 'Randonnée le long des plages sauvages et baignade dans l''océan.', 180, 15, 'Détente', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Lokossa'),
('Visite de la Basilique de Ouidah', 'Découverte de l''architecture coloniale et de l''histoire religieuse.', 45, 6, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Ouidah'),
('Atelier de cuisine béninoise', 'Apprentissage des recettes traditionnelles avec un chef local.', 120, 25, 'Gastronomie', (SELECT id_zone FROM zones WHERE nom = 'Sud'), 'Cotonou'),

-- Centre
('Visite du Musée Honmé', 'Exploration du musée des arts et traditions du Bénin.', 75, 9, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Centre'), 'Abomey'),
('Randonnée dans la Forêt de Pobè', 'Balade guidée dans la réserve naturelle avec observation d''oiseaux.', 240, 20, 'Nature', (SELECT id_zone FROM zones WHERE nom = 'Centre'), 'Bohicon'),
('Découverte des villages Zoun', 'Visite des villages traditionnels et rencontre avec les habitants.', 180, 16, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Centre'), 'Dassa-Zoumè'),
('Atelier de poterie à Covè', 'Initiation à la poterie traditionnelle béninoise.', 150, 18, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Centre'), 'Covè'),

-- Nord
('Visite du Musée de la Pendjari', 'Découverte de l''histoire du parc national et de sa biodiversité.', 60, 8, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou'),
('Randonnée dans les Monts Kouffé', 'Ascension des montagnes sacrées avec vue panoramique.', 300, 28, 'Aventure', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou'),
('Visite des chutes de Koudou', 'Balade vers les chutes d''eau et pique-nique local.', 150, 12, 'Nature', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Djougou'),
('Découverte de l''artisanat de Parakou', 'Visite des ateliers de bronze et de tissage.', 120, 11, 'Culture', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Parakou'),
('Safari nocturne en 4x4', 'Observation des animaux nocturnes dans le parc de la Pendjari.', 360, 45, 'Aventure', (SELECT id_zone FROM zones WHERE nom = 'Nord'), 'Natitingou')
ON DUPLICATE KEY UPDATE nom = nom;