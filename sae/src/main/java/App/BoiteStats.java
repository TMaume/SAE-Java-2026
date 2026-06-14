package App;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contient les statistiques d'une boîte LEGO.
 * <p>
 * Inclut le nombre total de pièces, le nombre de suppléments,
 * et la répartition des pièces par couleur.
 * </p>
 */
public class BoiteStats {
    private final int totalPieces;
    private final int totalSupplement;
    private final Map<Couleur, Integer> repartitionCouleurs;

    /**
     * Crée les statistiques d'une boîte.
     *
     * @param totalPieces le nombre total de pièces
     * @param totalSupplement le nombre de pièces en supplément
     * @param repartitionCouleurs la répartition des pièces par couleur
     */
    public BoiteStats(int totalPieces, int totalSupplement, Map<Couleur, Integer> repartitionCouleurs) {
        this.totalPieces = totalPieces;
        this.totalSupplement = totalSupplement;
        this.repartitionCouleurs = new LinkedHashMap<>(repartitionCouleurs);
    }

    /**
     * Retourne le nombre total de pièces.
     *
     * @return le nombre total de pièces
     */
    public int getTotalPieces() {
        return totalPieces;
    }

    /**
     * Retourne le nombre de pièces en supplément.
     *
     * @return le nombre de suppléments
     */
    public int getTotalSupplement() {
        return totalSupplement;
    }

    /**
     * Retourne la répartition des pièces par couleur.
     *
     * @return une map non-modifiable des couleurs et leurs quantités
     */
    public Map<Couleur, Integer> getRepartitionCouleurs() {
        return Collections.unmodifiableMap(repartitionCouleurs);
    }
}
