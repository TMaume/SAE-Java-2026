package BD;
import App.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des catégories de pièces en base de données.
 * <p>
 * Fournit les méthodes CRUD pour les catégories.
 * </p>
 */
public class CategorieBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un accès aux données des catégories.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public CategorieBD(ConnexionMySQL connexion) {
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
     * Insère une catégorie dans la base de données.
     *
     * @param c la catégorie à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si c est null
     */
    public int insererCategorie(Categorie c) {
        if (c == null) {
            throw new IllegalArgumentException("categorie");
        }
        String sql = "INSERT INTO CATEGORIE (idcat, nomcat) VALUES (?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.setString(2, c.getNom());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime une catégorie de la base de données.
     *
     * @param idCat l'identifiant de la catégorie
     * @return le nombre de lignes affectées
     */
    public int effacerCategorie(int idCat) {
        String sql = "DELETE FROM CATEGORIE WHERE idcat = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCat);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour une catégorie dans la base de données.
     *
     * @param c la catégorie à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si c est null
     */
    public int majCategorie(Categorie c) {
        if (c == null) {
            throw new IllegalArgumentException("categorie");
        }
        String sql = "UPDATE CATEGORIE SET nomcat = ? WHERE idcat = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setInt(2, c.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche une catégorie par son identifiant.
     *
     * @param idCat l'identifiant de la catégorie
     * @return la catégorie trouvée, ou null si introuvable
     */
    public Categorie rechercherCategorie(int idCat) {
        String sql = "SELECT idcat, nomcat FROM CATEGORIE WHERE idcat = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCat);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne la liste de toutes les catégories.
     *
     * @return liste des catégories
     */
    public List<Categorie> listeDesCategories() {
        ArrayList<Categorie> res = new ArrayList<>();
        String sql = "SELECT idcat, nomcat FROM CATEGORIE ORDER BY nomcat";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new Categorie(rs.getInt("idcat"), rs.getString("nomcat")));
            }
        } catch (SQLException e) {
        }
        return res;
    }
}