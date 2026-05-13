package BD;
import java.sql.*;

public class ConnexionMySQL {
	private Connection mysql=null;
	private boolean connecte=false;
	public ConnexionMySQL() throws ClassNotFoundException{
		Class.forName("org.mariadb.jdbc.Driver");
	}

	public void connecter(String nomBase, String nomLogin, String motDePasse) throws SQLException {
		try{
			this.mysql = DriverManager.getConnection("jdbc:mysql://servinfo-maria:3306/" + nomBase, nomLogin, motDePasse);;
		}
		catch (SQLException ex){
		System.out.println("Msg:" + ex.getMessage() + ex.getErrorCode());
		}
		this.connecte=this.mysql!=null;
	}

	public void close() throws SQLException {
		this.connecte=false;
	}

    public boolean isConnecte() { return this.connecte;}

	public Statement createStatement() throws SQLException {
		return this.mysql.createStatement();
	}

	public PreparedStatement prepareStatement(String requete) throws SQLException{
		return this.mysql.prepareStatement(requete);
	}
	
}
