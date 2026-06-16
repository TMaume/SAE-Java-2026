package App;

import java.util.Objects;

/**
 * Représente une couleur de pièce LEGO.
 */
public class Couleur {
    private final int idCouleur;
    private String nom;
    private String rgb;
    private boolean transparent;

    /**
     * Crée une couleur.
     *
     * @param idCouleur l'identifiant unique de la couleur
     * @param nom le nom de la couleur
     * @param rgb le code RGB de la couleur
     * @param transparent true si la couleur est transparente
     */
    public Couleur(int idCouleur, String nom, String rgb, boolean transparent) {
        this.idCouleur = idCouleur;
        this.nom = nom == null ? "" : nom;
        this.rgb = rgb == null ? "" : rgb;
        this.transparent = transparent;
    }

    /**
     * Retourne l'identifiant de la couleur.
     *
     * @return l'identifiant
     */
    public int getIdCouleur() {
        return idCouleur;
    }

    /**
     * Retourne le nom de la couleur.
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la couleur.
     *
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    /**
     * Retourne le code RGB de la couleur.
     *
     * @return le code RGB
     */
    public String getRgb() {
        return rgb;
    }

    /**
     * Modifie le code RGB de la couleur.
     *
     * @param rgb le nouveau code RGB
     */
    public void setRgb(String rgb) {
        this.rgb = rgb == null ? "" : rgb;
    }

    /**
     * Indique si la couleur est transparente.
     *
     * @return true si transparente, false sinon
     */
    public boolean isTransparent() {
        return transparent;
    }

    /**
     * Modifie si la couleur est transparente.
     *
     * @param transparent true pour transparente, false sinon
     */
    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Couleur couleur = (Couleur) o;
        return idCouleur == couleur.idCouleur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCouleur);
    }

    @Override
    public String toString() {
        return idCouleur + " - " + nom + " (" + rgb + ")";
    }
}