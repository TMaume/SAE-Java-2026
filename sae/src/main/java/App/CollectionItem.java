package App;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un élément d'une collection personnelle de boîtes LEGO.
 * <p>
 * Associe une boîte avec son état et la liste des pièces manquantes.
 * </p>
 */
public class CollectionItem {
    private final Boite boite;
    private EtatBoite etat;
    private final List<PieceQuantite> piecesManquantes = new ArrayList<>();

    /**
     * Crée un élément de collection.
     *
     * @param boite la boîte (non null)
     * @param etat l'état de complétude (null = INCOMPLETE)
     * @throws IllegalArgumentException si boite est null
     */
    public CollectionItem(Boite boite, EtatBoite etat) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        this.boite = boite;
        this.etat = etat == null ? EtatBoite.INCOMPLETE : etat;
    }

    /**
     * Retourne la boîte.
     *
     * @return la boîte
     */
    public Boite getBoite() {
        return boite;
    }

    /**
     * Retourne l'état de la boîte.
     *
     * @return l'état (COMPLETE ou INCOMPLETE)
     */
    public EtatBoite getEtat() {
        return etat;
    }

    /**
     * Modifie l'état de la boîte.
     *
     * @param etat le nouvel état (null = INCOMPLETE)
     */
    public void setEtat(EtatBoite etat) {
        this.etat = etat == null ? EtatBoite.INCOMPLETE : etat;
    }

    /**
     * Retourne la liste des pièces manquantes.
     *
     * @return la liste des pièces manquantes
     */
    public List<PieceQuantite> getPiecesManquantes() {
        return piecesManquantes;
    }
}
