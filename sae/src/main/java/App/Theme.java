package App;

public class Theme {
    private final int idTheme;
    private String nom;
    private Theme parent;

    public Theme(int idTheme, String nom, Theme parent) {
        this.idTheme = idTheme;
        this.nom = nom == null ? "" : nom;
        this.parent = parent;
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

    public Theme getParent() {
        return parent;
    }

    public void setParent(Theme parent) {
        this.parent = parent;
    }

    public Integer getIdThemePere() {
        return parent == null ? null : parent.getIdTheme();
    }

    @Override
    public String toString() {
        return idTheme + " - " + nom;
    }
}