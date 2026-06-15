package App;

import java.util.Objects;

/**
 * Représente une pièce LEGO avec ses caractéristiques.
 */
public class Piece {
    private final String numero;
    private String nom;
    private Categorie categorie;
    private Couleur couleur;

    /**
     * Crée une pièce LEGO.
     *
     * @param numero le numéro unique (non null, non vide)
     * @param nom le nom de la pièce
     * @param categorie la catégorie de la pièce
     * @param couleur la couleur de la pièce
     * @throws IllegalArgumentException si numero est null ou vide
     */
    public Piece(String numero, String nom, Categorie categorie, Couleur couleur) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("numero");
        }
        this.numero = numero;
        this.nom = nom == null ? "" : nom;
        this.categorie = categorie;
        this.couleur = couleur;
    }

    /**
     * Retourne le numéro de la pièce.
     *
     * @return le numéro
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Retourne le nom de la pièce.
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la pièce.
     *
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    /**
     * Retourne la catégorie de la pièce.
     *
     * @return la catégorie
     */
    public Categorie getCategorie() {
        return categorie;
    }

    /**
     * Modifie la catégorie de la pièce.
     *
     * @param categorie la nouvelle catégorie
     */
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    /**
     * Retourne la couleur de la pièce.
     *
     * @return la couleur
     */
    public Couleur getCouleur() {
        return couleur;
    }

    /**
     * Modifie la couleur de la pièce.
     *
     * @param couleur la nouvelle couleur
     */
    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    /**
     * Crée une nouvelle pièce avec une couleur différente.
     *
     * @param nouvelleCouleur la nouvelle couleur
     * @return une nouvelle instance de Piece avec la couleur modifiée
     */
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
