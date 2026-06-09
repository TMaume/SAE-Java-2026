package BD;
import App.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieBD {
    private final ConnexionMySQL connexion;

    public CategorieBD(ConnexionMySQL connexion) {
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

    public int effacerCategorie(int idCat) {
        String sql = "DELETE FROM CATEGORIE WHERE idcat = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCat);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

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