package BD;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import App.Categorie;
import App.Couleur;
import App.Piece;
import App.PieceQuantite;
import testsupport.JdbcTestSupport;

public class ContenirpBDTest {

    /**
     * Verification insertion piece contenu
     */
    @Test
    void insererContenirp_bindeLesParametresEssentiels() {
        Piece piece = new Piece("3001", "Brick 2x4", new Categorie(1, "Briques"), new Couleur(2, "Rouge", "FF0000", false));
        PieceQuantite pq = new PieceQuantite(piece, 7, true, "img.png");
        JdbcTestSupport.StubPreparedStatement stub = JdbcTestSupport.stubPreparedStatement(1, List.of());
        Connection connection = JdbcTestSupport.connectionFor(stub);
        ContenirpBD dao = new ContenirpBD(JdbcTestSupport.connexionFor(connection));

        int lignes = dao.insererContenirp(12, pq);

        assertEquals(1, lignes);
        assertEquals(12, stub.param(1));
        assertEquals("3001", stub.param(2));
        assertEquals(2, stub.param(3));
        assertEquals("t", stub.param(4));
        assertEquals(7, stub.param(5));
    }

    /**
     * Verification reconstruction piece quantite
     */
    @Test
    void rechercherContenirp_reconstruitUnePieceQuantite() {
        JdbcTestSupport.StubPreparedStatement stub = JdbcTestSupport.stubPreparedStatement(0, List.of(
                Map.ofEntries(
                        entry("quantitep", 4),
                        entry("en_supplement", "f"),
                        entry("imageP", "piece.png"),
                        entry("numpiece", "3001"),
                        entry("nompiece", "Brick 2x4"),
                        entry("idcat", 1),
                        entry("nomcat", "Briques"),
                        entry("idcoul", 2),
                        entry("nomcoul", "Rouge"),
                        entry("RGB", "FF0000"),
                        entry("transparent", "f"))));
        Connection connection = JdbcTestSupport.connectionFor(stub);
        ContenirpBD dao = new ContenirpBD(JdbcTestSupport.connexionFor(connection));

        PieceQuantite resultat = dao.rechercherContenirp(8, "3001", 2, false);

        assertNotNull(resultat);
        assertEquals(4, resultat.getQuantite());
        assertEquals(false, resultat.isEnSupplement());
        assertEquals("3001", resultat.getPiece().getNumero());
        assertEquals("Brick 2x4", resultat.getPiece().getNom());
        assertEquals("Rouge", resultat.getPiece().getCouleur().getNom());
    }
}
