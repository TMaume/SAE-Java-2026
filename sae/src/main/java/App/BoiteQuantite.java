package App;

public class BoiteQuantite {
    private final Boite boite;
    private final int quantite;

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

    public Boite getBoite() {
        return boite;
    }

    public int getQuantite() {
        return quantite;
    }
}
