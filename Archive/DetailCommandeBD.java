package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DetailCommandeBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public DetailCommandeBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererDetailCommande(DetailCommande d) throws SQLException {
        String query = "INSERT INTO DETAILCOMMANDE VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, d.getNumcom());
        ps.setInt(2, d.getNumlig());
        ps.setInt(3, d.getQte());
        ps.setDouble(4, d.getPrixvente());
        ps.setString(5, d.getIsbn());
        return ps.executeUpdate();
    }

    public void effacerDetailCommande(int numcom, int numlig) throws SQLException {
        String query = "DELETE FROM DETAILCOMMANDE WHERE numcom = ? AND numlig = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, numcom);
        ps.setInt(2, numlig);
        ps.executeUpdate();
    }

    public void majDetailCommande(DetailCommande d) throws SQLException {
        String query = "UPDATE DETAILCOMMANDE SET qte = ?, prixvente = ?, isbn = ? WHERE numcom = ? AND numlig = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, d.getQte());
        ps.setDouble(2, d.getPrixvente());
        ps.setString(3, d.getIsbn());
        ps.setInt(4, d.getNumcom());
        ps.setInt(5, d.getNumlig());
        ps.executeUpdate();
    }

    public DetailCommande rechercherDetailCommande(int numcom, int numlig) throws SQLException {
        String query = "SELECT * FROM DETAILCOMMANDE WHERE numcom = ? AND numlig = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, numcom);
        ps.setInt(2, numlig);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new DetailCommande(
                res.getInt("numcom"),
                res.getInt("numlig"),
                res.getInt("qte"),
                res.getDouble("prixvente"),
                res.getString("isbn")
            );
        }
        return null;
    }

    public ArrayList<DetailCommande> listeDesDetails() throws SQLException {
        ArrayList<DetailCommande> liste = new ArrayList<>();
        String query = "SELECT * FROM DETAILCOMMANDE";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            liste.add(new DetailCommande(
                res.getInt("numcom"),
                res.getInt("numlig"),
                res.getInt("qte"),
                res.getDouble("prixvente"),
                res.getString("isbn")
            ));
        }
        return liste;
    }

    public List<DetailCommande> getDetailsForCommande(int numCom) throws SQLException {
        List<DetailCommande> details = new ArrayList<>();
        String query = "SELECT * FROM DETAILCOMMANDE WHERE numcom = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, numCom);
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            details.add(new DetailCommande(
                res.getInt("numcom"),
                res.getInt("numlig"),
                res.getInt("qte"),
                res.getDouble("prixvente"),
                res.getString("isbn")
            ));
        }
        return details;
    }

    public int getMaxNumlig(int numcom) {
        int maxNumlig = 0;
        String query = "SELECT MAX(numlig) AS max_numlig FROM DETAILCOMMANDE WHERE numcom = ?";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setInt(1, numcom);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                maxNumlig = res.getInt("max_numlig");
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return maxNumlig;
    }

    public Map<String, Integer> livresLesPlusVendusParMagasin(String idMag) {
        Map<String, Integer> livres = new HashMap<>();
        String query = "SELECT isbn, SUM(qte) AS total FROM DETAILCOMMANDE NATURAL JOIN COMMANDE NATURAL JOIN MAGASIN WHERE idmag=? GROUP BY isbn ORDER BY total DESC";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idMag);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                livres.put(res.getString("isbn"), res.getInt("total"));
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return livres;
    }

    public Map<String, Integer> livresLesPlusVendus() {
        Map<String, Integer> livres = new HashMap<>();
        String query = "SELECT isbn, SUM(qte) AS total FROM DETAILCOMMANDE GROUP BY isbn ORDER BY total DESC";
        try {
            Statement st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            while (res.next()) {
                livres.put(res.getString("isbn"), res.getInt("total"));
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return livres;
    }
}
