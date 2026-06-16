package App;

/**
 * Représente une figurine LEGO.
 */
public class Figurine {
    private final String idFigurine;
    private String nom;
    private Integer nbParties;
    private String imageF;

    /**
     * Crée une figurine LEGO.
     *
     * @param idFigurine l'identifiant unique (non null, non vide)
     * @param nom le nom de la figurine
     * @param nbParties le nombre de parties
     * @param imageF l'URL de l'image de la figurine
     * @throws IllegalArgumentException si idFigurine est null ou vide
     */
    public Figurine(String idFigurine, String nom, Integer nbParties, String imageF) {
        if (idFigurine == null || idFigurine.isBlank()) {
            throw new IllegalArgumentException("idFigurine");
        }
        this.idFigurine = idFigurine;
        this.nom = nom == null ? "" : nom;
        this.nbParties = nbParties;
        this.imageF = imageF == null ? "" : imageF;
    }

    /**
     * Retourne l'identifiant de la figurine.
     *
     * @return l'identifiant
     */
    public String getIdFigurine() {
        return idFigurine;
    }

    /**
     * Retourne le nom de la figurine.
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la figurine.
     *
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    /**
     * Retourne le nombre de parties de la figurine.
     *
     * @return le nombre de parties
     */
    public Integer getNbParties() {
        return nbParties;
    }

    /**
     * Modifie le nombre de parties de la figurine.
     *
     * @param nbParties le nouveau nombre de parties
     */
    public void setNbParties(Integer nbParties) {
        this.nbParties = nbParties;
    }

    @Override
    public String toString() {
        return idFigurine + " - " + nom;
    }
}
