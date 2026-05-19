package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenirfBD {
	private final ConnexionMySQL connexion;

	public ContenirfBD(ConnexionMySQL connexion) {
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

	public static final class ContenirfRow {
		private final int idCont;
		private final String idFig;
		private final Integer quantite;

		public ContenirfRow(int idCont, String idFig, Integer quantite) {
			this.idCont = idCont;
			this.idFig = idFig;
			this.quantite = quantite;
		}

		public int getIdCont() {
			return idCont;
		}

		public String getIdFig() {
			return idFig;
		}

		public Integer getQuantite() {
			return quantite;
		}
	}

	public int insererContenirf(ContenirfRow c) {
		if (c == null) {
			throw new IllegalArgumentException("contenirf");
		}
		String sql = "INSERT INTO CONTENIRF (idcont, idfig, quantitef) VALUES (?, ?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, c.getIdCont());
			ps.setString(2, c.getIdFig());
			if (c.getQuantite() == null) {
				ps.setNull(3, Types.INTEGER);
			} else {
				ps.setInt(3, c.getQuantite());
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL insererContenirf: " + e.getMessage());
			return 0;
		}
	}

	public int effacerContenirf(int idCont, String idFig) {
		String sql = "DELETE FROM CONTENIRF WHERE idcont = ? AND idfig = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			ps.setString(2, idFig);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerContenirf: " + e.getMessage());
			return 0;
		}
	}

	public int majContenirf(ContenirfRow c) {
		if (c == null) {
			throw new IllegalArgumentException("contenirf");
		}
		String sql = "UPDATE CONTENIRF SET quantitef = ? WHERE idcont = ? AND idfig = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			if (c.getQuantite() == null) {
				ps.setNull(1, Types.INTEGER);
			} else {
				ps.setInt(1, c.getQuantite());
			}
			ps.setInt(2, c.getIdCont());
			ps.setString(3, c.getIdFig());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majContenirf: " + e.getMessage());
			return 0;
		}
	}

	public ContenirfRow rechercherContenirf(int idCont, String idFig) {
		String sql = "SELECT idcont, idfig, quantitef FROM CONTENIRF WHERE idcont = ? AND idfig = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			ps.setString(2, idFig);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new ContenirfRow(
					rs.getInt("idcont"),
					rs.getString("idfig"),
					(Integer) rs.getObject("quantitef")
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherContenirf: " + e.getMessage());
			return null;
		}
	}

	public List<ContenirfRow> listeContenirfParContenu(int idCont) {
		ArrayList<ContenirfRow> res = new ArrayList<>();
		String sql = "SELECT idcont, idfig, quantitef FROM CONTENIRF WHERE idcont = ? ORDER BY idfig";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new ContenirfRow(
						rs.getInt("idcont"),
						rs.getString("idfig"),
						(Integer) rs.getObject("quantitef")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeContenirfParContenu: " + e.getMessage());
		}
		return res;
	}
}
