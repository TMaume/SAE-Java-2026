package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoiteBD {
    private final ConnexionMySQL connexion;

    public BoiteBD(ConnexionMySQL connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("connexion");
        }
        this.connexion = connexion;
    }

    public ConnexionMySQL getConnexion() {
        return connexion;
    }

    protected Statement createStatement() throws SQLException {
        return connexion.createStatement();
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return connexion.prepareStatement(sql);
    }

    public static final class BoiteRow {
        private final String numBoite;
        private final String nomBoite;
        private final Integer annee;
        private final Integer nbPieces;
        private final int idTheme;

        public BoiteRow(String numBoite, String nomBoite, Integer annee, Integer nbPieces, int idTheme) {
            this.numBoite = numBoite;
            this.nomBoite = nomBoite;
            this.annee = annee;
            this.nbPieces = nbPieces;
            this.idTheme = idTheme;
        }

        public String getNumBoite() {
            return numBoite;
        }

        public String getNomBoite() {
            return nomBoite;
        }

        public Integer getAnnee() {
            return annee;
        }

        public Integer getNbPieces() {
            return nbPieces;
        }

        public int getIdTheme() {
            return idTheme;
        }
    }

    public int insererBoite(BoiteRow b) {
        if (b == null) {
            throw new IllegalArgumentException("boite");
        }
        String sql = "INSERT INTO BOITE (numboite, nomboite, annee, nbpieces, idtheme) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, b.getNumBoite());
            ps.setString(2, b.getNomBoite());
            if (b.getAnnee() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, b.getAnnee());
            }
            if (b.getNbPieces() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, b.getNbPieces());
            }
            ps.setInt(5, b.getIdTheme());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL insererBoite: " + e.getMessage());
            return 0;
        }
    }

    public int effacerBoite(String numBoite) {
        String sql = "DELETE FROM BOITE WHERE numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numBoite);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL effacerBoite: " + e.getMessage());
            return 0;
        }
    }

    public int majBoite(BoiteRow b) {
        if (b == null) {
            throw new IllegalArgumentException("boite");
        }
        String sql = "UPDATE BOITE SET nomboite = ?, annee = ?, nbpieces = ?, idtheme = ? WHERE numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, b.getNomBoite());
            if (b.getAnnee() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, b.getAnnee());
            }
            if (b.getNbPieces() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, b.getNbPieces());
            }
            ps.setInt(4, b.getIdTheme());
            ps.setString(5, b.getNumBoite());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL majBoite: " + e.getMessage());
            return 0;
        }
    }

    public BoiteRow rechercherBoite(String numBoite) {
        String sql = "SELECT numboite, nomboite, annee, nbpieces, idtheme FROM BOITE WHERE numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numBoite);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Integer annee = (Integer) rs.getObject("annee");
                Integer nbPieces = (Integer) rs.getObject("nbpieces");
                return new BoiteRow(
                    rs.getString("numboite"),
                    rs.getString("nomboite"),
                    annee,
                    nbPieces,
                    rs.getInt("idtheme")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL rechercherBoite: " + e.getMessage());
            return null;
        }
    }

    public List<BoiteRow> listeDesBoites() {
        ArrayList<BoiteRow> res = new ArrayList<>();
        String sql = "SELECT numboite, nomboite, annee, nbpieces, idtheme FROM BOITE ORDER BY nomboite";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new BoiteRow(
                    rs.getString("numboite"),
                    rs.getString("nomboite"),
                    (Integer) rs.getObject("annee"),
                    (Integer) rs.getObject("nbpieces"),
                    rs.getInt("idtheme")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listeDesBoites: " + e.getMessage());
        }
        return res;
    }

    public List<BoiteRow> listeBoitesParTheme(int idTheme) {
        ArrayList<BoiteRow> res = new ArrayList<>();
        String sql = "SELECT numboite, nomboite, annee, nbpieces, idtheme FROM BOITE WHERE idtheme = ? ORDER BY nomboite";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idTheme);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new BoiteRow(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        (Integer) rs.getObject("nbpieces"),
                        rs.getInt("idtheme")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listeBoitesParTheme: " + e.getMessage());
        }
        return res;
    }
}
