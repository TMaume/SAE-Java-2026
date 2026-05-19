package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class EcrireBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public EcrireBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererEcrire(Ecrire e) throws SQLException {
        String query = "INSERT INTO ECRIRE (isbn, idauteur) VALUES (?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, e.getIsbn());
        ps.setString(2, e.getIdauteur());
        return ps.executeUpdate();
    }

    public void effacerEcrire(String isbn, String idauteur) throws SQLException {
        String query = "DELETE FROM ECRIRE WHERE isbn = ? AND idauteur = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, isbn);
        ps.setString(2, idauteur);
        ps.executeUpdate();
    }

    public void majEcrire(Ecrire e) throws SQLException {
        throw new SQLException("Pas de mise à jour pour ECRIRE, supprimer et réinsérer");
    }

    public Ecrire rechercherEcrire(String isbn, String idauteur) throws SQLException {
        String query = "SELECT * FROM ECRIRE WHERE isbn = ? AND idauteur = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, isbn);
        ps.setString(2, idauteur);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Ecrire(res.getString("isbn"), res.getString("idauteur"));
        }
        return null;
    }

    public ArrayList<Ecrire> listeDesEcritures() throws SQLException {
        ArrayList<Ecrire> liste = new ArrayList<>();
        String query = "SELECT * FROM ECRIRE";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            liste.add(new Ecrire(res.getString("isbn"), res.getString("idauteur")));
        }
        return liste;
    }
}
