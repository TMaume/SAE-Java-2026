package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class BoiteQuantiteTest {

    // Verifie la levee dexception pour une boite null
    @Test
    void constructeur_boiteNull_declencheException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new BoiteQuantite(null, 1));

        assertEquals("boite", exception.getMessage());
    }

    // Verifie la levee dexception pour une quantite negative
    @Test
    void constructeur_quantiteNegative_declencheException() {
        Boite boite = new Boite("10305", "Castle", 2021, new Theme(1, "Castle", null), null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new BoiteQuantite(boite, -1));

        assertEquals("quantite", exception.getMessage());
    }
}
