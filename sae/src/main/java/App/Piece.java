package App;

import java.util.Objects;

public class Piece {
    private final String numero;
    private String nom;
    private Categorie categorie;
    private Couleur couleur;

    public Piece(String numero, String nom, Categorie categorie, Couleur couleur) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("numero");
        }
        this.numero = numero;
        this.nom = nom == null ? "" : nom;
        this.categorie = categorie;
        this.couleur = couleur;
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

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    public Piece avecCouleur(Couleur nouvelleCouleur) {
        return new Piece(numero, nom, categorie, nouvelleCouleur);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Piece)) {
            return false;
        }
        Piece piece = (Piece) o;
        return Objects.equals(numero, piece.numero) && idCouleur() == piece.idCouleur();
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, idCouleur());
    }

    private int idCouleur() {
        return couleur == null ? 0 : couleur.getIdCouleur();
    }

    @Override
    public String toString() {
        return numero + " - " + nom;
    }
}
