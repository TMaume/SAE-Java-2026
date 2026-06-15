package App;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une boîte LEGO avec ses caractéristiques et son contenu.
 * <p>
 * Une boîte peut contenir des pièces, des figurines et d'autres boîtes incluses.
 * Elle peut être personnalisée ou provenir de la base de données.
 * </p>
 */
public class Boite {
    private final String numero;
    private String nom;
    private Integer annee;
    private Theme theme;
    private Integer nbPieces;
    private boolean personnalisee;
    private final List<PieceQuantite> pieces = new ArrayList<>();
    private final List<FigurineQuantite> figurines = new ArrayList<>();
    private final List<BoiteQuantite> boitesIncluses = new ArrayList<>();

    /**
     * Crée une nouvelle boîte LEGO.
     *
     * @param numero le numéro unique de la boîte (non null, non vide)
     * @param nom le nom de la boîte
     * @param annee l'année de sortie de la boîte
     * @param theme le thème associé à la boîte
     * @throws IllegalArgumentException si le numero est null ou vide
     */
    public Boite(String numero, String nom, Integer annee, Theme theme) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("numero");
        }
        this.numero = numero;
        this.nom = nom == null ? "" : nom;
        this.annee = annee;
        this.theme = theme;
    }

    /**
     * Retourne le numéro unique de la boîte.
     *
     * @return le numéro de la boîte
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Retourne le nom de la boîte.
     *
     * @return le nom de la boîte
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la boîte.
     *
     * @param nom le nouveau nom de la boîte
     */
    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    /**
     * Retourne l'année de sortie de la boîte.
     *
     * @return l'année de la boîte
     */
    public Integer getAnnee() {
        return annee;
    }

    /**
     * Modifie l'année de sortie de la boîte.
     *
     * @param annee la nouvelle année
     */
    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    /**
     * Retourne le thème associé à la boîte.
     *
     * @return le thème de la boîte
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Modifie le thème associé à la boîte.
     *
     * @param theme le nouveau thème
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * Retourne le nombre total de pièces dans la boîte.
     *
     * @return le nombre de pièces (calculé si null)
     */
    public Integer getNbPieces() {
        return nbPieces == null ? calculerNbPieces() : nbPieces;
    }

    /**
     * Définit le nombre de pièces dans la boîte.
     *
     * @param nbPieces le nombre de pièces
     */
    public void setNbPieces(Integer nbPieces) {
        this.nbPieces = nbPieces;
    }

    /**
     * Indique si la boîte est personnalisée.
     *
     * @return true si la boîte est personnalisée, false sinon
     */
    public boolean isPersonnalisee() {
        return personnalisee;
    }

    /**
     * Définit si la boîte est personnalisée.
     *
     * @param personnalisee true si personnalisée, false sinon
     */
    public void setPersonnalisee(boolean personnalisee) {
        this.personnalisee = personnalisee;
    }

    /**
     * Retourne la liste des pièces de la boîte.
     *
     * @return la liste des pièces avec leurs quantités
     */
    public List<PieceQuantite> getPieces() {
        return pieces;
    }

    /**
     * Retourne la liste des figurines de la boîte.
     *
     * @return la liste des figurines avec leurs quantités
     */
    public List<FigurineQuantite> getFigurines() {
        return figurines;
    }

    /**
     * Retourne la liste des sous-boîtes incluses.
     *
     * @return la liste des boîtes incluses avec leurs quantités
     */
    public List<BoiteQuantite> getBoitesIncluses() {
        return boitesIncluses;
    }

    /**
     * Ajoute une pièce à la boîte.
     *
     * @param piece la pièce à ajouter (ignorée si null)
     */
    public void ajouterPiece(PieceQuantite piece) {
        if (piece != null) {
            pieces.add(piece);
        }
    }

    /**
     * Ajoute une figurine à la boîte.
     *
     * @param figurine la figurine à ajouter (ignorée si null)
     */
    public void ajouterFigurine(FigurineQuantite figurine) {
        if (figurine != null) {
            figurines.add(figurine);
        }
    }

    /**
     * Ajoute une sous-boîte incluse.
     *
     * @param boite la boîte à ajouter (ignorée si null)
     */
    public void ajouterBoiteIncluse(BoiteQuantite boite) {
        if (boite != null) {
            boitesIncluses.add(boite);
        }
    }

    /**
     * Calcule le nombre total de pièces en additionnant les quantités.
     *
     * @return le nombre total de pièces
     */
    public int calculerNbPieces() {
        int total = 0;
        for (PieceQuantite piece : pieces) {
            total += piece.getQuantite();
        }
        return total;
    }

    @Override
    public String toString() {
        return numero + " - " + nom;
    }
}
