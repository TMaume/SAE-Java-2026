public class Piece {

    private String numPiece;
    private String nomPiece;
    private Categorie categorie;
    private Couleur couleur;

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

    public void setCouleur(Couleur newVar) {
        couleur = newVar;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public Categorie obtenirCategorie() {
    }

    public Couleur obtenirCouleur() {
    }

    public String obtenirNumero() {
    }

    public String afficher() {
    }
}
