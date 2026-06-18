
package App;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class BoiteTest {

    private Boite boite;
    private Theme theme;
    private Couleur couleur;

    @BeforeEach
    public void setUp() {
        theme = new Theme(1, "Castle", null);
        couleur = new Couleur(1, "Red", "#FF0000", false);
        boite = new Boite("10305", "Castle", 2021, theme, "url_image.jpg");
    }

    @Test
    public void testConstructeurAvecParametresNull() {
        Boite b = new Boite("54321", null, 2023, null, null);
        
        assertEquals("54321", b.getNumero());
        assertEquals("", b.getNom());  // nom null devrait être converti en chaîne vide
        assertEquals(2023, b.getAnnee());
        assertNull(b.getTheme());
        assertEquals("", b.getImageBoite());  // image null devrait être convertie en chaîne vide
    }

    @Test
    public void testConstructeurErreurSiNumeroVide() {
        // On vérifie que la création d'une boîte avec un numero vide déclenche une exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Boite("", "Boite Invalide", 2026, null, null);
        });
        
        assertEquals("numero", exception.getMessage());
    }

    @Test
    public void testConstructeurErreurSiNumeroNull() {
        // On vérifie que la création d'une boîte avec un numero null déclenche une exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Boite(null, "Boite Invalide", 2026, null, null);
        });
        
        assertEquals("numero", exception.getMessage());
    }

    @Test
    public void testConstructeurErreurSiNumeroEspaces() {
        // On vérifie qu'une chaîne avec uniquement des espaces est considérée comme vide
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Boite("   ", "Boite Invalide", 2026, null, null);
        });
        
        assertEquals("numero", exception.getMessage());
    }


    @Test
    public void testGetNbPiecesNull() {
        // Si nbPieces est null, getNbPieces() devrait retourner le résultat de calculerNbPieces()
        boite.setNbPieces(null);
        assertEquals(0, boite.getNbPieces());  // Aucune pièce ajoutée
    }

    // Tests des collections (pièces, figurines, boîtes)

    @Test
    public void testGetPiecesVide() {
        List<PieceQuantite> pieces = boite.getPieces();
        assertNotNull(pieces);
        assertTrue(pieces.isEmpty());
    }

    @Test
    public void testAjouterPiece() {
        Categorie cat = new Categorie(1, "Briques");
        Piece piece = new Piece("3001", "Brick 2x4", cat, couleur);
        PieceQuantite pq = new PieceQuantite(piece, 5, false, "");
        
        boite.ajouterPiece(pq);
        
        assertEquals(1, boite.getPieces().size());
        assertEquals(pq, boite.getPieces().get(0));
    }

    @Test
    public void testAjouterPieceNull() {
        int tailleBefore = boite.getPieces().size();
        boite.ajouterPiece(null);
        
        // La taille ne devrait pas changer
        assertEquals(tailleBefore, boite.getPieces().size());
    }

    @Test
    public void testAjouterMultiplesPieces() {
        Categorie cat = new Categorie(1, "Briques");
        Piece piece1 = new Piece("3001", "Brick 2x4", cat, couleur);
        Piece piece2 = new Piece("3002", "Brick 2x2", cat, couleur);
        PieceQuantite pq1 = new PieceQuantite(piece1, 5, false, "");
        PieceQuantite pq2 = new PieceQuantite(piece2, 3, false, "");
        
        boite.ajouterPiece(pq1);
        boite.ajouterPiece(pq2);
        
        assertEquals(2, boite.getPieces().size());
    }

    @Test
    public void testAjouterFigurine() {
        Figurine figurine = new Figurine("fig1", "Chevalier", 2, "");
        FigurineQuantite fq = new FigurineQuantite(figurine, 2);
        
        boite.ajouterFigurine(fq);
        
        assertEquals(1, boite.getFigurines().size());
        assertEquals(fq, boite.getFigurines().get(0));
    }

    @Test
    public void testAjouterFigurineNull() {
        int tailleBefore = boite.getFigurines().size();
        boite.ajouterFigurine(null);
        
        assertEquals(tailleBefore, boite.getFigurines().size());
    }

    @Test
    public void testGetBoitesInclusesVide() {
        List<BoiteQuantite> boites = boite.getBoitesIncluses();
        assertNotNull(boites);
        assertTrue(boites.isEmpty());
    }

    @Test
    public void testAjouterBoiteIncluse() {
        Boite boiteIncluse = new Boite("20000", "Petite Boîte", 2020, theme, null);
        BoiteQuantite bq = new BoiteQuantite(boiteIncluse, 1);
        
        boite.ajouterBoiteIncluse(bq);
        
        assertEquals(1, boite.getBoitesIncluses().size());
        assertEquals(bq, boite.getBoitesIncluses().get(0));
    }

    @Test
    public void testAjouterBoiteIncluseNull() {
        int tailleBefore = boite.getBoitesIncluses().size();
        boite.ajouterBoiteIncluse(null);
        
        assertEquals(tailleBefore, boite.getBoitesIncluses().size());
    }

    // Tests de la méthode calculerNbPieces

    @Test
    public void testCalculerNbPiecesVide() {
        assertEquals(0, boite.calculerNbPieces());
    }

    @Test
    public void testCalculerNbPiecesAvecUneSeulePiece() {
        Categorie cat = new Categorie(1, "Briques");
        Piece piece = new Piece("3001", "Brick 2x4", cat, couleur);
        PieceQuantite pq = new PieceQuantite(piece, 10, false, "");
        
        boite.ajouterPiece(pq);
        
        assertEquals(10, boite.calculerNbPieces());
    }

    @Test
    public void testCalculerNbPiecesAvecPlusieursAdditions() {
        Categorie cat1 = new Categorie(1, "Briques");
        Categorie cat2 = new Categorie(2, "Plaques");
        Piece piece1 = new Piece("3001", "Brick 2x4", cat1, couleur);
        Piece piece2 = new Piece("3002", "Brick 2x2", cat1, couleur);
        Piece piece3 = new Piece("3003", "Plate 1x1", cat2, couleur);
        
        boite.ajouterPiece(new PieceQuantite(piece1, 10, false, ""));
        boite.ajouterPiece(new PieceQuantite(piece2, 15, false, ""));
        boite.ajouterPiece(new PieceQuantite(piece3, 8, false, ""));
        
        assertEquals(33, boite.calculerNbPieces());
    }

    @Test
    public void testGetNbPiecesCalcul() {
        Categorie cat = new Categorie(1, "Briques");
        Piece piece = new Piece("3001", "Brick 2x4", cat, couleur);
        boite.ajouterPiece(new PieceQuantite(piece, 42, false, ""));
        
        // Quand nbPieces est null, getNbPieces() doit calculer à partir des pièces
        assertEquals(42, boite.getNbPieces());
    }

    // Tests de toString

    @Test
    public void testToString() {
        String result = boite.toString();
        
        assertTrue(result.contains("10305"));
        assertTrue(result.contains("Castle"));
        assertEquals("10305 - Castle", result);
    }

    @Test
    public void testToStringAvecNomVide() {
        Boite b = new Boite("99999", "", 2025, null, null);
        assertEquals("99999 - ", b.toString());
    }
}
