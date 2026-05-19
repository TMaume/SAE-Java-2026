package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class ClientBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public ClientBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererClient(Client c) throws SQLException {
        String query = "INSERT INTO CLIENT (idcli, nomcli, prenomcli, adressecli, codepostal, villecli) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, c.getIdcli());
        ps.setString(2, c.getNomcli());
        ps.setString(3, c.getPrenomcli());
        ps.setString(4, c.getAdressecli());
        ps.setString(5, c.getCodepostal());
        ps.setString(6, c.getVillecli());
        return ps.executeUpdate();
    }

    public void effacerClient(int idcli) throws SQLException {
        String query = "DELETE FROM CLIENT WHERE idcli = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, idcli);
        ps.executeUpdate();
    }

    public void majClient(Client c) throws SQLException {
        String query = "UPDATE CLIENT SET nomcli = ?, prenomcli = ?, adressecli = ?, codepostal = ?, villecli = ? WHERE idcli = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, c.getNomcli());
        ps.setString(2, c.getPrenomcli());
        ps.setString(3, c.getAdressecli());
        ps.setString(4, c.getCodepostal());
        ps.setString(5, c.getVillecli());
        ps.setInt(6, c.getIdcli());
        ps.executeUpdate();
    }

    public Client rechercherClientParId(int idcli){
        try {
            String query = "SELECT * FROM CLIENT WHERE idcli = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setInt(1, idcli);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return new Client(
                    res.getInt("idcli"),
                    res.getString("nomcli"),
                    res.getString("prenomcli"),
                    res.getString("adressecli"),
                    res.getString("codepostal"),
                    res.getString("villecli")
                );
            }
            return null;
        }
        catch (SQLException e){
            System.out.println("Erreur SQL");
        }
        return null;
        
    }

    public ArrayList<Client> listeDesClients() {
        ArrayList<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM CLIENT";
        try {
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            while (res.next()) {
                clients.add(new Client(
                    res.getInt("idcli"),
                    res.getString("nomcli"),
                    res.getString("prenomcli"),
                    res.getString("adressecli"),
                    res.getString("codepostal"),
                    res.getString("villecli")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return clients;
    }

    public int getMaxIdcli() {
        int maxId = 0;
        String query = "SELECT MAX(idcli) AS max_id FROM CLIENT";
        try {
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            if (res.next()) {
                maxId = res.getInt("max_id");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return maxId;
    }
}
