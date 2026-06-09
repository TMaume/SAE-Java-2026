package App;

import BD.ThemeBD;
import BD.ThemeParentBD;
import java.util.List;

public class ThemeService {
    private final ThemeBD themeBD;
    private final ThemeParentBD themeParentBD;

    public ThemeService(ThemeBD themeBD, ThemeParentBD themeParentBD) {
        this.themeBD = themeBD;
        this.themeParentBD = themeParentBD;
    }

    public List<Theme> listerThemes() {
        return themeBD.listeDesThemes();
    }

    public Theme rechercherTheme(int id) {
        return themeBD.rechercherTheme(id);
    }

    public List<Theme> listerSousThemes(int idPere) {
        return themeBD.listeSousThemes(idPere);
    }

    public boolean ajouterTheme(Theme theme) {
        return themeBD.insererTheme(theme) > 0;
    }
    
    public boolean definirParent(int idTheme, Integer idThemePere) {
        return themeParentBD.definirParent(idTheme, idThemePere) > 0;
    }
}