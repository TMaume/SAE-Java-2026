package BD;
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

	public static final class PieceRow {
		private final String numPiece;
		private final String nomPiece;
		private final int idCat;

		public PieceRow(String numPiece, String nomPiece, int idCat) {
			this.numPiece = numPiece;
			this.nomPiece = nomPiece;
			this.idCat = idCat;
		}

		public String getNumPiece() {
			return numPiece;
		}

		public String getNomPiece() {
			return nomPiece;
		}

		public int getIdCat() {
			return idCat;
		}
	}

	public int insererPiece(PieceRow p) {
		if (p == null) {
			throw new IllegalArgumentException("piece");
		}
		String sql = "INSERT INTO PIECE (numpiece, nompiece, idcat) VALUES (?, ?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, p.getNumPiece());
			ps.setString(2, p.getNomPiece());
			ps.setInt(3, p.getIdCat());
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

	public int majPiece(PieceRow p) {
		if (p == null) {
			throw new IllegalArgumentException("piece");
		}
		String sql = "UPDATE PIECE SET nompiece = ?, idcat = ? WHERE numpiece = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, p.getNomPiece());
			ps.setInt(2, p.getIdCat());
			ps.setString(3, p.getNumPiece());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majPiece: " + e.getMessage());
			return 0;
		}
	}

	public PieceRow rechercherPiece(String numPiece) {
		String sql = "SELECT numpiece, nompiece, idcat FROM PIECE WHERE numpiece = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, numPiece);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new PieceRow(
					rs.getString("numpiece"),
					rs.getString("nompiece"),
					rs.getInt("idcat")
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherPiece: " + e.getMessage());
			return null;
		}
	}

	public List<PieceRow> listeDesPieces() {
		ArrayList<PieceRow> res = new ArrayList<>();
		String sql = "SELECT numpiece, nompiece, idcat FROM PIECE ORDER BY nompiece";
		try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				res.add(new PieceRow(
					rs.getString("numpiece"),
					rs.getString("nompiece"),
					rs.getInt("idcat")
				));
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeDesPieces: " + e.getMessage());
		}
		return res;
	}

	public List<PieceRow> listePiecesParCategorie(int idCat) {
		ArrayList<PieceRow> res = new ArrayList<>();
		String sql = "SELECT numpiece, nompiece, idcat FROM PIECE WHERE idcat = ? ORDER BY nompiece";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCat);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new PieceRow(
						rs.getString("numpiece"),
						rs.getString("nompiece"),
						rs.getInt("idcat")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listePiecesParCategorie: " + e.getMessage());
		}
		return res;
	}
}
