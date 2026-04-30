import java.util.List;
import java.util.ArrayList;

public class CollectionPersonnelle {
    private List boites;

    public CollectionPersonnelle() {
        this.boites = new ArrayList<>();
    }

    public void ajouterBoite(Boite boite) {
        boites.add(boite);
    }

    public List obtenirPiecesManquantes(Boite boite) {
        return this.getEtatBoite(boite).getPiecesManquantes();
    }

    public List rechercherBoitesParTheme(Theme theme) {
    }

    public BoitePersonnalisee composerBoitePersonnalisee(String nom, List pieces) {
    }

    public Boite rechercherBoiteParNumero(String numero) {
    }

    public List listerCollection() {
    }

    public void enregistrerNouvelleBoite(Boite boite) {
    }

    public void mettreAJourContenuBoite(Boite boite) {
    }
}
