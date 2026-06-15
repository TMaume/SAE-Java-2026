package App;

/**
 * Représente une boîte avec une quantité associée.
 */
public class BoiteQuantite {
    private final Boite boite;
    private final int quantite;

    /**
     * Crée une association boîte-quantité.
     *
     * @param boite la boîte (non null)
     * @param quantite la quantité (>= 0)
     * @throws IllegalArgumentException si boite est null ou quantite &lt; 0
     */
    public BoiteQuantite(Boite boite, int quantite) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        if (quantite < 0) {
            throw new IllegalArgumentException("quantite");
        }
        this.boite = boite;
        this.quantite = quantite;
    }

    /**
     * Retourne la boîte.
     *
     * @return la boîte
     */
    public Boite getBoite() {
        return boite;
    }

    /**
     * Retourne la quantité.
     *
     * @return la quantité
     */
    public int getQuantite() {
        return quantite;
    }
}
