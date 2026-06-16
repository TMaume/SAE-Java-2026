package BD;
import App.PieceQuantite;
import App.Piece;
import App.Categorie;
import App.Couleur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des pièces supplémentaires en base de données.
 * <p>
 * Fournit les méthodes spécialisées pour récupérer les pièces marquées comme supplémentaires.
 * </p>
 */
public class SupplementBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire des pièces supplémentaires.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public SupplementBD(ConnexionMySQL connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("connexion");
        }
        this.connexion = connexion;
    }

    /**
     * Retourne la connexion MySQL.
     *
     * @return la connexion
     */
    public ConnexionMySQL getConnexion() {
        return connexion;
    }

    /**
     * Crée une nouvelle instruction SQL.
     *
     * @return une instruction SQL
     * @throws SQLException si l'opération échoue
     */
    protected Statement createStatement() throws SQLException {
        return connexion.createStatement();
    }

    /**
     * Prépare une requête SQL paramétrée.
     *
     * @param sql la requête SQL
     * @return une instruction SQL préparée
     * @throws SQLException si l'opération échoue
     */
    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return connexion.prepareStatement(sql);
    }

    /**
     * Convertit un String "t" ou "f" en booléen.
     *
     * @param value la valeur à convertir
     * @return true si value commence par 't', 'T' ou '1'
     */
    private static boolean tfToBool(String value) {
        return value != null && !value.isEmpty() && (value.charAt(0) == 't' || value.charAt(0) == 'T' || value.charAt(0) == '1');
    }

    /**
     * Retourne les pièces supplémentaires d'un contenu.
     *
     * @param idCont l'identifiant du contenu
     * @return liste des pièces supplémentaires
     */
    public List<PieceQuantite> listeSupplementsParContenu(int idCont) {
        ArrayList<PieceQuantite> res = new ArrayList<>();
        String sql = "SELECT cp.quantitep, cp.en_supplement, p.numpiece, p.nompiece, c.idcat, c.nomcat, coul.idcoul, coul.nomcoul, coul.RGB, coul.transparent " +
                     "FROM CONTENIRP cp " +
                     "JOIN PIECE p ON cp.numpiece = p.numpiece " +
                     "LEFT JOIN CATEGORIE c ON p.idcat = c.idcat " +
                     "JOIN COULEUR coul ON cp.idcoul = coul.idcoul " +
                     "WHERE cp.idcont = ? AND cp.en_supplement = 't' " +
                     "ORDER BY p.numpiece, coul.idcoul";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categorie cat = null;
                    if (rs.getObject("idcat") != null) {
                        cat = new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
                    }
                    Couleur couleur = new Couleur(
                        rs.getInt("idcoul"),
                        rs.getString("nomcoul"),
                        rs.getString("RGB"),
                        tfToBool(rs.getString("transparent"))
                    );
                    Piece piece = new Piece(
                        rs.getString("numpiece"),
                        rs.getString("nompiece"),
                        cat,
                        couleur
                    );
                    res.add(new PieceQuantite(piece, rs.getInt("quantitep"), tfToBool(rs.getString("en_supplement")), rs.getString("imageP")));
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }

    /**
     * Compte le nombre de pièces supplémentaires d'un contenu.
     *
     * @param idCont l'identifiant du contenu
     * @return le nombre de pièces supplémentaires
     */
    public int compterSupplementsParContenu(int idCont) {
        String sql = "SELECT COUNT(*) AS nb FROM CONTENIRP WHERE idcont = ? AND en_supplement = 't'";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }
                return rs.getInt("nb");
            }
        } catch (SQLException e) {
            return 0;
        }
    }
}