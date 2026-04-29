-- SAE 2026
-- Nom:  , Prenom: 

-- +------------------+--
-- * Question 1 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Les informations sur les boîtes qui contiennent une pièce de couleur Very Light Orange

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +---------------+------------------------------------------+-------+----------+---------+
-- | numboite      | nomboite                                 | annee | nbpieces | idtheme |
-- +---------------+------------------------------------------+-------+----------+---------+
-- | 3149-1        | Happy Home                               | 2000  | 274      | 668     |
-- | etc...
-- = Reponse question 1.
SELECT DISTINCT numboite, nomboite, annee, nbpieces, idtheme
FROM BOITE NATURAL JOIN CONTENU NATURAL JOIN CONTENIRP NATURAL JOIN COULEUR
WHERE nomcoul = 'Very Light Orange'
ORDER BY numboite;


-- +------------------+--
-- * Question 2 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Les informations sur les contenus qui sont présents à la fois dans CONTENIRB et dans CONTENIRP.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +--------+---------+-------------------+-------+
-- | idcont | version | numboite          | idfig |
-- +--------+---------+-------------------+-------+
-- | 311    | 1       | 10201-1           | None  |
-- | etc...
-- = Reponse question 2.
SELECT DISTINCT idcont, version, numboite, idfig
FROM CONTENU
WHERE idcont IN (SELECT DISTINCT idcont FROM CONTENIRB) AND idcont IN (SELECT DISTINCT idcont FROM CONTENIRP)
ORDER BY idcont;


-- +------------------+--
-- * Question 3 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Les informations des pièces qui ne sont utilisées que dans des boîtes du thème Fortnite.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +-------------+-------------------------------------------------------------------------------------------+------------------------+
-- | numpiece    | nompiece                                                                                  | nomcat                 |
-- +-------------+-------------------------------------------------------------------------------------------+------------------------+
-- | 6180pr0036  | Plate Special 4 x 6 with Studs on 3 Edges with 'LEGO FORTNITE PEELY BONE' print           | Plates Special         |
-- | etc...
-- = Reponse question 3.
SELECT numpiece, nompiece, nomcat
FROM PIECE NATURAL JOIN CATEGORIE
WHERE numpiece IN (
	SELECT DISTINCT numpiece
	FROM CONTENIRP NATURAL JOIN CONTENU NATURAL JOIN BOITE NATURAL JOIN THEME
	WHERE nomtheme = 'Fortnite'
)
AND numpiece NOT IN (
	SELECT DISTINCT numpiece
	FROM CONTENIRP NATURAL JOIN CONTENU NATURAL JOIN BOITE NATURAL JOIN THEME
	WHERE nomtheme != 'Fortnite'
);


-- +------------------+--
-- * Question 4 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Les informations sur la ou les pièces utilisées en plus grande quantité dans une seule boîte.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +----------+-------------+--------+--------+
-- | numpiece | nompiece    | nomcat | total  |
-- +----------+-------------+--------+--------+
-- | 3023     | Plate 1 x 2 | Plates | 161631 |
-- +----------+-------------+--------+--------+
-- = Reponse question 4.
with totals AS (
	SELECT numpiece, SUM(quantitep) AS total
	FROM CONTENIRP NATURAL JOIN CONTENU
	GROUP BY numpiece
)

SELECT numpiece, nompiece, nomcat, SUM(quantitep) AS total
FROM CONTENIRP NATURAL JOIN CONTENU NATURAL JOIN PIECE NATURAL JOIN CATEGORIE
GROUP BY numpiece, nompiece, nomcat
HAVING total = (SELECT MAX(total) FROM totals)
ORDER BY numpiece;


-- +------------------+--
-- * Question 5 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  L'année où pour la première fois une boîte du thème Technic est apparue.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +-------+
-- | annee |
-- +-------+
-- | 1977  |
-- +-------+
-- = Reponse question 5.
SELECT MIN(annee) AS annee
FROM BOITE NATURAL JOIN THEME
WHERE nomtheme = 'Technic';


-- +------------------+--
-- * Question 6 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Pour chaque année, on voudrait connaitre le nombre de couleurs différentes utilisées dans les boîtes crées cette année là.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +-------+--------+
-- | annee | nbcoul |
-- +-------+--------+
-- | 1949  | 10     |
-- | etc...
-- = Reponse question 6.
SELECT annee, COUNT(DISTINCT idcoul) AS nbcoul
FROM BOITE NATURAL JOIN CONTENU NATURAL JOIN CONTENIRP
GROUP BY annee
ORDER BY annee;


-- +------------------+--
-- * Question 7 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  les informations des pièces de la catégorie Pen & Watch qui ne sont contenues dans aucune boîte.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +------------------+-------------------------------------------------------------------------------+-------------+
-- | numpiece         | nompiece                                                                      | nomcat      |
-- +------------------+-------------------------------------------------------------------------------+-------------+
-- | penupn0003pr0020 | Pen Bead, Round Orb with 'YODA' Print                                         | Pen & Watch |
-- | etc...
-- = Reponse question 7.
SELECT numpiece, nompiece, nomcat
FROM PIECE NATURAL JOIN CATEGORIE
WHERE nomcat = 'Pen & Watch' AND numpiece NOT IN (
	SELECT DISTINCT numpiece
	FROM CONTENIRP NATURAL JOIN CONTENU
	WHERE numboite IS NOT NULL
  )
