package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CategorieTest {

    // Verifie la conversion du nom null en chaine vide
    @Test
    void constructeur_nomNull_devientChaineVide() {
        Categorie categorie = new Categorie(1, null);

        assertEquals(1, categorie.getId());
        assertEquals("", categorie.getNom());
    }
}
