import java.util.List;

public class EtatBoite {

    private boolean complete;
    private List piecesManquantes;
    private Boite boite;

    public EtatBoite() {
    }

    public void setComplete(boolean newVar) {
        complete = newVar;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setPiecesManquantes(List newVar) {
        piecesManquantes = newVar;
    }

    public List getPiecesManquantes() {
        return piecesManquantes;
    }

    public void setBoite(Boite newVar) {
        boite = newVar;
    }

    public Boite getBoite() {
        return boite;
    }

    public boolean estComplete() {
    }

    public List obtenirPiecesManquantes() {
    }

    public Boite obtenirBoite() {
    }
}
