package App;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;


public class BoiteTest {

    @Test
    public void testConstructeurErreurSiNumeroVide() {
        // On vérifie que la création d'une boîte vide déclenche bien l'erreur "IllegalArgumentException"

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            Boite fausseBoite = new Boite("", "Boite Invalide", 2026, null, null);
        });
        
        // On vérifie que le message de l'erreur est bien "numero" comme tu l'as codé
        assertEquals("numero", exception.getMessage());
    }
}
