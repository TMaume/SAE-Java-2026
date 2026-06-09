package App;

import java.util.ArrayList;
import java.util.List;

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

    public Boite(String numero, String nom, Integer annee, Theme theme) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("numero");
        }
        this.numero = numero;
        this.nom = nom == null ? "" : nom;
        this.annee = annee;
        this.theme = theme;
    }

    public String getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Integer getNbPieces() {
        return nbPieces == null ? calculerNbPieces() : nbPieces;
    }

    public void setNbPieces(Integer nbPieces) {
        this.nbPieces = nbPieces;
    }

    public boolean isPersonnalisee() {
        return personnalisee;
    }

    public void setPersonnalisee(boolean personnalisee) {
        this.personnalisee = personnalisee;
    }

    public List<PieceQuantite> getPieces() {
        return pieces;
    }

    public List<FigurineQuantite> getFigurines() {
        return figurines;
    }

    public List<BoiteQuantite> getBoitesIncluses() {
        return boitesIncluses;
    }

    public void ajouterPiece(PieceQuantite piece) {
        if (piece != null) {
            pieces.add(piece);
        }
    }

    public void ajouterFigurine(FigurineQuantite figurine) {
        if (figurine != null) {
            figurines.add(figurine);
        }
    }

    public void ajouterBoiteIncluse(BoiteQuantite boite) {
        if (boite != null) {
            boitesIncluses.add(boite);
        }
    }

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
