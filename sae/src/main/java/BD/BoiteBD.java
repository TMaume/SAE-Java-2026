package BD;
import App.Boite;
import App.Theme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des boîtes LEGO en base de données.
 * <p>
 * Fournit les méthodes CRUD (Create, Read, Update, Delete) pour les boîtes.
 * </p>
 */
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

    public int insererBoite(Boite b) {
        if (b == null) {
            throw new IllegalArgumentException("boite");
        }
        String sql = "INSERT INTO BOITE (numboite, nomboite, annee, nbpieces, idtheme, image) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, b.getNumero());
            ps.setString(2, b.getNom());
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
            ps.setInt(5, b.getTheme().getIdTheme());
            if (b.getImageBoite() == null || b.getImageBoite().isBlank()) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, b.getImageBoite());
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int effacerBoite(String numBoite) {
        String sql = "DELETE FROM BOITE WHERE numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numBoite);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int majBoite(Boite b) {
        if (b == null) {
            throw new IllegalArgumentException("boite");
        }
        String sql = "UPDATE BOITE SET nomboite = ?, annee = ?, nbpieces = ?, idtheme = ?, image = ? WHERE numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, b.getNom());
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
            ps.setInt(4, b.getTheme().getIdTheme());
            if (b.getImageBoite() == null || b.getImageBoite().isBlank()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, b.getImageBoite());
            }
            ps.setString(6, b.getNumero());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    public Boite rechercherBoite(String numBoite) {
        String sql = "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
                     "FROM BOITE b " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "WHERE b.numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numBoite);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Theme theme = new Theme(
                    rs.getInt("idtheme"),
                    rs.getString("nomtheme"),
                    null
                );
                Boite boite = new Boite(
                    rs.getString("numboite"),
                    rs.getString("nomboite"),
                    (Integer) rs.getObject("annee"),
                    theme,
                    rs.getString("image")
                );
                boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                return boite;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Boite> listeDesBoites() {
        ArrayList<Boite> res = new ArrayList<>();
        String sql = "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
                     "FROM BOITE b " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "ORDER BY b.nomboite";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Theme theme = new Theme(
                    rs.getInt("idtheme"),
                    rs.getString("nomtheme"),
                    null
                );
                Boite boite = new Boite(
                    rs.getString("numboite"),
                    rs.getString("nomboite"),
                    (Integer) rs.getObject("annee"),
                    theme,
                    rs.getString("image")
                );
                boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                res.add(boite);
            }
        } catch (SQLException e) {
        }
        return res;
    }

    public List<Boite> listeDesBoitesPaginee(int limite, int offset) {
        ArrayList<Boite> res = new ArrayList<>();
        String sql = "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
                     "FROM BOITE b " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "ORDER BY b.nomboite " +
                     "LIMIT ? OFFSET ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, limite);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme theme = new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        null
                    );
                    Boite boite = new Boite(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        theme,
                        rs.getString("image")
                    );
                    boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                    res.add(boite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération paginée : " + e.getMessage());
        }
        return res;
    }

    public int compterBoites() {
        String sql = "SELECT COUNT(*) AS total FROM BOITE";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des boîtes : " + e.getMessage());
        }
        return 0;
    }

    // --- NOUVELLES MÉTHODES POUR LE FILTRAGE DYNAMIQUE ---

    /**
     * Recherche avancée : Filtre par nom partiel ET/OU par thème, de manière paginée.
     */
    public List<Boite> rechercherBoitesFiltreesPaginees(String recherche, Integer idTheme, int limite, int offset) {
        ArrayList<Boite> res = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
            "FROM BOITE b " +
            "JOIN THEME t ON b.idtheme = t.idtheme " +
            "WHERE 1=1 "
        );

        if (recherche != null && !recherche.isBlank()) {
            sql.append("AND b.nomboite LIKE ? ");
        }
        if (idTheme != null && idTheme > 0) {
            sql.append("AND b.idtheme = ? ");
        }
        sql.append("ORDER BY b.nomboite LIMIT ? OFFSET ?");

        try (PreparedStatement ps = prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (recherche != null && !recherche.isBlank()) {
                ps.setString(paramIndex++, "%" + recherche + "%");
            }
            if (idTheme != null && idTheme > 0) {
                ps.setInt(paramIndex++, idTheme);
            }
            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme theme = new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        null
                    );
                    Boite boite = new Boite(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        theme,
                        rs.getString("image")
                    );
                    boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                    res.add(boite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche filtrée paginée : " + e.getMessage());
        }
        return res;
    }

    /**
     * Compte le nombre de boîtes correspondant aux filtres.
     */
    public int compterBoitesFiltrees(String recherche, Integer idTheme) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM BOITE b WHERE 1=1 ");
        
        if (recherche != null && !recherche.isBlank()) {
            sql.append("AND b.nomboite LIKE ? ");
        }
        if (idTheme != null && idTheme > 0) {
            sql.append("AND b.idtheme = ? ");
        }

        try (PreparedStatement ps = prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (recherche != null && !recherche.isBlank()) {
                ps.setString(paramIndex++, "%" + recherche + "%");
            }
            if (idTheme != null && idTheme > 0) {
                ps.setInt(paramIndex++, idTheme);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage filtré : " + e.getMessage());
        }
        return 0;
    }

    // -----------------------------------------------------

    public List<Boite> listeBoitesParTheme(int idTheme) {
        ArrayList<Boite> res = new ArrayList<>();
        String sql = "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
                     "FROM BOITE b " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "WHERE b.idtheme = ? ORDER BY b.nomboite";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idTheme);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme theme = new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        null
                    );
                    Boite boite = new Boite(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        theme,
                        rs.getString("image")
                    );
                    boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                    res.add(boite);
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }

    public List<Boite> rechercherBoitesParNom(String nomPartiel) {
        ArrayList<Boite> res = new ArrayList<>();
        String sql = "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
                     "FROM BOITE b " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "WHERE b.nomboite LIKE ? ORDER BY b.nomboite";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, "%" + nomPartiel + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme theme = new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        null
                    );
                    Boite boite = new Boite(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        theme,
                        rs.getString("image")
                    );
                    boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                    res.add(boite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des boîtes par nom : " + e.getMessage());
        }
        return res;
    }

    public List<Boite> rechercherBoitesParPiece(String numPiece) {
        ArrayList<Boite> res = new ArrayList<>();
        String sql = "SELECT DISTINCT b.numboite, b.nomboite, b.annee, b.nbpieces, t.idtheme, t.nomtheme, b.image " +
                     "FROM BOITE b " +
                     "JOIN THEME t ON b.idtheme = t.idtheme " +
                     "JOIN CONTENU c ON b.numboite = c.numboite " +
                     "JOIN CONTENIRP cp ON c.idcont = cp.idcont " +
                     "WHERE cp.numpiece = ? ORDER BY b.nomboite";
                     
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numPiece);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme theme = new Theme(
                        rs.getInt("idtheme"),
                        rs.getString("nomtheme"),
                        null
                    );
                    Boite boite = new Boite(
                        rs.getString("numboite"),
                        rs.getString("nomboite"),
                        (Integer) rs.getObject("annee"),
                        theme,
                        rs.getString("image")
                    );
                    boite.setNbPieces((Integer) rs.getObject("nbpieces"));
                    res.add(boite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des boîtes par pièce : " + e.getMessage());
        }
        return res;
    }
}