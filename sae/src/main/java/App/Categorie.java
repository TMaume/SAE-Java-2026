package App;

/**
 * Représente une catégorie de pièces LEGO.
 */
public class Categorie {
    private final int idCategorie;
    private String nom;

    /**
     * Crée une catégorie de pièces.
     *
     * @param idCategorie l'identifiant unique de la catégorie
     * @param nom le nom de la catégorie
     */
    public Categorie(int idCategorie, String nom) {
        this.idCategorie = idCategorie;
        this.nom = nom == null ? "" : nom;
    }

    /**
     * Retourne l'identifiant de la catégorie.
     *
     * @return l'identifiant
     */
    public int getId() {
        return idCategorie;
    }

    /**
     * Retourne le nom de la catégorie.
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la catégorie.
     *
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    @Override
    public String toString() {
        return idCategorie + " - " + nom;
    }
}
