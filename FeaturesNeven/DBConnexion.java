package fr.sae.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion JDBC vers MariaDB.
 *
 * Les paramètres sont lus depuis les propriétés système passées par le
 * Makefile (via -Ddb.url / -Ddb.user / -Ddb.password) ou depuis des
 * variables d'environnement en fallback.
 *
 * Utilisation :
 *   try (Connection conn = DBConnection.getConnection()) {
 *       // ... requêtes SQL
 *   }
 */
public class DBConnection {

    // Valeurs par défaut (cohérentes avec flake.nix et Makefile)
    private static final String DEFAULT_URL  = "jdbc:mariadb://127.0.0.1:3306/lego_db";
    private static final String DEFAULT_USER = "sae_user";
    private static final String DEFAULT_PASS = "sae_pass";

    private DBConnection() {}   // classe utilitaire, pas d'instanciation

    /**
     * Ouvre et retourne une nouvelle connexion JDBC.
     * Préférer un pool (HikariCP) en production.
     */
    public static Connection getConnection() throws SQLException {
        String url  = System.getProperty("db.url",      DEFAULT_URL);
        String user = System.getProperty("db.user",     DEFAULT_USER);
        String pass = System.getProperty("db.password", DEFAULT_PASS);

        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Vérifie que la connexion est opérationnelle (utile dans les tests).
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            System.err.println("[DBConnection] Échec de connexion : " + e.getMessage());
            return false;
        }
    }
}