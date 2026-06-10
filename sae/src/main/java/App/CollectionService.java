package App;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import BD.CollectionBD;

public class CollectionService {
    private final Map<String, CollectionItem> collection = new LinkedHashMap<>();

    public void ajouterBoite(Boite boite, EtatBoite etat) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        EtatBoite etatFinal = etat == null ? EtatBoite.INCOMPLETE : etat;
        collection.put(boite.getNumero(), new CollectionItem(boite, etatFinal));
    }

    public CollectionItem obtenirItem(String numBoite) {
        return collection.get(numBoite);
    }

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

    public List<PieceQuantite> obtenirPiecesManquantes(String numBoite) {
        CollectionItem item = collection.get(numBoite);
        if (item == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(item.getPiecesManquantes());
    }

    public List<CollectionItem> listerCollection() {
        return new ArrayList<>(collection.values());
    }

    public boolean sauvegarderCollection(String identifiant, CollectionBD depot) {
        if (depot == null) {
            throw new IllegalArgumentException("depot");
        }
        return depot.sauvegarderCollection(identifiant, listerCollection());
    }
}
