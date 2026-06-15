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

    /**
     * Crée un accès aux données des boîtes.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public BoiteBD(ConnexionMySQL connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("connexion");
        }
        this.connexion = connexion;
    }

    /**
     * Retourne la connexion MySQL.
     *
     * @return la connexion
     */
    public ConnexionMySQL getConnexion() {
        return connexion;
    }

    /**
     * Crée une nouvelle instruction SQL.
     *
     * @return une instruction SQL
     * @throws SQLException si l'opération échoue
     */
    protected Statement createStatement() throws SQLException {
        return connexion.createStatement();
    }

    /**
     * Prépare une requête SQL paramétrée.
     *
     * @param sql la requête SQL
     * @return une instruction SQL préparée
     * @throws SQLException si l'opération échoue
     */
    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return connexion.prepareStatement(sql);
    }

    /**
     * Insère une boîte dans la base de données.
     *
     * @param b la boîte à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si b est null
     */
    public int insererBoite(Boite b) {
        if (b == null) {
            throw new IllegalArgumentException("boite");
        }
        String sql = "INSERT INTO BOITE (numboite, nomboite, annee, nbpieces, idtheme) VALUES (?, ?, ?, ?, ?)";
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
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime une boîte de la base de données.
     *
     * @param numBoite le numéro de la boîte (non null)
     * @return le nombre de lignes affectées
     */
    public int effacerBoite(String numBoite) {
        String sql = "DELETE FROM BOITE WHERE numboite = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numBoite);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour une boîte dans la base de données.
     *
     * @param b la boîte à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si b est null
     */
    public int majBoite(Boite b) {
        if (b == null) {
            throw new IllegalArgumentException("boite");
        }
        String sql = "UPDATE BOITE SET nomboite = ?, annee = ?, nbpieces = ?, idtheme = ? WHERE numboite = ?";
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
            ps.setString(5, b.getNumero());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche une boîte par son numéro.
     *
     * @param numBoite le numéro de la boîte (non null)
     * @return la boîte trouvée, ou null si introuvable
     */
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

    /**
     * Retourne la liste de toutes les boîtes (Attention : non paginée).
     *
     * @return liste des boîtes
     */
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

    /**
     * Récupère une portion restreinte de boîtes (Pagination).
     *
     * @param limite nombre maximum de résultats
     * @param offset décalage initial
     * @return la liste paginée des boîtes
     */
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

    /**
     * Compte le nombre total de boîtes dans la base de données.
     *
     * @return le nombre de boîtes total
     */
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

    /**
     * Retourne les boîtes d'un thème.
     *
     * @param idTheme l'identifiant du thème
     * @return liste des boîtes du thème
     */
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

    /**
     * Recherche les boîtes par nom partiel.
     *
     * @param nomPartiel le nom ou partie du nom à rechercher
     * @return liste des boîtes correspondantes
     */
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

    /**
     * Recherche les boîtes contenant une pièce.
     *
     * @param numPiece le numéro de la pièce
     * @return liste des boîtes contenant la pièce
     */
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