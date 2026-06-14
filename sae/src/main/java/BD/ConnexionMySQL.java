package BD;
import java.sql.*;

public class ConnexionMySQL {
	private Connection mysql=null;
	private boolean connecte=false;
	public ConnexionMySQL() throws ClassNotFoundException{}

	public void connecter(String nomServeur, String nomBase, String nomLogin, String motDePasse) throws SQLException {
		if (nomServeur == null || nomServeur.isBlank()) {
			nomServeur = "servinfo-maria";
		}
		if (nomBase == null || nomBase.isBlank()) {
			nomBase = "DBo22403450";
		}
		if (nomLogin == null || nomLogin.isBlank()) {
			nomLogin = "o22403450";
		}
		if (motDePasse == null || motDePasse.isBlank()) {
			motDePasse = "o22403450";
		}

		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Driver MariaDB introuvable", e);
		}

		String url = "jdbc:mariadb://" + nomServeur + ":3306/" + nomBase + "?useSSL=false&allowLocalInfile=true";
		this.mysql = DriverManager.getConnection(url, nomLogin, motDePasse);
		this.connecte=this.mysql!=null;
	}
	public void close() throws SQLException {
		this.connecte=false;
		if (this.mysql != null && !this.mysql.isClosed()) {
			this.mysql.close();
		}
		this.mysql = null;
	}

    	public boolean isConnecte() { return this.connecte;}
	public Statement createStatement() throws SQLException {
		if (this.mysql == null) {
			throw new SQLException("Connexion non initialisée");
		}
		return this.mysql.createStatement();
	}

	public PreparedStatement prepareStatement(String requete) throws SQLException{
		if (this.mysql == null) {
			throw new SQLException("Connexion non initialisée");
		}
		return this.mysql.prepareStatement(requete);
	}
	
}
