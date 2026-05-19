package tableBD;
import table.*;
import appli.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClassificationBD {
    ConnexionMySQL laConnexion;
    Statement st;

    public ClassificationBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    public int insererClassification(Classification c) throws SQLException {
        String query = "INSERT INTO CLASSIFICATION (iddewey, nomclass) VALUES (?, ?)";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, c.getIddewey());
        ps.setString(2, c.getNomclass());
        return ps.executeUpdate();
    }

    public void effacerClassification(String iddewey) throws SQLException {
        String query = "DELETE FROM CLASSIFICATION WHERE iddewey = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, iddewey);
        ps.executeUpdate();
    }

    public void majClassification(Classification c) throws SQLException {
        String query = "UPDATE CLASSIFICATION SET nomclass = ? WHERE iddewey = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, c.getNomclass());
        ps.setString(2, c.getIddewey());
        ps.executeUpdate();
    }

    public Classification rechercheClassificationParId(String iddewey) throws SQLException {
        String query = "SELECT * FROM CLASSIFICATION WHERE iddewey = ?";
        PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, iddewey);
        ResultSet res = ps.executeQuery();
        if (res.next()) {
            return new Classification(res.getString("iddewey"), res.getString("nomclass"));
        }
        return null;
    }
    
    public ArrayList<Classification> listeToutesClassifications() throws SQLException {
        ArrayList<Classification> liste = new ArrayList<>();
        String query = "SELECT * FROM CLASSIFICATION ORDER BY iddewey";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);
        while (res.next()) {
            liste.add(new Classification(res.getString("iddewey"), res.getString("nomclass")));
        }
        return liste;
    }

    public List<Classification> listeThemesPrincipaux() throws SQLException {
        List<Classification> themesPrincipaux = new ArrayList<>();
        String query = "SELECT * FROM CLASSIFICATION";
        st = laConnexion.createStatement();
        ResultSet res = st.executeQuery(query);

        boolean autresExiste = false;
        while (res.next()) {
            String idDewey = res.getString("iddewey");
            try {
                int idNum = Integer.parseInt(idDewey);
                if (idNum < 100) {
                    autresExiste = true;
                } else if (idDewey.endsWith("00")) {
                    themesPrincipaux.add(new Classification(idDewey, res.getString("nomclass")));
                }
            } catch (NumberFormatException e) {
                System.err.println("ID Dewey non numérique ignoré : " + idDewey);
            }
        }

        if (autresExiste) {
            themesPrincipaux.add(new Classification("099", "Autres"));
        }
        
        themesPrincipaux.sort(Comparator.comparing(Classification::getNomclass));
        
        return themesPrincipaux;
    }
    
    public List<Classification> listeSousThemes(String idThemePrincipal) throws SQLException {
        List<Classification> sousThemes = new ArrayList<>();
        if (idThemePrincipal == null) {
            return sousThemes;
        }

        PreparedStatement ps;
       
        if ("099".equals(idThemePrincipal)) {
            String query = "SELECT * FROM CLASSIFICATION WHERE CAST(iddewey AS UNSIGNED) < 100 ORDER BY iddewey";
            ps = laConnexion.prepareStatement(query);
        } else if (idThemePrincipal.endsWith("00")) {
            String centainePrefix = idThemePrincipal.substring(0, 1);
            String query = "SELECT * FROM CLASSIFICATION WHERE iddewey LIKE ? AND iddewey NOT LIKE '%00' ORDER BY iddewey";
            ps = laConnexion.prepareStatement(query);
            ps.setString(1, centainePrefix + "%");
        } else {
            return sousThemes;
        }
        
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            sousThemes.add(new Classification(res.getString("iddewey"), res.getString("nomclass")));
        }
        return sousThemes;
    }

    public List<Livre> rechercheParClassification(String idClassification) throws SQLException {
        List<Livre> resultats = new ArrayList<>();
        
        if (idClassification == null || idClassification.isEmpty()) {
            throw new IllegalArgumentException("L'ID de la classification ne peut pas être nul ou vide.");
        }

        String query;
        PreparedStatement ps;

        if ("099".equals(idClassification)) {
            query = "SELECT DISTINCT l.* FROM LIVRE l " +
                    "JOIN THEMES t ON l.isbn = t.isbn " +
                    "WHERE CAST(t.iddewey AS UNSIGNED) < 100";
            ps = laConnexion.prepareStatement(query);
        } else {
            query = "SELECT DISTINCT l.* FROM LIVRE l " +
                    "JOIN THEMES t ON l.isbn = t.isbn " +
                    "WHERE t.iddewey LIKE ?";
            ps = laConnexion.prepareStatement(query);
            
            if (idClassification.endsWith("00")) {
                String centainePrefix = idClassification.substring(0, 1);
                ps.setString(1, centainePrefix + "%");
            } else {
                ps.setString(1, idClassification);
            }
        }
        
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            resultats.add(new Livre(
                res.getString("isbn"),
                res.getString("titre"),
                res.getInt("nbpages"),
                res.getInt("datepubli"),
                res.getDouble("prix")
            ));
        }
        return resultats;
    }
}
