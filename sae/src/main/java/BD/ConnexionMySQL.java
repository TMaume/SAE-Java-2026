package BD;
import java.sql.*;

/**
 * Gère la connexion à la base de données MySQL/MariaDB.
 * <p>
 * Fournit les méthodes de connexion, déconnexion et création de requêtes SQL.
 * </p>
 */
public class ConnexionMySQL {
	private Connection mysql=null;
	private boolean connecte=false;
	
	/**
	 * Crée une nouvelle connexion MySQL (non initialisée).
	 * 
	 * @throws ClassNotFoundException si le driver MariaDB n'est pas trouvé
	 */
	public ConnexionMySQL() throws ClassNotFoundException{
		// volontairement vide (structure du squelette)
	}

	/**
	 * Établit une connexion à la base de données MySQL/MariaDB.
	 *
	 * @param nomServeur le nom du serveur (valeur par défaut: servinfo-maria)
	 * @param nomBase le nom de la base de données (valeur par défaut: DBo22403450)
	 * @param nomLogin le nom d'utilisateur (valeur par défaut: o22403450)
	 * @param motDePasse le mot de passe (valeur par défaut: o22403450)
	 * @throws SQLException si la connexion échoue
	 */
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
	
	/**
	 * Ferme la connexion à la base de données.
	 *
	 * @throws SQLException si la fermeture échoue
	 */
	public void close() throws SQLException {
		this.connecte=false;
		if (this.mysql != null && !this.mysql.isClosed()) {
			this.mysql.close();
		}
		this.mysql = null;
	}

	/**
	 * Vérifie si la connexion est établie.
	 *
	 * @return true si connecté, false sinon
	 */
    	public boolean isConnecte() { return this.connecte;}
    	
	/**
	 * Crée une nouvelle instruction SQL.
	 *
	 * @return une instruction SQL
	 * @throws SQLException si la connexion n'est pas initialisée
	 */
	public Statement createStatement() throws SQLException {
		if (this.mysql == null) {
			throw new SQLException("Connexion non initialisée");
		}
		return this.mysql.createStatement();
	}

	/**
	 * Prépare une requête SQL paramétrée.
	 *
	 * @param requete la requête SQL avec des paramètres
	 * @return une instruction SQL préparée
	 * @throws SQLException si la connexion n'est pas initialisée
	 */
	public PreparedStatement prepareStatement(String requete) throws SQLException{
		if (this.mysql == null) {
			throw new SQLException("Connexion non initialisée");
		}
		return this.mysql.prepareStatement(requete);
	}
	
}
