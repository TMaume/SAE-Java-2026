package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MagasinBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public MagasinBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererMagasin(Magasin m) {
        String query = "INSERT INTO MAGASIN (idmag, nommag, villemag) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, m.getIdmag());
            ps.setString(2, m.getNommag());
            ps.setString(3, m.getVillemag());
            return ps.executeUpdate();
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL lors de l'insertion du magasin");
            return 0;
        }
    }

    public void effacerMagasin(String idmag) throws SQLException {
        String query = "DELETE FROM MAGASIN WHERE idmag = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, idmag);
        ps.executeUpdate();
    }

    public void majMagasin(Magasin m) throws SQLException {
        String query = "UPDATE MAGASIN SET nommag = ?, villemag = ? WHERE idmag = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, m.getNommag());
        ps.setString(2, m.getVillemag());
        ps.setString(3, m.getIdmag());
        ps.executeUpdate();
    }

    public Magasin rechercherMagasinParId(String idmag) {
        try {
            String query = "SELECT * FROM MAGASIN WHERE idmag = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idmag);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return new Magasin(
                    res.getString("idmag"),
                    res.getString("nommag"),
                    res.getString("villemag")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return null;
    }

    public ArrayList<Magasin> listeDesMagasins() {
        ArrayList<Magasin> magasins = new ArrayList<>();
        String query = "SELECT * FROM MAGASIN order by idmag";
        try {
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            while (res.next()) {
                magasins.add(new Magasin(
                    res.getString("idmag"),
                    res.getString("nommag"),
                    res.getString("villemag")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return magasins;
    }

    public String maxIdMag() {
        String maxId = null;
        String query = "SELECT MAX(idmag) AS max_id FROM MAGASIN";
        try {
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            if (res.next()) {
                maxId = res.getString("max_id");
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return maxId;
    }

}
