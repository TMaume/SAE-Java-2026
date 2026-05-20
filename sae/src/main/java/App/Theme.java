package App;

public class Theme {
    private final int idTheme;
    private String nom;
    private Integer idThemePere;

    public Theme(int idTheme, String nom, Integer idThemePere) {
        this.idTheme = idTheme;
        this.nom = nom == null ? "" : nom;
        this.idThemePere = idThemePere;
    }

    public int getIdTheme() {
        return idTheme;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    public Integer getIdThemePere() {
        return idThemePere;
    }

    public void setIdThemePere(Integer idThemePere) {
        this.idThemePere = idThemePere;
    }

    @Override
    public String toString() {
        return idTheme + " - " + nom;
    }
}
