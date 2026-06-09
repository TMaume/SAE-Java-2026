package BD;

import App.Theme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeBD {
    private final ConnexionMySQL connexion;

    public ThemeBD(ConnexionMySQL connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("connexion");
        }
        this.connexion = connexion;
    }

    public ConnexionMySQL getConnexion() {
        return connexion;
    }

    protected Statement createStatement() throws SQLException {
        return connexion.createStatement();
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return connexion.prepareStatement(sql);
    }

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