package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class ThemesBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public ThemesBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererTheme(Themes t) throws SQLException {
        String query = "INSERT INTO THEMES (isbn, iddewey) VALUES (?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, t.getIsbn());
        ps.setString(2, t.getIddewey());
        return ps.executeUpdate();
    }

    public void effacerTheme(String isbn, String iddewey) throws SQLException {
        String query = "DELETE FROM THEMES WHERE isbn = ? AND iddewey = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, isbn);
        ps.setString(2, iddewey);
        ps.executeUpdate();
    }

    public void majTheme(Themes t) throws SQLException {
        throw new SQLException("Pas de mise à jour pour THEMES, supprimer et réinsérer");
    }

    public Themes rechercherTheme(String isbn, String iddewey) throws SQLException {
        String query = "SELECT * FROM THEMES WHERE isbn = ? AND iddewey = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, isbn);
        ps.setString(2, iddewey);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Themes(res.getString("isbn"), res.getString("iddewey"));
        }
        return null;
    }

    public ArrayList<Themes> listeDesThemes() throws SQLException {
        ArrayList<Themes> liste = new ArrayList<>();
        String query = "SELECT * FROM THEMES";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            liste.add(new Themes(res.getString("isbn"), res.getString("iddewey")));
        }
        return liste;
    }
}
