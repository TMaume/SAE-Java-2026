package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class FigurineQuantiteTest {

    // Verifie la levee dexception pour une figurine null
    @Test
    void constructeur_figurineNull_declencheException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new FigurineQuantite(null, 1));

        assertEquals("figurine", exception.getMessage());
    }

    // Verifie la levee dexception pour une quantite negative
    @Test
    void constructeur_quantiteNegative_declencheException() {
        Figurine figurine = new Figurine("fig1", "Chevalier", 1, "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new FigurineQuantite(figurine, -1));

        assertEquals("quantite", exception.getMessage());
    }
}
