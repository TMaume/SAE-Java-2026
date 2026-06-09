package App;

import java.util.ArrayList;
import java.util.List;

public class CollectionItem {
    private final Boite boite;
    private EtatBoite etat;
    private final List<PieceQuantite> piecesManquantes = new ArrayList<>();

    public CollectionItem(Boite boite, EtatBoite etat) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        this.boite = boite;
        this.etat = etat == null ? EtatBoite.INCOMPLETE : etat;
    }

    public Boite getBoite() {
        return boite;
    }

    public EtatBoite getEtat() {
        return etat;
    }

    public void setEtat(EtatBoite etat) {
        this.etat = etat == null ? EtatBoite.INCOMPLETE : etat;
    }

    public List<PieceQuantite> getPiecesManquantes() {
        return piecesManquantes;
    }
}
