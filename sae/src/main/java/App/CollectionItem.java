package App;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollectionItem {
    private final Boite boite;
    private EtatBoite etat;
    private LocalDate dateAjout;
    private final List<PieceQuantite> piecesManquantes = new ArrayList<>();
    
    private boolean construite = false;
    private String imagePersonnelle = null;
    
    private boolean boitePersonnalisee = false;

    // NOUVEAU : Listes d'inventaire pour les boîtes personnalisées
    private final List<PieceQuantite> piecesPerso = new ArrayList<>();
    private final List<FigurineQuantite> figurinesPerso = new ArrayList<>();

    public CollectionItem(Boite boite, EtatBoite etat) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        this.boite = boite;
        this.etat = etat == null ? EtatBoite.INCOMPLETE : etat;
        this.dateAjout = LocalDate.now(); 
    }

    public Boite getBoite() { return boite; }
    public EtatBoite getEtat() { return etat; }
    public void setEtat(EtatBoite etat) { this.etat = etat == null ? EtatBoite.INCOMPLETE : etat; }
    public List<PieceQuantite> getPiecesManquantes() { return piecesManquantes; }
    public LocalDate getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDate dateAjout) { this.dateAjout = dateAjout; }
    public boolean isConstruite() { return construite; }
    public void setConstruite(boolean construite) { this.construite = construite; }
    public String getImagePersonnelle() { return imagePersonnelle; }
    public void setImagePersonnelle(String imagePersonnelle) { this.imagePersonnelle = imagePersonnelle; }
    public boolean isBoitePersonnalisee() { return boitePersonnalisee; }
    public void setBoitePersonnalisee(boolean boitePersonnalisee) { this.boitePersonnalisee = boitePersonnalisee; }

    // --- Nouveaux Getters pour l'inventaire personnalisé ---
    public List<PieceQuantite> getPiecesPerso() { return piecesPerso; }
    public List<FigurineQuantite> getFigurinesPerso() { return figurinesPerso; }
}