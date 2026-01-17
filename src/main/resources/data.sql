-- Insertion des villes du Bénin
-- D'abord, s'assurer que les zones existent
INSERT INTO zones (nom, description) VALUES
('Sud', 'Région sud du Bénin'),
('Centre', 'Région centre du Bénin'),
('Nord', 'Région nord du Bénin')
ON DUPLICATE KEY UPDATE nom = nom;

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
(1, '/images/placeholder1.jpg', 'image', 'Image placeholder 1'),
(2, '/images/placeholder2.jpg', 'image', 'Image placeholder 2'),
(3, '/images/placeholder3.jpg', 'image', 'Image placeholder 3'),
(4, '/images/placeholder4.jpg', 'image', 'Image placeholder 4'),
(5, '/images/placeholder5.jpg', 'image', 'Image placeholder 5'),
(6, '/images/placeholder6.jpg', 'image', 'Image placeholder 6'),
(7, '/images/placeholder7.jpg', 'image', 'Image placeholder 7'),
(8, '/images/placeholder8.jpg', 'image', 'Image placeholder 8'),
(9, '/images/placeholder9.jpg', 'image', 'Image placeholder 9'),
(10, '/images/placeholder10.jpg', 'image', 'Image placeholder 10')
ON DUPLICATE KEY UPDATE url = url;

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