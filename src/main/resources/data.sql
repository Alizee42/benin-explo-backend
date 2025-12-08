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