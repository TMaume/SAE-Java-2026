package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplementBD {
	private final ConnexionMySQL connexion;

	public SupplementBD(ConnexionMySQL connexion) {
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

	private static boolean tfToBool(String value) {
		return value != null && !value.isEmpty() && (value.charAt(0) == 't' || value.charAt(0) == 'T' || value.charAt(0) == '1');
	}

	public List<ContenirpBD.ContenirpRow> listeSupplementsParContenu(int idCont) {
		ArrayList<ContenirpBD.ContenirpRow> res = new ArrayList<>();
		String sql = "SELECT idcont, numpiece, idcoul, en_supplement, quantitep FROM CONTENIRP WHERE idcont = ? AND en_supplement = 't' ORDER BY numpiece, idcoul";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new ContenirpBD.ContenirpRow(
						rs.getInt("idcont"),
						rs.getString("numpiece"),
						rs.getInt("idcoul"),
						tfToBool(rs.getString("en_supplement")),
						(Integer) rs.getObject("quantitep")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeSupplementsParContenu: " + e.getMessage());
		}
		return res;
	}

	public int compterSupplementsParContenu(int idCont) {
		String sql = "SELECT COUNT(*) AS nb FROM CONTENIRP WHERE idcont = ? AND en_supplement = 't'";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCont);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return 0;
				}
				return rs.getInt("nb");
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL compterSupplementsParContenu: " + e.getMessage());
			return 0;
		}
	}
}
