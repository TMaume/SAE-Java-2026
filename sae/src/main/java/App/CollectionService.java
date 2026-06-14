package App;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gère les collections personnelles de boîtes LEGO.
 * <p>
 * Permet d'ajouter/retirer des boîtes, gérer leur état et les pièces manquantes.
 * </p>
 */
public class CollectionService {
    private final Map<String, CollectionItem> collection = new LinkedHashMap<>();

    /**
     * Ajoute une boîte à la collection.
     *
     * @param boite la boîte à ajouter (non null)
     * @param etat l'état initial (null = INCOMPLETE)
     * @throws IllegalArgumentException si boite est null
     */
    public void ajouterBoite(Boite boite, EtatBoite etat) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        EtatBoite etatFinal = etat == null ? EtatBoite.INCOMPLETE : etat;
        collection.put(boite.getNumero(), new CollectionItem(boite, etatFinal));
    }

    /**
     * Récupère un élément de la collection par le numéro de boîte.
     *
     * @param numBoite le numéro de la boîte
     * @return l'élément ou null si non trouvé
     */
    public CollectionItem obtenirItem(String numBoite) {
        return collection.get(numBoite);
    }

    /**
     * Définit l'état d'une boîte dans la collection.
     *
     * @param numBoite le numéro de la boîte
     * @param etat le nouvel état
     * @throws IllegalArgumentException si la boîte n'existe pas
     */
    public void definirEtat(String numBoite, EtatBoite etat) {
        CollectionItem item = collection.get(numBoite);
        if (item == null) {
            throw new IllegalArgumentException("boite");
        }
        item.setEtat(etat);
        if (etat == EtatBoite.COMPLETE) {
            item.getPiecesManquantes().clear();
        }
    }

    /**
     * Définit les pièces manquantes pour une boîte.
     *
     * @param numBoite le numéro de la boîte
     * @param pieces la liste des pièces manquantes
     * @throws IllegalArgumentException si la boîte n'existe pas
     */
    public void definirPiecesManquantes(String numBoite, List<PieceQuantite> pieces) {
        CollectionItem item = collection.get(numBoite);
        if (item == null) {
            throw new IllegalArgumentException("boite");
        }
        item.getPiecesManquantes().clear();
        if (pieces != null) {
            item.getPiecesManquantes().addAll(pieces);
        }
        item.setEtat(EtatBoite.INCOMPLETE);
    }

    /**
     * Récupère les pièces manquantes pour une boîte.
     *
     * @param numBoite le numéro de la boîte
     * @return la liste des pièces manquantes (liste vide si boîte n'existe pas)
     */
    public List<PieceQuantite> obtenirPiecesManquantes(String numBoite) {
        CollectionItem item = collection.get(numBoite);
        if (item == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(item.getPiecesManquantes());
    }

    /**
     * Liste tous les éléments de la collection.
     *
     * @return la liste de tous les éléments
     */
    public List<CollectionItem> listerCollection() {
        return new ArrayList<>(collection.values());
    }
}
