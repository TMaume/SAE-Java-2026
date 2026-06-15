package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les relations parent-enfant entre thèmes LEGO.
 * <p>
 * Fournit les méthodes pour gérer la hiérarchie des thèmes.
 * </p>
 */
public class ThemeParentBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire de hiérarchie des thèmes.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public ThemeParentBD(ConnexionMySQL connexion) {
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
     * Définit le thème parent d'un thème.
     *
     * @param idTheme l'identifiant du thème
     * @param idThemePere l'identifiant du thème parent (null pour aucun parent)
     * @return le nombre de lignes affectées
     */
    public int definirParent(int idTheme, Integer idThemePere) {
        String sql = "UPDATE THEME SET idtheme_pere = ? WHERE idtheme = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            if (idThemePere == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idThemePere);
            }
            ps.setInt(2, idTheme);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche le thème parent d'un thème.
     *
     * @param idTheme l'identifiant du thème
     * @return l'identifiant du thème parent, ou null si pas de parent
     */
    public Integer rechercherParent(int idTheme) {
        String sql = "SELECT idtheme_pere FROM THEME WHERE idtheme = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idTheme);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return (Integer) rs.getObject("idtheme_pere");
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne les identifiants des sous-thèmes d'un thème.
     *
     * @param idThemePere l'identifiant du thème parent
     * @return liste des identifiants des sous-thèmes
     */
    public List<Integer> listeSousThemes(int idThemePere) {
        ArrayList<Integer> res = new ArrayList<>();
        String sql = "SELECT idtheme FROM THEME WHERE idtheme_pere = ? ORDER BY idtheme";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idThemePere);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(rs.getInt("idtheme"));
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }
}