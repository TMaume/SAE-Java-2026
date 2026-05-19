package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class LivreBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public LivreBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public ArrayList<Livre> listeLivresParMagasin(String idMag) {
        ArrayList<Livre> livres = new ArrayList<>();
        String query = "SELECT L.*, P.qte FROM LIVRE L JOIN POSSEDER P ON L.isbn = P.isbn WHERE P.idmag = ? AND P.qte > 0 ORDER BY L.titre";
        try {
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, idMag);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                livres.add(new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des livres du magasin : " + e.getMessage());
        }
        return livres;
    }
    

    public int insererLivre(Livre l) {
        try {
            String query = "INSERT INTO LIVRE (isbn, titre, nbpages, datepubli, prix) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, l.getIsbn());
            ps.setString(2, l.getTitre());
            ps.setInt(3, l.getNbPages());
            ps.setInt(4, l.getDatePubli());
            ps.setDouble(5, l.getPrix());
            return ps.executeUpdate();
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return 0;
        }
    }

    public void effacerLivre(String isbn) {
        try {
            String query = "DELETE FROM LIVRE WHERE isbn = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, isbn);
            ps.executeUpdate();
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
    }

    public void majLivre(Livre l) {
        try {
            String query = "UPDATE LIVRE SET titre = ?, nbpages = ?, datepubli = ?, prix = ? WHERE isbn = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, l.getTitre());
            ps.setInt(2, l.getNbPages());
            ps.setInt(3, l.getDatePubli());
            ps.setDouble(4, l.getPrix());
            ps.setString(5, l.getIsbn());
            ps.executeUpdate();
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return;
        }
    }

    public ArrayList<Livre> rechercherLivreParISBN(String isbn) {
        try {
            ArrayList<Livre> livres = new ArrayList<>();
            String query = "SELECT * FROM LIVRE WHERE isbn LIKE ? order by titre";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, "%" + isbn + "%");
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                livres.add(new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                ));
            }
            return livres;
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }

    public ArrayList<Livre> rechercheParAuteur(String auteur) {
        ArrayList<Livre> livres = new ArrayList<>();
        try {
            String query = "SELECT * FROM LIVRE NATURAL JOIN ECRIRE NATURAL JOIN AUTEUR WHERE nomauteur LIKE ? order by nomauteur, titre";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, "%" + auteur + "%");
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                livres.add(new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                ));
            }
            return livres;
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }

    public ArrayList<Livre> rechercheParTheme(String theme) {
        ArrayList<Livre> livres = new ArrayList<>();
        try {
            String query = "SELECT * FROM LIVRE NATURAL JOIN THEMES NATURAL JOIN CLASSIFICATION WHERE iddewey LIKE ? order by titre";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, theme + "%");
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                livres.add(new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                ));
            }
            return livres;
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }

    public ArrayList<Livre> rechercheParNom(String nom){
        ArrayList<Livre> livres = new ArrayList<>();
        try {
            String query = "SELECT * FROM LIVRE WHERE titre LIKE ? order by titre";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, "%" + nom + "%");
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                livres.add(new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                ));
            }
            return livres;
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }

    public Livre rechercheUnLivreParISBN(String isbn){
        try {
            Livre livre;
            String query = "SELECT * FROM LIVRE WHERE isbn = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, isbn);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                return new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                );
            }
            return null;
            
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }

    public int quantiteTotaleDispo(String isbn) {
        int total = 0;
        try {
            String query = "SELECT SUM(qte) as total FROM POSSEDER WHERE isbn = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, isbn);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                total = res.getInt("total");
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
        }
        return total;
    }

    public int qteDispoDansUnMag(String isbnLivre, String idMag) {
        try {
            String query = "SELECT qte FROM POSSEDER NATURAL JOIN LIVRE NATURAL JOIN MAGASIN WHERE isbn = ? and idmag = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, isbnLivre);
            ps.setString(2, idMag);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                int qteLivre = res.getInt("qte");
                return qteLivre;
            } 
            else {
                return 0;
            }
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return 0;
        }
    }

    public Map<String, Integer> dicoQteMagPourUnLivre(Livre livre) {
        Map<String, Integer> result = new HashMap<>();
        try {
            String query = "SELECT nommag, qte FROM POSSEDER NATURAL JOIN MAGASIN NATURAL JOIN LIVRE WHERE isbn = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, livre.getIsbn());
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String nomMagasin = res.getString("nommag");
                int quantite = res.getInt("qte");
                result.put(nomMagasin, quantite);
            }
            return result;
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }

    public double prixLivreParISBN(String isbnLivre) {
        try {
            String query = "SELECT prix FROM LIVRE WHERE isbn = ?";
            PreparedStatement ps = laConnexion.prepareStatement(query);
            ps.setString(1, isbnLivre);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getDouble("prix");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL");
            return -1;
        }
    }

    public ArrayList<Livre> listeDesLivres() {
        ArrayList<Livre> livres = new ArrayList<>();
        try {
            String query = "SELECT * FROM LIVRE";
            st = laConnexion.createStatement();
            ResultSet res = st.executeQuery(query);

            while (res.next()) {
                livres.add(new Livre(
                    res.getString("isbn"),
                    res.getString("titre"),
                    res.getInt("nbpages"),
                    res.getInt("datepubli"),
                    res.getDouble("prix")
                ));
            }
            return livres;
        } 
        catch (SQLException e) {
            System.out.println("Erreur SQL");
            return null;
        }
    }
}