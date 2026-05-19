package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenirbBD {
	private final ConnexionMySQL connexion;

	public ContenirbBD(ConnexionMySQL connexion) {
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

	public static final class ContenirbRow {
		private final int idCont;
		private final String numBoite;
		private final Integer quantite;

		public ContenirbRow(int idCont, String numBoite, Integer quantite) {
			this.idCont = idCont;
			this.numBoite = numBoite;
			this.quantite = quantite;
		}

		public int getIdCont() {
			return idCont;
		}

		public String getNumBoite() {
			return numBoite;
		}

		public Integer getQuantite() {
			return quantite;
		}
	}

	public int insererContenirb(ContenirbRow c) {
		if (c == null) {
			throw new IllegalArgumentException("contenirb");
		}
		String sql = "INSERT INTO CONTENIRB (idcont, numboite, quantiteb) VALUES (?, ?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, c.getIdCont());
			ps.setString(2, c.getNumBoite());
			if (c.getQuantite() == null) {
				ps.setNull(3, Types.INTEGER);
			} else {
				ps.setInt(3, c.getQuantite());
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL insererContenirb: " + e.getMessage());
			return 0;
		}
	}

	public int effacerContenirb(int idCont, String numBoite) {
		String sql = "DELETE FROM CONTENIRB WHERE idcont = ? AND numboite = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			ps.setString(2, numBoite);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerContenirb: " + e.getMessage());
			return 0;
		}
	}

	public int majContenirb(ContenirbRow c) {
		if (c == null) {
			throw new IllegalArgumentException("contenirb");
		}
		String sql = "UPDATE CONTENIRB SET quantiteb = ? WHERE idcont = ? AND numboite = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			if (c.getQuantite() == null) {
				ps.setNull(1, Types.INTEGER);
			} else {
				ps.setInt(1, c.getQuantite());
			}
			ps.setInt(2, c.getIdCont());
			ps.setString(3, c.getNumBoite());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majContenirb: " + e.getMessage());
			return 0;
		}
	}

	public ContenirbRow rechercherContenirb(int idCont, String numBoite) {
		String sql = "SELECT idcont, numboite, quantiteb FROM CONTENIRB WHERE idcont = ? AND numboite = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			ps.setString(2, numBoite);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new ContenirbRow(
					rs.getInt("idcont"),
					rs.getString("numboite"),
					(Integer) rs.getObject("quantiteb")
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherContenirb: " + e.getMessage());
			return null;
		}
	}

	public List<ContenirbRow> listeContenirbParContenu(int idCont) {
		ArrayList<ContenirbRow> res = new ArrayList<>();
		String sql = "SELECT idcont, numboite, quantiteb FROM CONTENIRB WHERE idcont = ? ORDER BY numboite";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new ContenirbRow(
						rs.getInt("idcont"),
						rs.getString("numboite"),
						(Integer) rs.getObject("quantiteb")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeContenirbParContenu: " + e.getMessage());
		}
		return res;
	}
}
