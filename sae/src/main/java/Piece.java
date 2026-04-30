public class Piece {

    private String numPiece;
    private String nomPiece;

    private Categorie categorie;

    public Piece() {
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


    public Categorie obtenirCategorie() {
        return null;
    }

    public Couleur obtenirCouleur() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}
