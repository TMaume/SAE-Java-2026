public class Piece {

    private String numPiece;
    private String nomPiece;
    private Categorie categorie;


    public Piece(String numPiece, String nomPiece, Categorie categorie) {
        this.numPiece = numPiece; 
        this.nomPiece = nomPiece;
        this.categorie = categorie;
    }

    public void setNumPiece(String newVar) {
        numPiece = newVar;
    }

    public String getNumPiece() {
        return numPiece;
    }

    public void setNomPiece(String newVar) {
        nomPiece = newVar;
    }

    public String getNomPiece() {
        return nomPiece;
    }

    public void setCategorie(Categorie newVar) {
        categorie = newVar;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCouleur(Couleur newVar) {
        couleur = newVar;
    }

    public String toString() {
        return "Pièce n°" + numPiece + " : " + nomPiece;
    }
}
