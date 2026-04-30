import java.util.List;

public class Theme {

    private int idTheme;
    private String nomTheme;
    private Theme parentTheme = null;

    public Theme(int idTheme, String nomTheme, Theme parentTheme) {
        this.idTheme = idTheme;
        this.nomTheme = nomTheme;
        this.parentTheme = parentTheme;
    }

    public Theme(int idTheme, String nomTheme) {
        this.idTheme = idTheme;
        this.nomTheme = nomTheme;
    }

    public void setIdTheme(int newVar) {
        idTheme = newVar;
    }

    public int getIdTheme() {
        return idTheme;
    }

    public void setNomTheme(String newVar) {
        nomTheme = newVar;
    }

    public String getNomTheme() {
        return nomTheme;
    }

    public void setParentTheme(Theme newVar) {
        parentTheme = newVar;
    }

    public Theme getParentTheme() {
        return parentTheme;
    }

    public List obtenirSousThemes() {
        return null;
    }

    public boolean estRacine() {
        if (this.parentTheme == null) {
            return true;
        } else {
            return false;
        }
    }

    public Theme obtenirParent() {
        return parentTheme;
    }
}
