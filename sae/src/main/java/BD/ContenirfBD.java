package BD;
import App.FigurineQuantite;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données d'association entre contenu et figurines en base de données.
 * <p>
 * Fournit les méthodes CRUD pour gérer les figurines contenues dans les boîtes.
 * </p>
 */
public class ContenirfBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire du contenu en figurines.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public ContenirfBD(ConnexionMySQL connexion) {
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
     * Insère une association figurine-contenu dans la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param fq la figurine avec quantité (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si fq est null
     */
    public int insererContenirf(int idCont, FigurineQuantite fq) {
        if (fq == null) {
            throw new IllegalArgumentException("contenirf");
        }
        String sql = "INSERT INTO CONTENIRF (idcont, idfig, quantitef) VALUES (?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, fq.getFigurine().getIdFigurine());
            ps.setInt(3, fq.getQuantite());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime une association figurine-contenu de la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param idFig l'identifiant de la figurine
     * @return le nombre de lignes affectées
     */
    public int effacerContenirf(int idCont, String idFig) {
        String sql = "DELETE FROM CONTENIRF WHERE idcont = ? AND idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, idFig);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour une association figurine-contenu dans la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param fq la figurine avec quantité mise à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si fq est null
     */
    public int majContenirf(int idCont, FigurineQuantite fq) {
        if (fq == null) {
            throw new IllegalArgumentException("contenirf");
        }
        String sql = "UPDATE CONTENIRF SET quantitef = ? WHERE idcont = ? AND idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, fq.getQuantite());
            ps.setInt(2, idCont);
            ps.setString(3, fq.getFigurine().getIdFigurine());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche une association figurine-contenu spécifique.
     *
     * @param idCont l'identifiant du contenu
     * @param idFig l'identifiant de la figurine
     * @return la figurine avec quantité, ou null si non trouvée
     */
    public FigurineQuantite rechercherContenirf(int idCont, String idFig) {
        String sql = "SELECT cf.quantitef, f.idfig, f.nomfig, f.nbparties, f.imageF " +
                     "FROM CONTENIRF cf " +
                     "JOIN FIGURINE f ON cf.idfig = f.idfig " +
                     "WHERE cf.idcont = ? AND cf.idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, idFig);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                App.Figurine figurine = new App.Figurine(
                    rs.getString("idfig"),
                    rs.getString("nomfig"),
                    (Integer) rs.getObject("nbparties"),
                    rs.getString("imageF")
                );
                return new FigurineQuantite(figurine, rs.getInt("quantitef"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne les figurines contenues dans un contenu spécifique.
     *
     * @param idCont l'identifiant du contenu
     * @return liste des figurines avec quantités
     */
    public List<FigurineQuantite> listeContenirfParContenu(int idCont) {
        ArrayList<FigurineQuantite> res = new ArrayList<>();
        String sql = "SELECT cf.quantitef, f.idfig, f.nomfig, f.nbparties, f.imageF " +
                     "FROM CONTENIRF cf " +
                     "JOIN FIGURINE f ON cf.idfig = f.idfig " +
                     "WHERE cf.idcont = ? ORDER BY f.idfig";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    App.Figurine figurine = new App.Figurine(
                        rs.getString("idfig"),
                        rs.getString("nomfig"),
                        (Integer) rs.getObject("nbparties"),
                        rs.getString("imageF")
                    );
                    res.add(new FigurineQuantite(figurine, rs.getInt("quantitef")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des figurines contenues : " + e.getMessage());
        }
        return res;
    }
}