package App;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class BoiteStatsTest {

    // Verification repartition immuable
    @Test
    void repartitionCouleurs_estImmuable() {
        Map<Couleur, Integer> couleurs = new LinkedHashMap<>();
        couleurs.put(new Couleur(1, "Rouge", "#FF0000", false), 12);

        BoiteStats stats = new BoiteStats(20, 3, 2, 1, couleurs);

        assertThrows(UnsupportedOperationException.class,
                () -> stats.getRepartitionCouleurs().put(new Couleur(2, "Bleu", "#0000FF", false), 4));
    }
}
