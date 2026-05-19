package tableBD;

import appli.ConnexionMySQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatistiquesBD {
    
    private ConnexionMySQL laConnexion;

    public StatistiquesBD(ConnexionMySQL laConnexion) {
        this.laConnexion = laConnexion;
    }

    /**
     * Calcule le chiffre d'affaires total pour chaque THÈME PRINCIPAL sur l'ensemble des magasins.
     * @return Une Map associant le nom du thème principal à son chiffre d'affaires.
     * @throws SQLException
     */
    public Map<String, Double> getChiffreAffairesParThemePrincipal() throws SQLException {
        Map<String, Double> caParTheme = new LinkedHashMap<>(); // LinkedHashMap pour préserver l'ordre
        String query = "SELECT " +
                       "    (SELECT C2.nomclass FROM CLASSIFICATION C2 WHERE C2.iddewey = CASE " +
                       "        WHEN CAST(C.iddewey AS UNSIGNED) < 100 THEN '099' " +
                       "        ELSE CONCAT(LEFT(C.iddewey, 1), '00') " +
                       "    END) AS theme_principal, " +
                       "    SUM(D.prixvente) AS total_ventes " +
                       "FROM " +
                       "    DETAILCOMMANDE D " +
                       "JOIN " +
                       "    THEMES T ON D.isbn = T.isbn " +
                       "JOIN " +
                       "    CLASSIFICATION C ON T.iddewey = C.iddewey " +
                       "GROUP BY theme_principal " +
                       "HAVING theme_principal IS NOT NULL " +
                       "ORDER BY total_ventes DESC";
        
        try (PreparedStatement ps = laConnexion.prepareStatement(query);
             ResultSet res = ps.executeQuery()) {
            while (res.next()) {
                caParTheme.put(res.getString("theme_principal"), res.getDouble("total_ventes"));
            }
        }
        return caParTheme;
    }

    /**
     * Calcule le chiffre d'affaires pour chaque THÈME PRINCIPAL pour un magasin spécifique.
     * @param idMag L'identifiant du magasin.
     * @return Une Map associant le nom du thème principal à son chiffre d'affaires.
     * @throws SQLException
     */
    public Map<String, Double> getChiffreAffairesParThemePrincipal(String idMag) throws SQLException {
        Map<String, Double> caParTheme = new LinkedHashMap<>(); // LinkedHashMap pour préserver l'ordre
        String query = "SELECT " +
                       "    (SELECT C2.nomclass FROM CLASSIFICATION C2 WHERE C2.iddewey = CASE " +
                       "        WHEN CAST(C.iddewey AS UNSIGNED) < 100 THEN '099' " +
                       "        ELSE CONCAT(LEFT(C.iddewey, 1), '00') " +
                       "    END) AS theme_principal, " +
                       "    SUM(DC.prixvente) AS total_ventes " +
                       "FROM " +
                       "    DETAILCOMMANDE DC " +
                       "JOIN " +
                       "    COMMANDE CO ON DC.numcom = CO.numcom " +
                       "JOIN " +
                       "    THEMES T ON DC.isbn = T.isbn " +
                       "JOIN " +
                       "    CLASSIFICATION C ON T.iddewey = C.iddewey " +
                       "WHERE CO.idmag = ? " +
                       "GROUP BY theme_principal " +
                       "HAVING theme_principal IS NOT NULL " +
                       "ORDER BY total_ventes DESC";

        try (PreparedStatement ps = laConnexion.prepareStatement(query)) {
            ps.setString(1, idMag);
            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    caParTheme.put(res.getString("theme_principal"), res.getDouble("total_ventes"));
                }
            }
        }
        return caParTheme;
    }
}