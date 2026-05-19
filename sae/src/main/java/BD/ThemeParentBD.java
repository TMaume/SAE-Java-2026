package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeParentBD {
	private final ConnexionMySQL connexion;

	public ThemeParentBD(ConnexionMySQL connexion) {
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

	public int definirParent(int idTheme, Integer idThemePere) {
		String sql = "UPDATE THEME SET idtheme_pere = ? WHERE idtheme = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			if (idThemePere == null) {
				ps.setNull(1, Types.INTEGER);
			} else {
				ps.setInt(1, idThemePere);
			}
			ps.setInt(2, idTheme);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL definirParent: " + e.getMessage());
			return 0;
		}
	}

	public Integer rechercherParent(int idTheme) {
		String sql = "SELECT idtheme_pere FROM THEME WHERE idtheme = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idTheme);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return (Integer) rs.getObject("idtheme_pere");
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherParent: " + e.getMessage());
			return null;
		}
	}

	public List<Integer> listeSousThemes(int idThemePere) {
		ArrayList<Integer> res = new ArrayList<>();
		String sql = "SELECT idtheme FROM THEME WHERE idtheme_pere = ? ORDER BY idtheme";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idThemePere);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(rs.getInt("idtheme"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeSousThemes (ThemeParentBD): " + e.getMessage());
		}
		return res;
	}
}
