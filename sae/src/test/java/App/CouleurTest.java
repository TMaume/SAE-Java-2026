package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CouleurTest {

    // Verifie la conversion des valeurs null en chaines vides
    @Test
    void constructeur_valeursNullDeviennentChainesVides() {
        Couleur couleur = new Couleur(1, null, null, true);

        assertEquals(1, couleur.getIdCouleur());
        assertEquals("", couleur.getNom());
        assertEquals("", couleur.getRgb());
    }
}
