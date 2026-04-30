public class SousBoite extends Boite {

    private int quantite;

    public SousBoite(String numBoite, String nomBoite, int annee, int nbPieces, Theme theme, int quantite) {
        super(numBoite, nomBoite, annee, nbPieces, theme);
        this.quantite = quantite;
    }

    public void setQuantite(int newVar) {
        quantite = newVar;
    }

    public int getQuantite() {
        return quantite;
    }
}
