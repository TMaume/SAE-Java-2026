package BD;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import App.Boite;
import App.BoiteQuantite;
import App.Theme;
import testsupport.JdbcTestSupport;

public class ContenirbBDTest {

    /**
     * Verification insertion boite contenu
     */
    @Test
    void insererContenirb_bindeLesParametresEssentiels() {
        BoiteQuantite bq = new BoiteQuantite(new Boite("10305", "Castle", 2021, new Theme(1, "Castle", null), "img.png"), 2);
        JdbcTestSupport.StubPreparedStatement stub = JdbcTestSupport.stubPreparedStatement(1, List.of());
        Connection connection = JdbcTestSupport.connectionFor(stub);
        ContenirbBD dao = new ContenirbBD(JdbcTestSupport.connexionFor(connection));

        int lignes = dao.insererContenirb(22, bq);

        assertEquals(1, lignes);
        assertEquals(22, stub.param(1));
        assertEquals("10305", stub.param(2));
        assertEquals(2, stub.param(3));
    }

    /**
     * Verification reconstruction boite quantite
     */
    @Test
    void rechercherContenirb_reconstruitUneBoiteQuantite() {
        JdbcTestSupport.StubPreparedStatement stub = JdbcTestSupport.stubPreparedStatement(0, List.of(
                Map.ofEntries(
                        entry("quantiteb", 3),
                        entry("numboite", "10305"),
                        entry("nomboite", "Castle"),
                        entry("annee", 2021),
                        entry("nbpieces", 1234),
                        entry("idtheme", 1),
                        entry("nomtheme", "Castle"),
                        entry("imageB", "box.png"))));
        Connection connection = JdbcTestSupport.connectionFor(stub);
        ContenirbBD dao = new ContenirbBD(JdbcTestSupport.connexionFor(connection));

        BoiteQuantite resultat = dao.rechercherContenirb(22, "10305");

        assertNotNull(resultat);
        assertEquals(3, resultat.getQuantite());
        assertEquals("10305", resultat.getBoite().getNumero());
        assertEquals("Castle", resultat.getBoite().getNom());
    }
}
