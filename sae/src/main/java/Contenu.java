
import java.util.List;

public class Contenu {

    private int idCont;
    private int version;
    private List boitesIncluses;
    private ContenuBoite boiteIncluse;
    private ContenuFigurine figurines;
    private ContenuPiece pieces;
    private Boite boites;

    public Contenu() {
    }

    public void setIdCont(int newVar) {
        idCont = newVar;
    }

    public int getIdCont() {
        return idCont;
    }

    public void setVersion(int newVar) {
        version = newVar;
    }

    public int getVersion() {
        return version;
    }

    public void setBoitesIncluses(List newVar) {
        boitesIncluses = newVar;
    }

    public List getBoitesIncluses() {
        return boitesIncluses;
    }

    public void setBoiteIncluse(ContenuBoite newVar) {
        boiteIncluse = newVar;
    }

    public ContenuBoite getBoiteIncluse() {
        return boiteIncluse;
    }

    public void setFigurines(ContenuFigurine newVar) {
        figurines = newVar;
    }

    public ContenuFigurine getFigurines() {
        return figurines;
    }

    public void setPieces(ContenuPiece newVar) {
        pieces = newVar;
    }

    public ContenuPiece getPieces() {
        return pieces;
    }

    public void setBoites(Boite newVar) {
        boites = newVar;
    }

    public Boite getBoites() {
        return boites;
    }

    public boolean estComplet() {
    }

    public int obtenirNbTotalPiece() {
    }

    public List obtenirPieceManquantes() {
    }

    public List obtenirPiece() {
    }

    public List obtenirFigurines() {
    }

    public List obtenirBoitesIncluses() {
    }
}
