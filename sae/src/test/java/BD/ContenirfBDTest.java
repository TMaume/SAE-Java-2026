package BD;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import App.Figurine;
import App.FigurineQuantite;
import testsupport.JdbcTestSupport;

public class ContenirfBDTest {

    /**
     * Verification insertion figurine contenu
     */
    @Test
    void insererContenirf_bindeLesParametresEssentiels() {
        FigurineQuantite fq = new FigurineQuantite(new Figurine("fig-1", "Minifig", 3, "img.png"), 2);
        JdbcTestSupport.StubPreparedStatement stub = JdbcTestSupport.stubPreparedStatement(1, List.of());
        Connection connection = JdbcTestSupport.connectionFor(stub);
        ContenirfBD dao = new ContenirfBD(JdbcTestSupport.connexionFor(connection));

        int lignes = dao.insererContenirf(15, fq);

        assertEquals(1, lignes);
        assertEquals(15, stub.param(1));
        assertEquals("fig-1", stub.param(2));
        assertEquals(2, stub.param(3));
    }

    /**
     * Verification reconstruction figurine quantite
     */
    @Test
    void rechercherContenirf_reconstruitUneFigurineQuantite() {
        JdbcTestSupport.StubPreparedStatement stub = JdbcTestSupport.stubPreparedStatement(0, List.of(
                Map.ofEntries(
                        entry("quantitef", 5),
                        entry("idfig", "fig-1"),
                        entry("nomfig", "Minifig"),
                        entry("nbparties", 3),
                        entry("imageF", "fig.png"))));
        Connection connection = JdbcTestSupport.connectionFor(stub);
        ContenirfBD dao = new ContenirfBD(JdbcTestSupport.connexionFor(connection));

        FigurineQuantite resultat = dao.rechercherContenirf(15, "fig-1");

        assertNotNull(resultat);
        assertEquals(5, resultat.getQuantite());
        assertEquals("fig-1", resultat.getFigurine().getIdFigurine());
        assertEquals("Minifig", resultat.getFigurine().getNom());
    }
}
