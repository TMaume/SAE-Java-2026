package BD;

import App.Piece;
import App.Categorie;
import App.Couleur; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PieceBD {
    private final ConnexionMySQL connexion;

    public PieceBD(ConnexionMySQL connexion) {
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