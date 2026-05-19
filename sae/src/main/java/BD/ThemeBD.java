package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeBD {
	private final ConnexionMySQL connexion;

	public ThemeBD(ConnexionMySQL connexion) {
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

	public static final class ThemeRow {
		private final int idTheme;
		private final String nomTheme;
		private final Integer idThemePere;

		public ThemeRow(int idTheme, String nomTheme, Integer idThemePere) {
			this.idTheme = idTheme;
			this.nomTheme = nomTheme;
			this.idThemePere = idThemePere;
		}

		public int getIdTheme() {
			return idTheme;
		}

		public String getNomTheme() {
			return nomTheme;
		}

		public Integer getIdThemePere() {
			return idThemePere;
		}
	}

	public int insererTheme(ThemeRow t) {
		if (t == null) {
			throw new IllegalArgumentException("theme");
		}
		String sql = "INSERT INTO THEME (idtheme, nomtheme, idtheme_pere) VALUES (?, ?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, t.getIdTheme());
			ps.setString(2, t.getNomTheme());
			if (t.getIdThemePere() == null) {
				ps.setNull(3, Types.INTEGER);
			} else {
				ps.setInt(3, t.getIdThemePere());
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL insererTheme: " + e.getMessage());
			return 0;
		}
	}

	public int effacerTheme(int idTheme) {
		String sql = "DELETE FROM THEME WHERE idtheme = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idTheme);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerTheme: " + e.getMessage());
			return 0;
		}
	}

	public int majTheme(ThemeRow t) {
		if (t == null) {
			throw new IllegalArgumentException("theme");
		}
		String sql = "UPDATE THEME SET nomtheme = ?, idtheme_pere = ? WHERE idtheme = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, t.getNomTheme());
			if (t.getIdThemePere() == null) {
				ps.setNull(2, Types.INTEGER);
			} else {
				ps.setInt(2, t.getIdThemePere());
			}
			ps.setInt(3, t.getIdTheme());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majTheme: " + e.getMessage());
			return 0;
		}
	}

	public ThemeRow rechercherTheme(int idTheme) {
		String sql = "SELECT idtheme, nomtheme, idtheme_pere FROM THEME WHERE idtheme = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idTheme);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new ThemeRow(
					rs.getInt("idtheme"),
					rs.getString("nomtheme"),
					(Integer) rs.getObject("idtheme_pere")
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherTheme: " + e.getMessage());
			return null;
		}
	}

	public List<ThemeRow> listeDesThemes() {
		ArrayList<ThemeRow> res = new ArrayList<>();
		String sql = "SELECT idtheme, nomtheme, idtheme_pere FROM THEME ORDER BY nomtheme";
		try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				res.add(new ThemeRow(
					rs.getInt("idtheme"),
					rs.getString("nomtheme"),
					(Integer) rs.getObject("idtheme_pere")
				));
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeDesThemes: " + e.getMessage());
		}
		return res;
	}

	public List<ThemeRow> listeSousThemes(int idThemePere) {
		ArrayList<ThemeRow> res = new ArrayList<>();
		String sql = "SELECT idtheme, nomtheme, idtheme_pere FROM THEME WHERE idtheme_pere = ? ORDER BY nomtheme";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idThemePere);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(new ThemeRow(
						rs.getInt("idtheme"),
						rs.getString("nomtheme"),
						(Integer) rs.getObject("idtheme_pere")
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeSousThemes: " + e.getMessage());
		}
		return res;
	}
}
