package App;

/**
 * Représente un thème LEGO avec une hiérarchie parent-enfant.
 */
public class Theme {
    private final int idTheme;
    private String nom;
    private Theme parent;

    /**
     * Crée un thème LEGO.
     *
     * @param idTheme l'identifiant unique du thème
     * @param nom le nom du thème
     * @param parent le thème parent (null si thème racine)
     */
    public Theme(int idTheme, String nom, Theme parent) {
        this.idTheme = idTheme;
        this.nom = nom == null ? "" : nom;
        this.parent = parent;
    }

    /**
     * Retourne l'identifiant du thème.
     *
     * @return l'identifiant
     */
    public int getIdTheme() {
        return idTheme;
    }

    /**
     * Retourne le nom du thème.
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom du thème.
     *
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    /**
     * Retourne le thème parent.
     *
     * @return le thème parent ou null
     */
    public Theme getParent() {
        return parent;
    }

    /**
     * Modifie le thème parent.
     *
     * @param parent le nouveau thème parent
     */
    public void setParent(Theme parent) {
        this.parent = parent;
    }

    /**
     * Retourne l'identifiant du thème parent.
     *
     * @return l'identifiant du thème parent ou null
     */
    public Integer getIdThemePere() {
        return parent == null ? null : parent.getIdTheme();
    }

    @Override
    public String toString() {
        return idTheme + " - " + nom;
    }
}