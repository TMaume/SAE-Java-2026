package App;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contient les statistiques d'une boîte LEGO.
 * <p>
 * Inclut le nombre total de pièces, de suppléments, de figurines,
 * de sous-boîtes et la répartition des pièces par couleur.
 * </p>
 */
public class BoiteStats {
    private final int totalPieces;
    private final int totalSupplement;
    private final int totalFigurines;
    private final int totalSousBoites;
    private final Map<Couleur, Integer> repartitionCouleurs;

    /**
     * Crée les statistiques d'une boîte.
     *
     * @param totalPieces le nombre total de pièces
     * @param totalSupplement le nombre de pièces en supplément
     * @param totalFigurines le nombre total de figurines
     * @param totalSousBoites le nombre de sous-boîtes incluses
     * @param repartitionCouleurs la répartition des pièces par couleur
     */
    public BoiteStats(int totalPieces, int totalSupplement, int totalFigurines, int totalSousBoites, Map<Couleur, Integer> repartitionCouleurs) {
        this.totalPieces = totalPieces;
        this.totalSupplement = totalSupplement;
        this.totalFigurines = totalFigurines;
        this.totalSousBoites = totalSousBoites;
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
     * Retourne le nombre total de figurines.
     *
     * @return le nombre de figurines
     */
    public int getTotalFigurines() {
        return totalFigurines;
    }

    /**
     * Retourne le nombre total de sous-boîtes incluses.
     *
     * @return le nombre de sous-boîtes
     */
    public int getTotalSousBoites() {
        return totalSousBoites;
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