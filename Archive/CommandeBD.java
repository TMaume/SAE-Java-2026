package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;


public class CommandeBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public CommandeBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererCommande(Commande c) throws SQLException {
        String query = "INSERT INTO COMMANDE VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, c.getNumcom());
        ps.setString(2, LocalDate.now().toString());
        ps.setString(3, c.getEnligne());
        ps.setString(4, c.getLivraison());
        ps.setInt(5, c.getIdcli());
        ps.setString(6, c.getIdmag());
        return ps.executeUpdate();
    }

    public void effacerCommande(int numcom) throws SQLException {
        String query = "DELETE FROM COMMANDE WHERE numcom = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, numcom);
        ps.executeUpdate();
    }

    public void majCommande(Commande c) throws SQLException {
        String query = "UPDATE COMMANDE SET datecom = ?, enligne = ?, livraison = ?, idcli = ?, idmag = ? WHERE numcom = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, c.getDatecom());
        ps.setString(2, c.getEnligne());
        ps.setString(3, c.getLivraison());
        ps.setInt(4, c.getIdcli());
        ps.setString(5, c.getIdmag());
        ps.setInt(6, c.getNumcom());
        ps.executeUpdate();
    }

    public Commande rechercherCommandeParNum(int numcom) throws SQLException {
        String query = "SELECT * FROM COMMANDE WHERE numcom = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setInt(1, numcom);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Commande(
                res.getInt("numcom"),
                res.getString("datecom"),
                res.getString("enligne"),
                res.getString("livraison"),
                res.getInt("idcli"),
                res.getString("idmag")
            );
        }
        return null;
    }

    public ArrayList<Commande> listeDesCommandes() throws SQLException {
        ArrayList<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM COMMANDE";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            commandes.add(new Commande(
                res.getInt("numcom"),
                res.getString("datecom"),
                res.getString("enligne"),
                res.getString("livraison"),
                res.getInt("idcli"),
                res.getString("idmag")
            ));
        }
        return commandes;
    }

    public int getMaxNumCom() {
        try {
            String query = "SELECT MAX(numcom) AS max_numcom FROM COMMANDE";
            Statement st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            if (res.next()) {
                return res.getInt("max_numcom");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return 0;
    }

    public double calculerChiffreAffairesParMagasin(String idMag) {
        double chiffreAffaires = 0.0;
        try {
            String query = "SELECT SUM(prixvente) AS chiffre_affaires FROM DETAILCOMMANDE NATURAL JOIN COMMANDE NATURAL JOIN MAGASIN WHERE idmag = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idMag);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                chiffreAffaires = res.getDouble("chiffre_affaires");
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return chiffreAffaires;
    }

    public double calculerChiffreAffaires() {
        double chiffreAffaires = 0.0;
        try {
            String query = "SELECT SUM(qte * prixvente) AS chiffre_affaires FROM DETAILCOMMANDE";
            Statement st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            if (res.next()) {
                chiffreAffaires = res.getDouble("chiffre_affaires");
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return chiffreAffaires;
    }

    public List<Commande> afficherCommandesClient(int idCli) {
        List<Commande> commandes = new ArrayList<>();
        try {
            String query = "SELECT * FROM COMMANDE where idcli=?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setInt(1, idCli);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                commandes.add(new Commande(
                    res.getInt("numcom"),
                    res.getString("datecom"),
                    res.getString("enligne"),
                    res.getString("livraison"),
                    res.getInt("idcli"),
                    res.getString("idmag")
                ));
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return commandes;
    }
    
    public List<Commande> afficherCommandesMagasin(String idmag) {
        List<Commande> commandes = new ArrayList<>();
        try {
            String query = "SELECT * FROM COMMANDE where idmag=?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idmag);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                commandes.add(new Commande(
                    res.getInt("numcom"),
                    res.getString("datecom"),
                    res.getString("enligne"),
                    res.getString("livraison"),
                    res.getInt("idcli"),
                    res.getString("idmag")
                ));
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return commandes;
    }
}
