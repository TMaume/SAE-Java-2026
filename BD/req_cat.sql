SELECT numboite, nomboite, annee, idtheme, nomtheme
FROM BOITE NATURAL JOIN THEME
ORDER BY annee, nomtheme, numboite;