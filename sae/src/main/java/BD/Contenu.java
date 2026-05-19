package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Contenu {
	private final ConnexionMySQL connexion;

	public Contenu(ConnexionMySQL connexion) {
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

	public static final class ContenuRow {
		private final int idCont;
		private final Integer version;
		private final String numBoite;
		private final String idFig;

		public ContenuRow(int idCont, Integer version, String numBoite, String idFig) {
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

	public int insererContenu(ContenuRow c) {
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
			System.err.println("Erreur SQL insererContenu: " + e.getMessage());
			return 0;
		}
	}

	public int effacerContenu(int idCont) {
		String sql = "DELETE FROM CONTENU WHERE idcont = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerContenu: " + e.getMessage());
			return 0;
		}
	}

	public int majContenu(ContenuRow c) {
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
			System.err.println("Erreur SQL majContenu: " + e.getMessage());
			return 0;
		}
	}

	public ContenuRow rechercherContenu(int idCont) {
		String sql = "SELECT idcont, version, numboite, idfig FROM CONTENU WHERE idcont = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new ContenuRow(
					rs.getInt("idcont"),
					(Integer) rs.getObject("version"),
					rs.getString("numboite"),
					rs.getString("idfig")
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherContenu: " + e.getMessage());
			return null;
		}
	}

	public List<ContenuRow> listeDesContenus() {
		ArrayList<ContenuRow> res = new ArrayList<>();
		String sql = "SELECT idcont, version, numboite, idfig FROM CONTENU ORDER BY idcont";
		try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				res.add(new ContenuRow(
					rs.getInt("idcont"),
					(Integer) rs.getObject("version"),
					rs.getString("numboite"),
					rs.getString("idfig")
				));
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeDesContenus: " + e.getMessage());
		}
		return res;
	}

	public List<ContenuRow> listeContenusParBoite(String numBoite) {
		ArrayList<ContenuRow> res = new ArrayList<>();
		String sql = "SELECT idcont, version, numboite, idfig FROM CONTENU WHERE numboite = ? ORDER BY idcont";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, numBoite);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new ContenuRow(
						rs.getInt("idcont"),
						(Integer) rs.getObject("version"),
						rs.getString("numboite"),
						rs.getString("idfig")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeContenusParBoite: " + e.getMessage());
		}
		return res;
	}
}
