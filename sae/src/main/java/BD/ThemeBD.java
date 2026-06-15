package BD;

import App.Theme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des thèmes LEGO en base de données.
 * <p>
 * Fournit les méthodes CRUD pour les thèmes avec gestion de la hiérarchie parent-enfant.
 * </p>
 */
public class ThemeBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un accès aux données des thèmes.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public ThemeBD(ConnexionMySQL connexion) {
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
     * Insère un thème dans la base de données.
     *
     * @param t le thème à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si t est null
     */
    public int insererTheme(Theme t) {
        if (t == null) {
            throw new IllegalArgumentException("theme");
        }
        String sql = "INSERT INTO THEME (idtheme, nomtheme, idtheme_pere) VALUES (?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, t.getIdTheme());
            ps.setString(2, t.getNom());
            
            if (t.getParent() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, t.getParent().getIdTheme());
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL insererTheme: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Supprime un thème de la base de données.
     *
     * @param idTheme l'identifiant du thème
     * @return le nombre de lignes affectées
     */
    public int effacerTheme(int idTheme) {
        String sql = "DELETE FROM THEME WHERE idtheme = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idTheme);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL effacerTheme: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Met à jour un thème dans la base de données.
     *
     * @param t le thème à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si t est null
     */
    public int majTheme(Theme t) {
        if (t == null) {
            throw new IllegalArgumentException("theme");
        }
        String sql = "UPDATE THEME SET nomtheme = ?, idtheme_pere = ? WHERE idtheme = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, t.getNom());
            if (t.getParent() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, t.getParent().getIdTheme());
            }
            ps.setInt(3, t.getIdTheme());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL majTheme: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Recherche un thème par son identifiant.
     *
     * @param idTheme l'identifiant du thème
     * @return le thème trouvé, ou null si introuvable
     */
    public Theme rechercherTheme(int idTheme) {
        String sql = "SELECT idtheme, nomtheme, idtheme_pere FROM THEME WHERE idtheme = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idTheme);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Integer idPere = (Integer) rs.getObject("idtheme_pere");
                Theme parent = (idPere != null) ? new Theme(idPere, "À charger", null) : null;
                
                return new Theme(
                    rs.getInt("idtheme"),
                    rs.getString("nomtheme"),
                    parent
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL rechercherTheme: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne la liste de tous les thèmes.
     *
     * @return liste des thèmes
     */
    public List<Theme> listeDesThemes() {
        ArrayList<Theme> res = new ArrayList<>();
        String sql = "SELECT idtheme, nomtheme, idtheme_pere FROM THEME ORDER BY nomtheme";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Integer idPere = (Integer) rs.getObject("idtheme_pere");
                Theme parent = (idPere != null) ? new Theme(idPere, "À charger", null) : null;
                
                res.add(new Theme(
                    rs.getInt("idtheme"),
                    rs.getString("nomtheme"),
                    parent
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listeDesThemes: " + e.getMessage());
        }
        return res;
    }

    /**
     * Retourne les sous-thèmes d'un thème parent.
     *
     * @param idThemePere l'identifiant du thème parent
     * @return liste des sous-thèmes
     */
    public List<Theme> listeSousThemes(int idThemePere) {
        ArrayList<Theme> res = new ArrayList<>();
        String sql = "SELECT idtheme, nomtheme, idtheme_pere FROM THEME WHERE idtheme_pere = ? ORDER BY nomtheme";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idThemePere);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme parent = new Theme(idThemePere, "À charger", null);
                    res.add(new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        parent
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listeSousThemes: " + e.getMessage());
        }
        return res;
    }
}