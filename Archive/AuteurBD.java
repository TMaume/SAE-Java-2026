package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;

public class AuteurBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public AuteurBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererAuteur(Auteur a) throws SQLException {
        String query = "INSERT INTO AUTEUR (idauteur, nomauteur, anneenais, anneedeces) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, a.getIdauteur());
        ps.setString(2, a.getNomauteur());
        ps.setInt(3, a.getAnneenais());
        ps.setInt(4, a.getAnneedeces());
        return ps.executeUpdate();
    }

    public void effacerAuteur(String idauteur) throws SQLException {
        String query = "DELETE FROM AUTEUR WHERE idauteur = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, idauteur);
        ps.executeUpdate();
    }

    public void majAuteur(Auteur a) throws SQLException {
        String query = "UPDATE AUTEUR SET nomauteur = ?, anneenais = ?, anneedeces = ? WHERE idauteur = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, a.getNomauteur());
        ps.setInt(2, a.getAnneenais());
        ps.setInt(3, a.getAnneedeces());
        ps.setString(4, a.getIdauteur());
        ps.executeUpdate();
    }

    public Auteur rechercherAuteurParId(String idauteur) throws SQLException {
        String query = "SELECT * FROM AUTEUR WHERE idauteur = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, idauteur);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Auteur(
                res.getString("idauteur"),
                res.getString("nomauteur"),
                res.getInt("anneenais"),
                res.getInt("anneedeces")
            );
        }
        return null;
    }

    public ArrayList<Auteur> rechercherAuteursParNom(String nomauteur) throws SQLException {
        ArrayList<Auteur> auteurs = new ArrayList<>();
        String query = "SELECT * FROM AUTEUR WHERE nomauteur LIKE ? order by nomauteur";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, "%" + nomauteur + "%");
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            auteurs.add(new Auteur(
                res.getString("idauteur"),
                res.getString("nomauteur"),
                res.getInt("anneenais"),
                res.getInt("anneedeces")
            ));
        }
        return auteurs;
    }

    public ArrayList<Auteur> listeDesAuteurs(){
        try {
            ArrayList<Auteur> auteurs = new ArrayList<>();
            String query = "SELECT * FROM AUTEUR order by nomauteur";
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);
            while (res.next()) {
                auteurs.add(new Auteur(
                    res.getString("idauteur"),
                    res.getString("nomauteur"),
                    res.getInt("anneenais"),
                    res.getInt("anneedeces")
                ));
            }
            return auteurs;
        }
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return new ArrayList<>();
        }
    }
}
