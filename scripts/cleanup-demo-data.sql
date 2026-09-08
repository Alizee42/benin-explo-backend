-- Supprime uniquement les donnees inserees par seed-demo-data.sql.
-- Usage : psql -h localhost -U postgres -d benin_explo -f scripts/cleanup-demo-data.sql
-- Ne touche pas aux zones/villes (V2, donnees de reference permanentes)
-- ni aux donnees creees ensuite via l'admin (reservations, comptes, etc.).
-- Ordre : on supprime d'abord ce qui depend d'une FK (reservations, demandes)
-- avant les tables referencees (circuits, hebergements, activites, utilisateurs).

DELETE FROM circuits_personnalises WHERE reference_reservation = 'DEMO-PERSO-001';

DELETE FROM reservations_hebergement WHERE reference_reservation IN ('DEMO-HEB-001', 'DEMO-HEB-002');

DELETE FROM reservations WHERE reference_reservation IN ('DEMO-RES-001', 'DEMO-RES-002', 'DEMO-RES-003');

DELETE FROM utilisateurs WHERE email IN ('client1.demo@beninexplo.local', 'client2.demo@beninexplo.local');

DELETE FROM actualite WHERE titre IN (
    'Ouverture de la saison des safaris a la Pendjari',
    'Nouveau circuit : Collines sacrees et artisanat du Centre',
    'Conseils pour bien preparer son voyage au Benin'
);

DELETE FROM circuits WHERE nom IN (
    'Decouverte du Sud Benin',
    'Route royale d''Abomey',
    'Safari et montagnes du Nord',
    'Escapade lagunaire',
    'Collines sacrees et artisanat du Centre'
);

DELETE FROM hebergements WHERE nom IN (
    'Hotel du Lac',
    'Residence Les Cocotiers',
    'Auberge de Ouidah',
    'Case Royale d''Abomey',
    'Hotel des Collines',
    'Lodge Pendjari Safari'
);

DELETE FROM activites WHERE nom IN (
    'Visite du marche Dantokpa',
    'Balade en pirogue sur la lagune',
    'Musee ethnographique de Porto-Novo',
    'Visite du lac Nokoue et village d''Ganvie',
    'Route des esclaves',
    'Foret sacree de Kpasse',
    'Plage de Ouidah',
    'Universite et campus d''Abomey-Calavi',
    'Palais royaux d''Abomey',
    'Atelier artisanat de bronze',
    'Grottes sacrees de Dassa-Zoume',
    'Marche central de Parakou',
    'Randonnee dans les montagnes de l''Atacora',
    'Safari au parc de la Pendjari',
    'Visite des tata somba'
);

DELETE FROM categories_activites WHERE nom IN ('Nature', 'Culture', 'Aventure', 'Detente');

DELETE FROM parametres_site WHERE email_contact = 'contact@beninexplo.com';
