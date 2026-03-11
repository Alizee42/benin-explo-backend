INSERT INTO zones (nom, description)
SELECT 'Sud', 'Region sud du Benin'
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE nom = 'Sud');

INSERT INTO zones (nom, description)
SELECT 'Centre', 'Region centre du Benin'
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE nom = 'Centre');

INSERT INTO zones (nom, description)
SELECT 'Nord', 'Region nord du Benin'
WHERE NOT EXISTS (SELECT 1 FROM zones WHERE nom = 'Nord');

INSERT INTO villes (nom, zone_id)
SELECT 'Cotonou', z.id_zone
FROM zones z
WHERE z.nom = 'Sud'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Cotonou');

INSERT INTO villes (nom, zone_id)
SELECT 'Porto-Novo', z.id_zone
FROM zones z
WHERE z.nom = 'Sud'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Porto-Novo');

INSERT INTO villes (nom, zone_id)
SELECT 'Ouidah', z.id_zone
FROM zones z
WHERE z.nom = 'Sud'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Ouidah');

INSERT INTO villes (nom, zone_id)
SELECT 'Abomey-Calavi', z.id_zone
FROM zones z
WHERE z.nom = 'Sud'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Abomey-Calavi');

INSERT INTO villes (nom, zone_id)
SELECT 'Allada', z.id_zone
FROM zones z
WHERE z.nom = 'Sud'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Allada');

INSERT INTO villes (nom, zone_id)
SELECT 'Lokossa', z.id_zone
FROM zones z
WHERE z.nom = 'Sud'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Lokossa');

INSERT INTO villes (nom, zone_id)
SELECT 'Abomey', z.id_zone
FROM zones z
WHERE z.nom = 'Centre'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Abomey');

INSERT INTO villes (nom, zone_id)
SELECT 'Bohicon', z.id_zone
FROM zones z
WHERE z.nom = 'Centre'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Bohicon');

INSERT INTO villes (nom, zone_id)
SELECT 'Cove', z.id_zone
FROM zones z
WHERE z.nom = 'Centre'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Cove');

INSERT INTO villes (nom, zone_id)
SELECT 'Dassa-Zoume', z.id_zone
FROM zones z
WHERE z.nom = 'Centre'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Dassa-Zoume');

INSERT INTO villes (nom, zone_id)
SELECT 'Savalou', z.id_zone
FROM zones z
WHERE z.nom = 'Centre'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Savalou');

INSERT INTO villes (nom, zone_id)
SELECT 'Parakou', z.id_zone
FROM zones z
WHERE z.nom = 'Nord'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Parakou');

INSERT INTO villes (nom, zone_id)
SELECT 'Djougou', z.id_zone
FROM zones z
WHERE z.nom = 'Nord'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Djougou');

INSERT INTO villes (nom, zone_id)
SELECT 'Natitingou', z.id_zone
FROM zones z
WHERE z.nom = 'Nord'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Natitingou');

INSERT INTO villes (nom, zone_id)
SELECT 'Kandi', z.id_zone
FROM zones z
WHERE z.nom = 'Nord'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Kandi');

INSERT INTO villes (nom, zone_id)
SELECT 'Malanville', z.id_zone
FROM zones z
WHERE z.nom = 'Nord'
  AND NOT EXISTS (SELECT 1 FROM villes WHERE nom = 'Malanville');
