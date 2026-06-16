package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des figurines en base de données.
 * <p>
 * Fournit les méthodes CRUD pour les figurines LEGO.
 * </p>
 */
public class FigurineBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire des figurines.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public FigurineBD(ConnexionMySQL connexion) {
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
     * Insère une figurine dans la base de données.
     *
     * @param f la figurine à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si f est null
     */
    public int insererFigurine(App.Figurine f) {
        if (f == null) {
            throw new IllegalArgumentException("figurine");
        }
        String sql = "INSERT INTO FIGURINE (idfig, nomfig, nbparties) VALUES (?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, f.getIdFigurine());
            ps.setString(2, f.getNom());
            ps.setInt(3, f.getNbParties());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la figurine : " + e.getMessage());
            return 0;
        }
    }

    /**
     * Supprime une figurine de la base de données.
     *
     * @param idFig l'identifiant de la figurine (non null)
     * @return le nombre de lignes affectées
     */
    public int effacerFigurine(String idFig) {
        String sql = "DELETE FROM FIGURINE WHERE idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, idFig);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la figurine : " + e.getMessage());
            return 0;
        }
    }

    /**
     * Met à jour une figurine dans la base de données.
     *
     * @param f la figurine à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si f est null
     */
    public int majFigurine(App.Figurine f) {
        if (f == null) {
            throw new IllegalArgumentException("figurine");
        }
        String sql = "UPDATE FIGURINE SET nomfig = ?, nbparties = ? WHERE idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, f.getNom());
            ps.setInt(2, f.getNbParties());
            ps.setString(3, f.getIdFigurine());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la figurine : " + e.getMessage());
            return 0;
        }
    }

    /**
     * Recherche une figurine par son identifiant.
     *
     * @param idFig l'identifiant de la figurine (non null)
     * @return la figurine trouvée, ou null si introuvable
     */
    public App.Figurine rechercherFigurine(String idFig) {
        String sql = "SELECT idfig, nomfig, nbparties, imageF FROM FIGURINE WHERE idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, idFig);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new App.Figurine(
                    rs.getString("idfig"),
                    rs.getString("nomfig"),
                    (Integer) rs.getObject("nbparties"),
                    rs.getString("imageF")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la figurine : " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne la liste de toutes les figurines.
     *
     * @return liste des figurines
     */
    public List<App.Figurine> listeDesFigurines() {
        ArrayList<App.Figurine> res = new ArrayList<>();
        String sql = "SELECT idfig, nomfig, nbparties, imageF FROM FIGURINE ORDER BY nomfig";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new App.Figurine(
                    rs.getString("idfig"),
                    rs.getString("nomfig"),
                    (Integer) rs.getObject("nbparties"),
                    rs.getString("imageF")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la liste des figurines : " + e.getMessage());
        }
        return res;
    }
}