ORDER BY numpiece;


-- +------------------+--
-- * Question 8 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  la requête qui associe à chaque identifiant de thème le nom de son thème principal (on supose qu'il n'y a que deux niveaux de sous-thème.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +---------+----------------------------------+
-- | idtheme | nomtheme                         |
-- +---------+----------------------------------+
-- | 280     | 4 Juniors                        |
-- | etc...
-- = Reponse question 8.
SELECT e.idtheme, p.nomtheme
FROM THEME e JOIN THEME p ON e.idtheme_pere = p.idtheme
GROUP BY p.nomtheme
ORDER BY p.nomtheme;


-- +------------------+--
-- * Question 9 :     --
-- +------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  la requête qui donne les informations des boîtes du thème Harry Potter qui incluent plus de 10 figurines.

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +----------+------------------------------------------------+-------+-----------+
-- | numboite | nomboite                                       | annee | total_fig |
-- +----------+------------------------------------------------+-------+-----------+
-- | 10217-1  | Diagon Alley                                   | 2011  | 24        |
-- | etc...
-- = Reponse question 9.
WITH contenu_boite AS (
  SELECT idcont, numboite
  FROM CONTENU
  WHERE numboite IS NOT NULL
)

SELECT numboite, nomboite, annee, SUM(quantitef) AS total_fig
FROM BOITE NATURAL JOIN THEME NATURAL JOIN contenu_boite NATURAL JOIN CONTENIRF
WHERE nomtheme = 'Harry Potter' OR idtheme_pere IN (
	SELECT idtheme 
	FROM THEME 
	WHERE nomtheme = 'Harry Potter')
GROUP BY numboite, nomboite, annee
HAVING total_fig > 10
ORDER BY numboite;


-- +-------------------+--
-- * Question 10 :     --
-- +-------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Requete du premier graphique pour la boite Harry Potter Hogwarts Crests

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +-------------------+-------+
-- | nomcoul           | total |
-- +-------------------+-------+
-- | Black             | 800   |
-- | etc...
-- = Reponse question 10.
SELECT nomcoul, SUM(quantitep) AS total
FROM BOITE NATURAL JOIN CONTENU NATURAL JOIN CONTENIRP NATURAL JOIN COULEUR
WHERE numboite = '31201-1'
GROUP BY nomcoul
ORDER BY total DESC, nomcoul;


-- +-------------------+--
-- * Question 11 :     --
-- +-------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Requete du deuxième graphique pour le thème Castle

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +--------------------+--------+-----------+
-- | nomtheme           | pere   | nb_boites |
-- +--------------------+--------+-----------+
-- | Black Falcons      | Castle | 7         |
-- | etc...
-- = Reponse question 11.
WITH theme_pere AS (
	SELECT t.idtheme, t.nomtheme, p.nomtheme AS pere, p.idtheme_pere
	FROM THEME t JOIN THEME p ON t.idtheme_pere = p.idtheme
)

SELECT nomtheme, pere, COUNT(numboite) AS nb_boites
FROM theme_pere NATURAL LEFT JOIN BOITE
WHERE pere = 'Castle'
	AND idtheme_pere IS NULL
GROUP BY nomtheme, pere
HAVING nb_boites > 0
ORDER BY nomtheme;


-- +-------------------+--
-- * Question 12 :     --
-- +-------------------+--
-- Ecrire une requête qui renvoie les informations suivantes:
--  Requete du catalogue

-- Voici le début de ce que vous devez obtenir.
-- ATTENTION à l'ordre des colonnes et leur nom!
-- +----------------------+-----------------------------------------------------------------------------------------------+-------+---------+--------------------------------------------+
-- | numboite             | nomboite                                                                                      | annee | idtheme | nomtheme                                   |
-- +----------------------+-----------------------------------------------------------------------------------------------+-------+---------+--------------------------------------------+
-- | 700.A-1              | Small Brick Set (ABB)                                                                         | 1949  | 371     | Supplemental                               |
-- | etc...
-- = Reponse question 12.
SELECT numboite, nomboite, annee, idtheme, nomtheme
FROM BOITE NATURAL JOIN THEME
ORDER BY annee, nomtheme, numboite
LIMIT 10;


INSERT INTO BOITE (numboite, nomboite, annee, nbpieces, idtheme)
VALUES ('IUT-2026', 'IUT', 2026, 6, 67);

INSERT INTO CONTENU (idcont, version, numboite)
VALUES (99999, 1, 'IUT-2026');

INSERT INTO PIECE (numpiece, nompiece, idcat)
VALUES ('kragle', 'Kragle', 28);

INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep)
VALUES (99999, '98496', 379, 'N', 3);

INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep)
VALUES (99999, '33121', 100, 'N', 1);

INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep)
VALUES (99999, 'kragle', -1, 'N', 1);

INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep)
VALUES (99999, 'kragle', -1, 'S', 1);