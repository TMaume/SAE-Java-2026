package App;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoiteStats {
    private final int totalPieces;
    private final int totalSupplement;
    private final Map<Couleur, Integer> repartitionCouleurs;

    public BoiteStats(int totalPieces, int totalSupplement, Map<Couleur, Integer> repartitionCouleurs) {
        this.totalPieces = totalPieces;
        this.totalSupplement = totalSupplement;
        this.repartitionCouleurs = new LinkedHashMap<>(repartitionCouleurs);
    }

    public int getTotalPieces() {
        return totalPieces;
    }

    public int getTotalSupplement() {
        return totalSupplement;
    }

    public Map<Couleur, Integer> getRepartitionCouleurs() {
        return Collections.unmodifiableMap(repartitionCouleurs);
    }
}
