package App;

import BD.ThemeBD;
import BD.ThemeParentBD;
import java.util.List;

/**
 * Service de gestion des thèmes LEGO.
 * <p>
 * Fournit des méthodes pour lister, rechercher et gérer les thèmes
 * ainsi que leurs relations parent-enfant.
 * </p>
 */
public class ThemeService {
    private final ThemeBD themeBD;
    private final ThemeParentBD themeParentBD;

    /**
     * Crée un service de gestion des thèmes.
     *
     * @param themeBD l'accès aux données des thèmes
     * @param themeParentBD l'accès aux relations parent-enfant des thèmes
     */
    public ThemeService(ThemeBD themeBD, ThemeParentBD themeParentBD) {
        this.themeBD = themeBD;
        this.themeParentBD = themeParentBD;
    }

    /**
     * Liste tous les thèmes.
     *
     * @return la liste des thèmes
     */
    public List<Theme> listerThemes() {
        return themeBD.listeDesThemes();
    }

    /**
     * Recherche un thème par son identifiant.
     *
     * @param id l'identifiant du thème
     * @return le thème ou null si non trouvé
     */
    public Theme rechercherTheme(int id) {
        return themeBD.rechercherTheme(id);
    }

    /**
     * Liste tous les sous-thèmes d'un thème parent.
     *
     * @param idPere l'identifiant du thème parent
     * @return la liste des sous-thèmes
     */
    public List<Theme> listerSousThemes(int idPere) {
        return themeBD.listeSousThemes(idPere);
    }

    /**
     * Ajoute un nouveau thème.
     *
     * @param theme le thème à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterTheme(Theme theme) {
        return themeBD.insererTheme(theme) > 0;
    }
    
    /**
     * Définit le thème parent d'un thème enfant.
     *
     * @param idTheme l'identifiant du thème enfant
     * @param idThemePere l'identifiant du thème parent (null pour retirer le parent)
     * @return true si l'opération a réussi, false sinon
     */
    public boolean definirParent(int idTheme, Integer idThemePere) {
        return themeParentBD.definirParent(idTheme, idThemePere) > 0;
    }

    public boolean idvalide(int id){
        return themeBD.idvalide(id);
    }
}