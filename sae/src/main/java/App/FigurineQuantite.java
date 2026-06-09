package App;

public class FigurineQuantite {
    private final Figurine figurine;
    private final int quantite;

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

    public Figurine getFigurine() {
        return figurine;
    }

    public int getQuantite() {
        return quantite;
    }
}
