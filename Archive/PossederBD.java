package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class PossederBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public PossederBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public void insererPosseder(Posseder p) {
        String query = "INSERT INTO POSSEDER VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, p.getIdmag());
            ps.setString(2, p.getIsbn());
            ps.setInt(3, p.getQte());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
    }

    public void effacerPosseder(String idmag, String isbn) {
        String query = "DELETE FROM POSSEDER WHERE idmag = ? AND isbn = ?";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idmag);
            ps.setString(2, isbn);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
    }

    public void majPosseder(Posseder p) {
        String query = "UPDATE POSSEDER SET qte = ? WHERE idmag = ? AND isbn = ?";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setInt(1, p.getQte());
            ps.setString(2, p.getIdmag());
            ps.setString(3, p.getIsbn());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
    }

    public Posseder rechercherPosseder(String idmag, String isbn) {
        String query = "SELECT * FROM POSSEDER WHERE idmag = ? AND isbn = ?";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idmag);
            ps.setString(2, isbn);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return new Posseder(res.getString("idmag"), res.getString("isbn"), res.getInt("qte"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return null;
    }

    public ArrayList<Posseder> listeDesPosessions() {
        ArrayList<Posseder> liste = new ArrayList<>();
        String query = "SELECT * FROM POSSEDER";
        try {
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            while (res.next()) {
                liste.add(new Posseder(res.getString("idmag"), res.getString("isbn"), res.getInt("qte")));
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return liste;
    }

    public int getQte(String idmag, String isbn) {
        String query = "SELECT qte FROM POSSEDER WHERE idmag = ? AND isbn = ?";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idmag);
            ps.setString(2, isbn);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt("qte");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return -1;
    }
}
