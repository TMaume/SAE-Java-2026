package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class PieceQuantiteTest {

    // Verifie la levee dexception pour une piece null
    @Test
    void constructeur_pieceNull_declencheException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new PieceQuantite(null, 5, false, ""));

        assertEquals("piece", exception.getMessage());
    }

    // Verifie la levee dexception pour une quantite negative
    @Test
    void constructeur_quantiteNegative_declencheException() {
        Piece piece = new Piece("3001", "Brick", new Categorie(1, "Briques"), new Couleur(1, "Rouge", "#FF0000", false));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new PieceQuantite(piece, -1, false, ""));

        assertEquals("quantite", exception.getMessage());
    }
}
