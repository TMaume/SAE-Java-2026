package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenirpBD {
	private final ConnexionMySQL connexion;

	public ContenirpBD(ConnexionMySQL connexion) {
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

	public static final class ContenirpRow {
		private final int idCont;
		private final String numPiece;
		private final int idCoul;
		private final boolean enSupplement;
		private final Integer quantite;

		public ContenirpRow(int idCont, String numPiece, int idCoul, boolean enSupplement, Integer quantite) {
			this.idCont = idCont;
			this.numPiece = numPiece;
			this.idCoul = idCoul;
			this.enSupplement = enSupplement;
			this.quantite = quantite;
		}

		public int getIdCont() {
			return idCont;
		}

		public String getNumPiece() {
			return numPiece;
		}

		public int getIdCoul() {
			return idCoul;
		}

		public boolean isEnSupplement() {
			return enSupplement;
		}

		public Integer getQuantite() {
			return quantite;
		}
	}

	private static boolean tfToBool(String value) {
		return value != null && !value.isEmpty() && (value.charAt(0) == 't' || value.charAt(0) == 'T' || value.charAt(0) == '1');
	}

	private static String boolToTf(boolean value) {
		return value ? "t" : "f";
	}

	public int insererContenirp(ContenirpRow c) {
		if (c == null) {
			throw new IllegalArgumentException("contenirp");
		}
		String sql = "INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, c.getIdCont());
			ps.setString(2, c.getNumPiece());
			ps.setInt(3, c.getIdCoul());
			ps.setString(4, boolToTf(c.isEnSupplement()));
			if (c.getQuantite() == null) {
				ps.setNull(5, Types.INTEGER);
			} else {
				ps.setInt(5, c.getQuantite());
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL insererContenirp: " + e.getMessage());
			return 0;
		}
	}

	public int effacerContenirp(int idCont, String numPiece, int idCoul, boolean enSupplement) {
		String sql = "DELETE FROM CONTENIRP WHERE idcont = ? AND numpiece = ? AND idcoul = ? AND en_supplement = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			ps.setString(2, numPiece);
			ps.setInt(3, idCoul);
			ps.setString(4, boolToTf(enSupplement));
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerContenirp: " + e.getMessage());
			return 0;
		}
	}

	public int majContenirp(ContenirpRow c) {
		if (c == null) {
			throw new IllegalArgumentException("contenirp");
		}
		String sql = "UPDATE CONTENIRP SET quantitep = ? WHERE idcont = ? AND numpiece = ? AND idcoul = ? AND en_supplement = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			if (c.getQuantite() == null) {
				ps.setNull(1, Types.INTEGER);
			} else {
				ps.setInt(1, c.getQuantite());
			}
			ps.setInt(2, c.getIdCont());
			ps.setString(3, c.getNumPiece());
			ps.setInt(4, c.getIdCoul());
			ps.setString(5, boolToTf(c.isEnSupplement()));
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majContenirp: " + e.getMessage());
			return 0;
		}
	}

	public ContenirpRow rechercherContenirp(int idCont, String numPiece, int idCoul, boolean enSupplement) {
		String sql = "SELECT idcont, numpiece, idcoul, en_supplement, quantitep FROM CONTENIRP WHERE idcont = ? AND numpiece = ? AND idcoul = ? AND en_supplement = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			ps.setString(2, numPiece);
			ps.setInt(3, idCoul);
			ps.setString(4, boolToTf(enSupplement));
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new ContenirpRow(
					rs.getInt("idcont"),
					rs.getString("numpiece"),
					rs.getInt("idcoul"),
					tfToBool(rs.getString("en_supplement")),
					(Integer) rs.getObject("quantitep")
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherContenirp: " + e.getMessage());
			return null;
		}
	}

	public List<ContenirpRow> listeContenirpParContenu(int idCont) {
		ArrayList<ContenirpRow> res = new ArrayList<>();
		String sql = "SELECT idcont, numpiece, idcoul, en_supplement, quantitep FROM CONTENIRP WHERE idcont = ? ORDER BY numpiece, idcoul";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new ContenirpRow(
						rs.getInt("idcont"),
						rs.getString("numpiece"),
						rs.getInt("idcoul"),
						tfToBool(rs.getString("en_supplement")),
						(Integer) rs.getObject("quantitep")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeContenirpParContenu: " + e.getMessage());
		}
		return res;
	}
}