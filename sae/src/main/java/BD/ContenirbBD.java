package BD;
import App.BoiteQuantite;
import App.Boite;
import App.Theme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données d'association entre contenu et boîtes en base de données.
 * <p>
 * Fournit les méthodes CRUD pour gérer les boîtes contenues dans les boîtes.
 * </p>
 */
public class ContenirbBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire du contenu en boîtes.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public ContenirbBD(ConnexionMySQL connexion) {
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
     * Insère une association boîte-contenu dans la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param bq la boîte avec quantité (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si bq est null
     */
    public int insererContenirb(int idCont, BoiteQuantite bq) {
        if (bq == null) {
            throw new IllegalArgumentException("contenirb");
        }
        String sql = "INSERT INTO CONTENIRB (idcont, numboite, quantiteb) VALUES (?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, bq.getBoite().getNumero());
            ps.setInt(3, bq.getQuantite());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime une association boîte-contenu de la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param numBoite le numéro de la boîte
     * @return le nombre de lignes affectées
     */
    public int effacerContenirb(int idCont, String numBoite) {
        String sql = "DELETE FROM CONTENIRB WHERE idcont = ? AND numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, numBoite);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour une association boîte-contenu dans la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param bq la boîte avec quantité mise à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si bq est null
     */
    public int majContenirb(int idCont, BoiteQuantite bq) {
        if (bq == null) {
            throw new IllegalArgumentException("contenirb");
        }
        String sql = "UPDATE CONTENIRB SET quantiteb = ? WHERE idcont = ? AND numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, bq.getQuantite());
            ps.setInt(2, idCont);
            ps.setString(3, bq.getBoite().getNumero());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche une association boîte-contenu spécifique.
     *
     * @param idCont l'identifiant du contenu
     * @param numBoite le numéro de la boîte
     * @return la boîte avec quantité, ou null si non trouvée
     */
    public BoiteQuantite rechercherContenirb(int idCont, String numBoite) {
        String sql = "SELECT cb.quantiteb, b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme " +
                     "FROM CONTENIRB cb " +
                     "JOIN BOITE b ON cb.numboite = b.numboite " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "WHERE cb.idcont = ? AND cb.numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, numBoite);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Theme theme = new Theme(
                    rs.getInt("idtheme"),
                    rs.getString("nomtheme"),
                    null
                );
                Boite boite = new Boite(
                    rs.getString("numboite"),
                    rs.getString("nomboite"),
                    (Integer) rs.getObject("annee"),
                    theme
                );
                boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                return new BoiteQuantite(boite, rs.getInt("quantiteb"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne les boîtes contenues dans un contenu spécifique.
     *
     * @param idCont l'identifiant du contenu
     * @return liste des boîtes avec quantités
     */
    public List<BoiteQuantite> listeContenirbParContenu(int idCont) {
        ArrayList<BoiteQuantite> res = new ArrayList<>();
        String sql = "SELECT cb.quantiteb, b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme " +
                     "FROM CONTENIRB cb " +
                     "JOIN BOITE b ON cb.numboite = b.numboite " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "WHERE cb.idcont = ? ORDER BY b.numboite";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme theme = new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        null
                    );
                    Boite boite = new Boite(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        theme
                    );
                    boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                    res.add(new BoiteQuantite(boite, rs.getInt("quantiteb")));
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }
}