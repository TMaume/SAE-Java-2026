public class ContenuPiece {

    private int quantite;
    private boolean enSupplement;
    private Piece piece;
    private Couleur couleur;

    public int getQt() {
        return quantite;
    }

    public boolean issupp(){
        return enSupplement;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public Piece getPiece() {
        return piece;
    }
}
