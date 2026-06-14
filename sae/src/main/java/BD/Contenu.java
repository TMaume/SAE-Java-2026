package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données du contenu des boîtes en base de données.
 * <p>
 * Gère les informations détaillées sur le contenu des boîtes LEGO incluant les pièces, figurines et versions.
 * </p>
 */
public class Contenu {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire du contenu des boîtes.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public Contenu(ConnexionMySQL connexion) {
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
     * Conteneur d'informations détaillées sur le contenu d'une boîte.
     * <p>
     * Contient l'identifiant du contenu, la version, le numéro de boîte et l'ID de la figurine.
     * </p>
     */
    public static final class ContenuDetail {
        private final int idCont;
        private final Integer version;
        private final String numBoite;
        private final String idFig;

        public ContenuDetail(int idCont, Integer version, String numBoite, String idFig) {
            this.idCont = idCont;
            this.version = version;
            this.numBoite = numBoite;
            this.idFig = idFig;
        }

        public int getIdCont() {
            return idCont;
        }

        public Integer getVersion() {
            return version;
        }

        public String getNumBoite() {
            return numBoite;
        }

        public String getIdFig() {
            return idFig;
        }
    }

    /**
     * Insère un contenu dans la base de données.
     *
     * @param c le contenu à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si c est null
     */
    public int insererContenu(ContenuDetail c) {
        if (c == null) {
            throw new IllegalArgumentException("contenu");
        }
        String sql = "INSERT INTO CONTENU (idcont, version, numboite, idfig) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, c.getIdCont());
            if (c.getVersion() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, c.getVersion());
            }
            if (c.getNumBoite() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, c.getNumBoite());
            }
            if (c.getIdFig() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, c.getIdFig());
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime un contenu de la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @return le nombre de lignes affectées
     */
    public int effacerContenu(int idCont) {
        String sql = "DELETE FROM CONTENU WHERE idcont = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour un contenu dans la base de données.
     *
     * @param c le contenu à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si c est null
     */
    public int majContenu(ContenuDetail c) {
        if (c == null) {
            throw new IllegalArgumentException("contenu");
        }
        String sql = "UPDATE CONTENU SET version = ?, numboite = ?, idfig = ? WHERE idcont = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            if (c.getVersion() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, c.getVersion());
            }
            if (c.getNumBoite() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, c.getNumBoite());
            }
            if (c.getIdFig() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, c.getIdFig());
            }
            ps.setInt(4, c.getIdCont());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche un contenu par son identifiant.
     *
     * @param idCont l'identifiant du contenu
     * @return le contenu trouvé, ou null si introuvable
     */
    public ContenuDetail rechercherContenu(int idCont) {
        String sql = "SELECT idcont, version, numboite, idfig FROM CONTENU WHERE idcont = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new ContenuDetail(
                    rs.getInt("idcont"),
                    (Integer) rs.getObject("version"),
                    rs.getString("numboite"),
                    rs.getString("idfig")
                );
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne la liste de tous les contenus.
     *
     * @return liste des contenus
     */
    public List<ContenuDetail> listeDesContenus() {
        ArrayList<ContenuDetail> res = new ArrayList<>();
        String sql = "SELECT idcont, version, numboite, idfig FROM CONTENU ORDER BY idcont";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(new ContenuDetail(
                    rs.getInt("idcont"),
                    (Integer) rs.getObject("version"),
                    rs.getString("numboite"),
                    rs.getString("idfig")
                ));
            }
        } catch (SQLException e) {
        }
        return res;
    }

    public List<ContenuDetail> listeContenusParBoite(String numBoite) {
        ArrayList<ContenuDetail> res = new ArrayList<>();
        String sql = "SELECT idcont, version, numboite, idfig FROM CONTENU WHERE numboite = ? ORDER BY idcont";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numBoite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new ContenuDetail(
                        rs.getInt("idcont"),
                        (Integer) rs.getObject("version"),
                        rs.getString("numboite"),
                        rs.getString("idfig")
                    ));
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }
}