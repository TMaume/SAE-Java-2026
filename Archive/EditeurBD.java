package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class EditeurBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public EditeurBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererEditeur(Editeur e) throws SQLException {
        String query = "INSERT INTO EDITEUR (idedit, nomedit) VALUES (?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, e.getIdedit());
        ps.setString(2, e.getNomedit());
        return ps.executeUpdate();
    }

    public void effacerEditeur(int idedit) throws SQLException {
        String query = "DELETE FROM EDITEUR WHERE idedit = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, idedit);
        ps.executeUpdate();
    }

    public void majEditeur(Editeur e) throws SQLException {
        String query = "UPDATE EDITEUR SET nomedit = ? WHERE idedit = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, e.getNomedit());
        ps.setInt(2, e.getIdedit());
        ps.executeUpdate();
    }

    public Editeur rechercherEditeurParId(int idedit) throws SQLException {
        String query = "SELECT * FROM EDITEUR WHERE idedit = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, idedit);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Editeur(
                res.getInt("idedit"),
                res.getString("nomedit")
            );
        }
        return null;
    }

    public ArrayList<Editeur> listeDesEditeurs() throws SQLException {
        ArrayList<Editeur> editeurs = new ArrayList<>();
        String query = "SELECT * FROM EDITEUR";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            editeurs.add(new Editeur(
                res.getInt("idedit"),
                res.getString("nomedit")
            ));
        }
        return editeurs;
    }
}
