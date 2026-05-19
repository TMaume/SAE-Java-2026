package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class EditerBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public EditerBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererEditer(Editer e) throws SQLException {
        String query = "INSERT INTO EDITER (isbn, idedit) VALUES (?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, e.getIsbn());
        ps.setInt(2, e.getIdedit());
        return ps.executeUpdate();
    }

    public void effacerEditer(String isbn, int idedit) throws SQLException {
        String query = "DELETE FROM EDITER WHERE isbn = ? AND idedit = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, isbn);
        ps.setInt(2, idedit);
        ps.executeUpdate();
    }

    public void majEditer(Editer e) throws SQLException {
        throw new SQLException("Pas de mise à jour pour EDITER, supprimer et réinsérer");
    }

    public Editer rechercherEditer(String isbn, int idedit) throws SQLException {
        String query = "SELECT * FROM EDITER WHERE isbn = ? AND idedit = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, isbn);
        ps.setInt(2, idedit);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Editer(res.getString("isbn"), res.getInt("idedit"));
        }
        return null;
    }

    public ArrayList<Editer> listeDesEditer() throws SQLException {
        ArrayList<Editer> liste = new ArrayList<>();
        String query = "SELECT * FROM EDITER";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            liste.add(new Editer(res.getString("isbn"), res.getInt("idedit")));
        }
        return liste;
    }
}
