package BD;
import App.FigurineQuantite;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenirfBD {
    private final ConnexionMySQL connexion;

    public ContenirfBD(ConnexionMySQL connexion) {
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

    public FigurineQuantite rechercherContenirf(int idCont, String idFig) {
        String sql = "SELECT cf.quantitef, f.idfig, f.nomfig, f.nbparties " +
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
                    (Integer) rs.getObject("nbparties")
                );
                return new FigurineQuantite(figurine, rs.getInt("quantitef"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public List<FigurineQuantite> listeContenirfParContenu(int idCont) {
        ArrayList<FigurineQuantite> res = new ArrayList<>();
        String sql = "SELECT cf.quantitef, f.idfig, f.nomfig, f.nbparties " +
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
                        (Integer) rs.getObject("nbparties")
                    );
                    res.add(new FigurineQuantite(figurine, rs.getInt("quantitef")));
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }
}