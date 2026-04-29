package Metier;

import java.util.List;

public class Theme {

    private int idTheme;
    private String nomTheme;
    private Theme parentTheme;

    public Theme() {
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
    }

    public boolean estRacine() {
    }

    public String obtenirNom() {
    }

    public Theme obtenirParent() {
    }
}
