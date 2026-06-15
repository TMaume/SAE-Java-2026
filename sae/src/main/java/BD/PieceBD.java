package BD;

import App.Piece;
import App.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'accès aux données des pièces LEGO en base de données.
 * <p>
 * Fournit les méthodes CRUD pour les pièces avec gestion des catégories.
 * </p>
 */
public class PieceBD {
    private final ConnexionMySQL connexion;

    /**
     * Crée un accès aux données des pièces.
     *
     * @param connexion la connexion MySQL (non null)
     * @throws IllegalArgumentException si connexion est null
     */
    public PieceBD(ConnexionMySQL connexion) {
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
     * Insère une pièce dans la base de données.
     *
     * @param p la pièce à insérer (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si p est null
     */
    public int insererPiece(Piece p) {
        if (p == null) {
            throw new IllegalArgumentException("piece");
        }
        String sql = "INSERT INTO PIECE (numpiece, nompiece, idcat) VALUES (?, ?, ?)";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, p.getNumero());
            ps.setString(2, p.getNom());
            ps.setInt(3, p.getCategorie().getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL insererPiece: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Supprime une pièce de la base de données.
     *
     * @param numPiece le numéro de la pièce (non null)
     * @return le nombre de lignes affectées
     */
    public int effacerPiece(String numPiece) {
        String sql = "DELETE FROM PIECE WHERE numpiece = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numPiece);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL effacerPiece: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Met à jour une pièce dans la base de données.
     *
     * @param p la pièce à mettre à jour (non null)
     * @return le nombre de lignes affectées
     * @throws IllegalArgumentException si p est null
     */
    public int majPiece(Piece p) {
        if (p == null) {
            throw new IllegalArgumentException("piece");
        }
        String sql = "UPDATE PIECE SET nompiece = ?, idcat = ? WHERE numpiece = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setInt(2, p.getCategorie().getId());
            ps.setString(3, p.getNumero());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL majPiece: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Recherche une pièce par son numéro.
     *
     * @param numPiece le numéro de la pièce (non null)
     * @return la pièce trouvée, ou null si introuvable
     */
    public Piece rechercherPiece(String numPiece) {
        String sql = "SELECT p.numpiece, p.nompiece, c.idcat, c.nomcat " +
                     "FROM PIECE p " +
                     "LEFT JOIN CATEGORIE c ON p.idcat = c.idcat " +
                     "WHERE p.numpiece = ?";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setString(1, numPiece);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                
                Categorie cat = null;
                if (rs.getObject("idcat") != null) {
                    cat = new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
                }
                
                return new Piece(
                    rs.getString("numpiece"),
                    rs.getString("nompiece"),
                    cat,
                    null 
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL rechercherPiece: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne la liste de toutes les pièces.
     *
     * @return liste des pièces
     */
    public List<Piece> listeDesPieces() {
        ArrayList<Piece> res = new ArrayList<>();
        String sql = "SELECT p.numpiece, p.nompiece, c.idcat, c.nomcat " +
                     "FROM PIECE p " +
                     "LEFT JOIN CATEGORIE c ON p.idcat = c.idcat " +
                     "ORDER BY p.nompiece";
        try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Categorie cat = null;
                if (rs.getObject("idcat") != null) {
                    cat = new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
                }
                res.add(new Piece(
                    rs.getString("numpiece"),
                    rs.getString("nompiece"),
                    cat,
                    null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listeDesPieces: " + e.getMessage());
        }
        return res;
    }

    /**
     * Retourne les pièces d'une catégorie.
     *
     * @param idCat l'identifiant de la catégorie
     * @return liste des pièces de la catégorie
     */
    public List<Piece> listePiecesParCategorie(int idCat) {
        ArrayList<Piece> res = new ArrayList<>();
        String sql = "SELECT p.numpiece, p.nompiece, c.idcat, c.nomcat " +
                     "FROM PIECE p " +
                     "LEFT JOIN CATEGORIE c ON p.idcat = c.idcat " +
                     "WHERE p.idcat = ? ORDER BY p.nompiece";
        try (PreparedStatement ps = prepareStatement(sql)) {
            ps.setInt(1, idCat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categorie cat = new Categorie(rs.getInt("idcat"), rs.getString("nomcat"));
                    res.add(new Piece(
                        rs.getString("numpiece"),
                        rs.getString("nompiece"),
                        cat,
                        null
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL listePiecesParCategorie: " + e.getMessage());
        }
        return res;
    }
}