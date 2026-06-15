LOAD DATA LOCAL INFILE 'colors.csv' INTO TABLE COULEUR 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3,@v4,@v5,@v6,@v7,@v8) set idcoul=NULLIF(@v1,''), nomcoul=NULLIF(@v2,''), rgb=NULLIF(@v3,''), transparent=NULLIF(SUBSTR(@v4,1,1),'');

LOAD DATA LOCAL INFILE 'part_categories.csv' INTO TABLE CATEGORIE
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2) set idcat=NULLIF(@v1,''), nomcat=NULLIF(@v2,'');

LOAD DATA LOCAL INFILE 'themes.csv' INTO TABLE THEME 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3) set idtheme=NULLIF(@v1,''), nomtheme=NULLIF(@v2,''), idtheme_pere=NULLIF(@v3,'');

LOAD DATA LOCAL INFILE 'parts.csv' INTO TABLE PIECE
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3,@v4) set numpiece=NULLIF(@v1,''), nompiece=NULLIF(@v2,''), idcat=NULLIF(@v3,'');

LOAD DATA LOCAL INFILE 'minifigs.csv' INTO TABLE FIGURINE
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3,@4) set idfig=NULLIF(@v1,''), nomfig=NULLIF(@v2,''), nbparties=NULLIF(@v3,'');


LOAD DATA LOCAL INFILE 'sets.csv' INTO TABLE BOITE 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3,@v4,@v5,@v6) set numboite=NULLIF(@v1,''), nomboite=NULLIF(@v2,''), annee=NULLIF(@v3,''),
nbpieces=NULLIF(@v5,''), idtheme=NULLIF(@v4,''), image=NULLIF(@v6,'');

LOAD DATA LOCAL INFILE 'inventories.csv' INTO TABLE CONTENU
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3,@v4) set idcont=NULLIF(@v1,''), version=NULLIF(@v2,''), numboite=NULLIF(@v3,''), idfig=NULLIF(@v4,'');

LOAD DATA LOCAL INFILE 'inventory_sets.csv' INTO TABLE CONTENIRB 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3) set idcont=NULLIF(@v1,''), numboite=NULLIF(@v2,''), quantiteb=NULLIF(@v3,'');

LOAD DATA LOCAL INFILE 'inventory_minifigs.csv' INTO TABLE CONTENIRF 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3) set idcont=NULLIF(@v1,''), idfig=NULLIF(@v2,''), quantitef=NULLIF(@v3,'');


LOAD DATA LOCAL INFILE 'inventory_parts.csv' INTO TABLE CONTENIRP
FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES 
(@v1,@v2,@v3,@v4,@v5,@v6) set idcont=NULLIF(@v1,''), numpiece=NULLIF(@v2,''), idcoul=NULLIF(@v3,''),
quantitep=NULLIF(@v4,''), en_supplement=NULLIF(substr(@v5,1,1),''); 
