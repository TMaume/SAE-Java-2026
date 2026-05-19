package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouleurBD {
	private final ConnexionMySQL connexion;

	public CouleurBD(ConnexionMySQL connexion) {
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

	public static final class CouleurRow {
		private final int idCoul;
		private final String nomCoul;
		private final String rgb;
		private final boolean transparent;

		public CouleurRow(int idCoul, String nomCoul, String rgb, boolean transparent) {
			this.idCoul = idCoul;
			this.nomCoul = nomCoul;
			this.rgb = rgb;
			this.transparent = transparent;
		}

		public int getIdCoul() {
			return idCoul;
		}

		public String getNomCoul() {
			return nomCoul;
		}

		public String getRgb() {
			return rgb;
		}

		public boolean isTransparent() {
			return transparent;
		}
	}

	private static boolean tfToBool(String value) {
		return value != null && !value.isEmpty() && (value.charAt(0) == 't' || value.charAt(0) == 'T' || value.charAt(0) == '1');
	}

	private static String boolToTf(boolean value) {
		return value ? "t" : "f";
	}

	public int insererCouleur(CouleurRow c) {
		if (c == null) {
			throw new IllegalArgumentException("couleur");
		}
		String sql = "INSERT INTO COULEUR (idcoul, nomcoul, RGB, transparent) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, c.getIdCoul());
			ps.setString(2, c.getNomCoul());
			ps.setString(3, c.getRgb());
			ps.setString(4, boolToTf(c.isTransparent()));
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL insererCouleur: " + e.getMessage());
			return 0;
		}
	}

	public int effacerCouleur(int idCoul) {
		String sql = "DELETE FROM COULEUR WHERE idcoul = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCoul);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerCouleur: " + e.getMessage());
			return 0;
		}
	}

	public int majCouleur(CouleurRow c) {
		if (c == null) {
			throw new IllegalArgumentException("couleur");
		}
		String sql = "UPDATE COULEUR SET nomcoul = ?, RGB = ?, transparent = ? WHERE idcoul = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, c.getNomCoul());
			ps.setString(2, c.getRgb());
			ps.setString(3, boolToTf(c.isTransparent()));
			ps.setInt(4, c.getIdCoul());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majCouleur: " + e.getMessage());
			return 0;
		}
	}

	public CouleurRow rechercherCouleur(int idCoul) {
		String sql = "SELECT idcoul, nomcoul, RGB, transparent FROM COULEUR WHERE idcoul = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCoul);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new CouleurRow(
					rs.getInt("idcoul"),
					rs.getString("nomcoul"),
					rs.getString("RGB"),
					tfToBool(rs.getString("transparent"))
				);
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherCouleur: " + e.getMessage());
			return null;
		}
	}

	public List<CouleurRow> listeDesCouleurs() {
		ArrayList<CouleurRow> res = new ArrayList<>();
		String sql = "SELECT idcoul, nomcoul, RGB, transparent FROM COULEUR ORDER BY nomcoul";
		try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				res.add(new CouleurRow(
					rs.getInt("idcoul"),
					rs.getString("nomcoul"),
					rs.getString("RGB"),
					tfToBool(rs.getString("transparent"))
				));
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeDesCouleurs: " + e.getMessage());
		}
		return res;
	}
}
