import java.util.List;

public class Contenu {

    private int idCont;
    private int version;
    private List<SousBoite> boitesIncluses;
    private ContenuFigurine figurine;
    private AttributsPiece pieces;
    private Boite laBoite;


    // methode faireBoite
    // methode faireFigurine

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

    public void setBoitesIncluses(List<SousBoite> newVar) {
        boitesIncluses = newVar;
    }

    public List<SousBoite> getBoitesIncluses() {
        return boitesIncluses;
    }

    public void setBoiteIncluse(List<SousBoite> newVar) {
        boitesIncluses = newVar;
    }

    public List<SousBoite> getBoiteIncluse() {
        return boitesIncluses;
    }

    public void setFigurines(ContenuFigurine newVar) {
        figurine = newVar;
    }

    public ContenuFigurine getFigurines() {
        return figurine;
    }

    public void setPieces(AttributsPiece newVar) {
        pieces = newVar;
    }

    public AttributsPiece getPieces() {
        return pieces;
    }

    public void setBoites(Boite newVar) {
        laBoite = newVar;
    }

    public Boite getBoites() {
        return laBoite;
    }

    public boolean estComplet() {
        return false;
    }

    public int obtenirNbTotalPiece() {
        return 0;
    }

    public List obtenirPieceManquantes() {
        return null;
    }

    public List obtenirPiece() {
        return null;
    }

    public List obtenirFigurines() {
        return null;
    }

    public List obtenirBoitesIncluses() {
        return null;
    }
}
