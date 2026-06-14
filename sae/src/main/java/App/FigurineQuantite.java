package App;

/**
 * Représente une figurine avec une quantité associée.
 */
public class FigurineQuantite {
    private final Figurine figurine;
    private final int quantite;

    /**
     * Crée une association figurine-quantité.
     *
     * @param figurine la figurine (non null)
     * @param quantite la quantité (>= 0)
     * @throws IllegalArgumentException si figurine est null ou quantite < 0
     */
    public FigurineQuantite(Figurine figurine, int quantite) {
        if (figurine == null) {
            throw new IllegalArgumentException("figurine");
        }
        if (quantite < 0) {
            throw new IllegalArgumentException("quantite");
        }
        this.figurine = figurine;
        this.quantite = quantite;
    }

    /**
     * Retourne la figurine.
     *
     * @return la figurine
     */
    public Figurine getFigurine() {
        return figurine;
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
