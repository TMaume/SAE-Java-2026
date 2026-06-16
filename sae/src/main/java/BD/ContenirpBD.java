package BD;
import App.PieceQuantite;
import App.Piece;
import App.Categorie;
import App.Couleur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données d'association entre contenu et pièces en base de données.
 * <p>
 * Fournit les méthodes CRUD pour gérer les pièces contenues dans les boîtes.
 * </p>
 */
public class ContenirpBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un gestionnaire du contenu en pièces.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public ContenirpBD(ConnexionMySQL connexion) {
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
     * Convertit un String "t" ou "f" en booléen.
     *
     * @param value la valeur à convertir
     * @return true si value commence par 't', 'T' ou '1'
     */
    private static boolean tfToBool(String value) {
        return value != null && !value.isEmpty() && (value.charAt(0) == 't' || value.charAt(0) == 'T' || value.charAt(0) == '1');
    }

    /**
     * Convertit un booléen en String "t" ou "f".
     *
     * @param value le booléen à convertir
     * @return "t" si value est true, "f" sinon
     */
    private static String boolToTf(boolean value) {
        return value ? "t" : "f";
    }

    /**
     * Insère une association pièce-contenu dans la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param pq la pièce avec quantité (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si pq est null
     */
    public int insererContenirp(int idCont, PieceQuantite pq) {
        if (pq == null) {
            throw new IllegalArgumentException("contenirp");
        }
        String sql = "INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, pq.getPiece().getNumero());
            ps.setInt(3, pq.getPiece().getCouleur().getIdCouleur());
            ps.setString(4, boolToTf(pq.isEnSupplement()));
            ps.setInt(5, pq.getQuantite());
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Supprime une association pièce-contenu de la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param numPiece le numéro de la pièce
     * @param idCoul l'identifiant de la couleur
     * @param enSupplement si la pièce est supplémentaire
     * @return le nombre de lignes affectées
     */
    public int effacerContenirp(int idCont, String numPiece, int idCoul, boolean enSupplement) {
        String sql = "DELETE FROM CONTENIRP WHERE idcont = ? AND numpiece = ? AND idcoul = ? AND en_supplement = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, numPiece);
            ps.setInt(3, idCoul);
            ps.setString(4, boolToTf(enSupplement));
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Met à jour une association pièce-contenu dans la base de données.
     *
     * @param idCont l'identifiant du contenu
     * @param pq la pièce avec quantité mise à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si pq est null
     */
    public int majContenirp(int idCont, PieceQuantite pq) {
        if (pq == null) {
            throw new IllegalArgumentException("contenirp");
        }
        String sql = "UPDATE CONTENIRP SET quantitep = ? WHERE idcont = ? AND numpiece = ? AND idcoul = ? AND en_supplement = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, pq.getQuantite());
            ps.setInt(2, idCont);
            ps.setString(3, pq.getPiece().getNumero());
            ps.setInt(4, pq.getPiece().getCouleur().getIdCouleur());
            ps.setString(5, boolToTf(pq.isEnSupplement()));
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Recherche une association pièce-contenu spécifique.
     *
     * @param idCont l'identifiant du contenu
     * @param numPiece le numéro de la pièce
     * @param idCoul l'identifiant de la couleur
     * @param enSupplement si la pièce est supplémentaire
     * @return la pièce avec quantité, ou null si non trouvée
     */
    public PieceQuantite rechercherContenirp(int idCont, String numPiece, int idCoul, boolean enSupplement) {
        String sql = "SELECT cp.quantitep, cp.en_supplement, cp.imageP, p.numpiece, p.nompiece, c.idcat, c.nomcat, coul.idcoul, coul.nomcoul, coul.RGB, coul.transparent " +
                     "FROM CONTENIRP cp " +
                     "JOIN PIECE p ON cp.numpiece = p.numpiece " +
                     "LEFT JOIN CATEGORIE c ON p.idcat = c.idcat " +
                     "JOIN COULEUR coul ON cp.idcoul = coul.idcoul " +
                     "WHERE cp.idcont = ? AND cp.numpiece = ? AND cp.idcoul = ? AND cp.en_supplement = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            ps.setString(2, numPiece);
            ps.setInt(3, idCoul);
            ps.setString(4, boolToTf(enSupplement));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Categorie cat = null;
                if (rs.getObject("idcat") != null) {
                    cat = new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
                }
                Couleur couleur = new Couleur(
                    rs.getInt("idcoul"),
                    rs.getString("nomcoul"),
                    rs.getString("RGB"),
                    tfToBool(rs.getString("transparent"))
                );
                Piece piece = new Piece(
                    rs.getString("numpiece"),
                    rs.getString("nompiece"),
                    cat,
                    couleur
                );
                return new PieceQuantite(piece, rs.getInt("quantitep"), tfToBool(rs.getString("en_supplement")), rs.getString("imageP"));
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retourne les pièces contenues dans un contenu spécifique.
     *
     * @param idCont l'identifiant du contenu
     * @return liste des pièces avec quantités
     */
    public List<PieceQuantite> listeContenirpParContenu(int idCont) {
        ArrayList<PieceQuantite> res = new ArrayList<>();
        String sql = "SELECT cp.quantitep, cp.en_supplement, cp.imageP, p.numpiece, p.nompiece, c.idcat, c.nomcat, coul.idcoul, coul.nomcoul, coul.RGB, coul.transparent " +
                     "FROM CONTENIRP cp " +
                     "JOIN PIECE p ON cp.numpiece = p.numpiece " +
                     "LEFT JOIN CATEGORIE c ON p.idcat = c.idcat " +
                     "JOIN COULEUR coul ON cp.idcoul = coul.idcoul " +
                     "WHERE cp.idcont = ? " +
                     "ORDER BY p.numpiece, coul.idcoul";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCont);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categorie cat = null;
                    if (rs.getObject("idcat") != null) {
                        cat = new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
                    }
                    Couleur couleur = new Couleur(
                        rs.getInt("idcoul"),
                        rs.getString("nomcoul"),
                        rs.getString("RGB"),
                        tfToBool(rs.getString("transparent"))
                    );
                    Piece piece = new Piece(
                        rs.getString("numpiece"),
                        rs.getString("nompiece"),
                        cat,
                        couleur
                    );
                    res.add(new PieceQuantite(piece, rs.getInt("quantitep"), tfToBool(rs.getString("en_supplement")), rs.getString("imageP")));
                }
            }
        } catch (SQLException e) {
        }
        return res;
    }
}