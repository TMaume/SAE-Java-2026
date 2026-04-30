public class ContenuPiece {

    private int quantite;
    private boolean enSupplement;
    private Piece piece;
    private Couleur couleur;

    public ContenuPiece(int qt,boolean supp, Piece p, Couleur coul){
        this.quantite=qt;
        this.enSupplement = supp;
        this.piece = p;
        this.couleur = coul;
    }

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
