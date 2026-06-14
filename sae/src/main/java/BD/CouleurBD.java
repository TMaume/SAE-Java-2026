package BD;
import App.Couleur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des couleurs en base de données.
 * <p>
 * Fournit les méthodes CRUD pour les couleurs avec gestion RGB et transparence.
 * </p>
 */
public class CouleurBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un accès aux données des couleurs.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public CouleurBD(ConnexionMySQL connexion) {
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
     * Convertit un booléen en String "t" ou "f".
     *
     * @param value le booléen à convertir
     * @return "t" si value est true, "f" sinon
     */
    private static String boolToTf(boolean value) {
        return value ? "t" : "f";
    }

    /**
     * Insère une couleur dans la base de données.
     *
     * @param c la couleur à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si c est null
     */
    public int insererCouleur(Couleur c) {
        if (c == null) {
            throw new IllegalArgumentException("couleur");
        }
        String sql = "INSERT INTO COULEUR (idcoul, nomcoul, RGB, transparent) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, c.getIdCouleur());
            ps.setString(2, c.getNom());
            ps.setString(3, c.getRgb());
            ps.setString(4, boolToTf(c.isTransparent()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime une couleur de la base de données.
     *
     * @param idCoul l'identifiant de la couleur
     * @return le nombre de lignes affectées
     */
    public int effacerCouleur(int idCoul) {
        String sql = "DELETE FROM COULEUR WHERE idcoul = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCoul);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour une couleur dans la base de données.
     *
     * @param c la couleur à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si c est null
     */
    public int majCouleur(Couleur c) {
        if (c == null) {
            throw new IllegalArgumentException("couleur");
        }
        String sql = "UPDATE COULEUR SET nomcoul = ?, RGB = ?, transparent = ? WHERE idcoul = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getRgb());
            ps.setString(3, boolToTf(c.isTransparent()));
            ps.setInt(4, c.getIdCouleur());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche une couleur par son identifiant.
     *
     * @param idCoul l'identifiant de la couleur
     * @return la couleur trouvée, ou null si introuvable
     */
    public Couleur rechercherCouleur(int idCoul) {
        String sql = "SELECT idcoul, nomcoul, RGB, transparent FROM COULEUR WHERE idcoul = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCoul);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Couleur(
                    rs.getInt("idcoul"),
                    rs.getString("nomcoul"),
                    rs.getString("RGB"),
                    tfToBool(rs.getString("transparent"))
                );
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne la liste de toutes les couleurs.
     *
     * @return liste des couleurs
     */
    public List<Couleur> listeDesCouleurs() {
        ArrayList<Couleur> res = new ArrayList<>();
        String sql = "SELECT idcoul, nomcoul, RGB, transparent FROM COULEUR ORDER BY nomcoul";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new Couleur(
                    rs.getInt("idcoul"),
                    rs.getString("nomcoul"),
                    rs.getString("RGB"),
                    tfToBool(rs.getString("transparent"))
                ));
            }
        } catch (SQLException e) {
        }
        return res;
    }
}