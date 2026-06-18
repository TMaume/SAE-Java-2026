package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class PieceTest {

    // Verifie la levee dexception pour un numero vide
    @Test
    void constructeur_numeroVide_declencheException() {
        Categorie categorie = new Categorie(1, "Briques");
        Couleur couleur = new Couleur(1, "Rouge", "#FF0000", false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Piece("", "Brick", categorie, couleur));

        assertEquals("numero", exception.getMessage());
    }
}
