package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class CollectionItemTest {

    // Verifie la levee dexception pour une boite null
    @Test
    void constructeur_boiteNull_declencheException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CollectionItem(null, EtatBoite.COMPLETE));

        assertEquals("boite", exception.getMessage());
    }

    // Verifie la conversion detat null en etat incomplet
    @Test
    void constructeur_etatNull_devientIncomplete() {
        Boite boite = new Boite("10305", "Castle", 2021, new Theme(1, "Castle", null), null);

        CollectionItem item = new CollectionItem(boite, null);

        assertEquals(EtatBoite.INCOMPLETE, item.getEtat());
    }
}
