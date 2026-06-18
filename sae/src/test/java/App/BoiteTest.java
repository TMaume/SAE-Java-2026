package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoiteTest {

    private Boite boite;
    private Theme theme;
    private Couleur couleur;

    // Initialisation objets partages
    @BeforeEach
    public void setUp() {
        theme = new Theme(1, "Castle", null);
        couleur = new Couleur(1, "Red", "#FF0000", false);
        boite = new Boite("10305", "Castle", 2021, theme, "url_image.jpg");
    }

    // Verification numero invalide
    @Test
    public void testConstructeurErreurSiNumeroInvalide() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Boite("", "Boite Invalide", 2026, null, null);
        });

        assertEquals("numero", exception.getMessage());
    }

    // Verification calcul pieces
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
}
