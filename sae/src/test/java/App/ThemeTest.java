package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class ThemeTest {

    // Verifie la conversion du nom null en chaine vide
    @Test
    void constructeur_nomNull_devientChaineVide() {
        Theme theme = new Theme(10, null, null);

        assertEquals(10, theme.getIdTheme());
        assertEquals("", theme.getNom());
        assertNull(theme.getParent());
        assertNull(theme.getIdThemePere());
    }
}
