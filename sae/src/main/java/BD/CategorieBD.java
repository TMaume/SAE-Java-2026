package BD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieBD {
	private final ConnexionMySQL connexion;

	public CategorieBD(ConnexionMySQL connexion) {
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

	public static final class CategorieRow {
		private final int idCat;
		private final String nomCat;

		public CategorieRow(int idCat, String nomCat) {
			this.idCat = idCat;
			this.nomCat = nomCat;
		}

		public int getIdCat() {
			return idCat;
		}

		public String getNomCat() {
			return nomCat;
		}
	}

	public int insererCategorie(CategorieRow c) {
		if (c == null) {
			throw new IllegalArgumentException("categorie");
		}
		String sql = "INSERT INTO CATEGORIE (idcat, nomcat) VALUES (?, ?)";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, c.getIdCat());
			ps.setString(2, c.getNomCat());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL insererCategorie: " + e.getMessage());
			return 0;
		}
	}

	public int effacerCategorie(int idCat) {
		String sql = "DELETE FROM CATEGORIE WHERE idcat = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCat);
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL effacerCategorie: " + e.getMessage());
			return 0;
		}
	}

	public int majCategorie(CategorieRow c) {
		if (c == null) {
			throw new IllegalArgumentException("categorie");
		}
		String sql = "UPDATE CATEGORIE SET nomcat = ? WHERE idcat = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setString(1, c.getNomCat());
			ps.setInt(2, c.getIdCat());
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erreur SQL majCategorie: " + e.getMessage());
			return 0;
		}
	}

	public CategorieRow rechercherCategorie(int idCat) {
		String sql = "SELECT idcat, nomcat FROM CATEGORIE WHERE idcat = ?";
		try (PreparedStatement ps = prepareStatement(sql)) {
			ps.setInt(1, idCat);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				return new CategorieRow(rs.getInt("idcat"), rs.getString("nomcat"));
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL rechercherCategorie: " + e.getMessage());
			return null;
		}
	}

	public List<CategorieRow> listeDesCategories() {
		ArrayList<CategorieRow> res = new ArrayList<>();
		String sql = "SELECT idcat, nomcat FROM CATEGORIE ORDER BY nomcat";
		try (Statement st = createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				res.add(new CategorieRow(rs.getInt("idcat"), rs.getString("nomcat")));
			}
		} catch (SQLException e) {
			System.err.println("Erreur SQL listeDesCategories: " + e.getMessage());
		}
		return res;
	}
}