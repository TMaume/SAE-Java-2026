package Metier;

public class ContenuPiece {

    private int quantite;
    private boolean enSupplement;
    private Piece piece;
    private Couleur couleur;

    public ContenuPiece() {
    }

    public void setQuantite(int newVar) {
        quantite = newVar;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setEnSupplement(boolean newVar) {
        enSupplement = newVar;
    }

    public boolean getEnSupplement() {
        return enSupplement;
    }

    public void setPiece(Piece newVar) {
        piece = newVar;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setCouleur(Couleur newVar) {
        couleur = newVar;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int obtenirQuantite() {
    }

    public boolean estEnSupplement() {
    }

    public Piece obtenirPiece() {
    }

    public Couleur obtenirCouleur() {
    }
}
