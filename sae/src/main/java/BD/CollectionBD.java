package BD;

import App.CollectionItem;
import App.EtatBoite;
import App.PieceQuantite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CollectionBD {
    private final ConnexionMySQL connexion;

    public CollectionBD(ConnexionMySQL connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("connexion");
        }
        this.connexion = connexion;
    }

    public boolean sauvegarderCollection(String identifiant, List<CollectionItem> collection) {
        if (identifiant == null || identifiant.isBlank()) {
            throw new IllegalArgumentException("identifiant");
        }
        if (collection == null) {
            throw new IllegalArgumentException("collection");
        }

        Connection connection = connexion.getConnection();
        if (connection == null) {
            return false;
        }

        try {
            creerTablesSiBesoin();
            boolean autoCommitInitial = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                supprimerCollection(identifiant);
                for (CollectionItem item : collection) {
                    enregistrerItem(identifiant, item);
                }
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Erreur SQL sauvegarderCollection: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(autoCommitInitial);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL sauvegarderCollection: " + e.getMessage());
            return false;
        }
    }

    private void creerTablesSiBesoin() throws SQLException {
        String createBoite = "CREATE TABLE IF NOT EXISTS COLLECTION_BOITE ("
            + "identifiant varchar(100) NOT NULL,"
            + "numboite varchar(20) NOT NULL,"
            + "etat varchar(20) NOT NULL,"
            + "PRIMARY KEY (identifiant, numboite),"
            + "FOREIGN KEY (numboite) REFERENCES BOITE (numboite)"
            + ")";
        String createPieces = "CREATE TABLE IF NOT EXISTS COLLECTION_PIECE_MANQUANTE ("
            + "identifiant varchar(100) NOT NULL,"
            + "numboite varchar(20) NOT NULL,"
            + "numpiece varchar(20) NOT NULL,"
            + "quantite int NOT NULL,"
            + "PRIMARY KEY (identifiant, numboite, numpiece),"
            + "FOREIGN KEY (numboite) REFERENCES BOITE (numboite),"
            + "FOREIGN KEY (numpiece) REFERENCES PIECE (numpiece)"
            + ")";

        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate(createBoite);
            statement.executeUpdate(createPieces);
        }
    }

    private void supprimerCollection(String identifiant) throws SQLException {
        String deletePieces = "DELETE FROM COLLECTION_PIECE_MANQUANTE WHERE identifiant = ?";
        String deleteBoites = "DELETE FROM COLLECTION_BOITE WHERE identifiant = ?";
        try (PreparedStatement psPieces = connexion.prepareStatement(deletePieces)) {
            psPieces.setString(1, identifiant);
            psPieces.executeUpdate();
        }
        try (PreparedStatement psBoites = connexion.prepareStatement(deleteBoites)) {
            psBoites.setString(1, identifiant);
            psBoites.executeUpdate();
        }
    }

    private void enregistrerItem(String identifiant, CollectionItem item) throws SQLException {
        if (item == null || item.getBoite() == null) {
            return;
        }

        String insertBoite = "INSERT INTO COLLECTION_BOITE (identifiant, numboite, etat) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connexion.prepareStatement(insertBoite)) {
            ps.setString(1, identifiant);
            ps.setString(2, item.getBoite().getNumero());
            ps.setString(3, item.getEtat() == null ? EtatBoite.INCOMPLETE.name() : item.getEtat().name());
            ps.executeUpdate();
        }

        Map<String, Integer> quantitesManquantes = new LinkedHashMap<>();
        Map<String, PieceQuantite> piecesManquantes = new LinkedHashMap<>();
        for (PieceQuantite pieceQuantite : item.getPiecesManquantes()) {
            if (pieceQuantite == null || pieceQuantite.getPiece() == null) {
                continue;
            }
            String numeroPiece = pieceQuantite.getPiece().getNumero();
            int quantite = pieceQuantite.getQuantite();
            quantitesManquantes.put(numeroPiece, quantitesManquantes.getOrDefault(numeroPiece, 0) + quantite);
            piecesManquantes.putIfAbsent(numeroPiece, pieceQuantite);
        }

        String insertPiece = "INSERT INTO COLLECTION_PIECE_MANQUANTE (identifiant, numboite, numpiece, quantite) VALUES (?, ?, ?, ?)";
        for (Map.Entry<String, Integer> entry : quantitesManquantes.entrySet()) {
            PieceQuantite pieceQuantite = piecesManquantes.get(entry.getKey());
            try (PreparedStatement ps = connexion.prepareStatement(insertPiece)) {
                ps.setString(1, identifiant);
                ps.setString(2, item.getBoite().getNumero());
                ps.setString(3, pieceQuantite.getPiece().getNumero());
                ps.setInt(4, entry.getValue());
                ps.executeUpdate();
            }
        }
    }
}