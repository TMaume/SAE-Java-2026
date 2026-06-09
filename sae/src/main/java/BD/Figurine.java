package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Figurine {
    private final ConnexionMySQL connexion;

    public Figurine(ConnexionMySQL connexion) {
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
            return 0;
        }
    }

    public int effacerFigurine(String idFig) {
        String sql = "DELETE FROM FIGURINE WHERE idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, idFig);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

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
            return 0;
        }
    }

    public App.Figurine rechercherFigurine(String idFig) {
        String sql = "SELECT idfig, nomfig, nbparties FROM FIGURINE WHERE idfig = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, idFig);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new App.Figurine(
                    rs.getString("idfig"),
                    rs.getString("nomfig"),
                    (Integer) rs.getObject("nbparties")
                );
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public List<App.Figurine> listeDesFigurines() {
        ArrayList<App.Figurine> res = new ArrayList<>();
        String sql = "SELECT idfig, nomfig, nbparties FROM FIGURINE ORDER BY nomfig";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new App.Figurine(
                    rs.getString("idfig"),
                    rs.getString("nomfig"),
                    (Integer) rs.getObject("nbparties")
                ));
            }
        } catch (SQLException e) {
        }
        return res;
    }
}