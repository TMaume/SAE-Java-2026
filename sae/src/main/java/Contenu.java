
import java.util.List;

public class Contenu {

    private int idCont;
    private int version;
    private List<ContenuBoite> boitesIncluses;
    private List<ContenuFigurine> figurines;
    private List<ContenuPiece> pieces;
    private Boite boite;
    private Figurine figurine;

    public Contenu(int idCont, List<ContenuBoite> boitesIncluses, List<ContenuFigurine> figurines, List<ContenuPiece> pieces, Boite boite) {
        this.idCont = idCont;
        this.boitesIncluses = boitesIncluses;
        this.figurines = figurines;
        this.pieces = pieces;
        this.boite = boite;
    }

    public Contenu(int idCont, List<ContenuBoite> boitesIncluses, List<ContenuFigurine> figurines, List<ContenuPiece> pieces, Figurine figurine) {
        this.idCont = idCont;
        this.boitesIncluses = boitesIncluses;
        this.figurines = figurines;
        this.pieces = pieces;
        this.figurine = figurine;
    }

    public int getId() {
        return idCont;
    }

    public int getVersion() {
        return version;
    }

    public List<ContenuBoite> getBoitesIncluses() {
        return boitesIncluses;
    }

    public List<ContenuFigurine> getFigurines() {
        return figurines;
    }

    public List<ContenuPiece> getPieces() {
        return pieces;
    }

    public Boite getBoite() {
        return boite;
    }

    public Figurine getFigurine() {
        return figurine;
    }

    

    
}
