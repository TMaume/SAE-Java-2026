package UI.Controller;


import java.util.List;

import App.Theme;
import App.ThemeService;
import UI.vue.CreerThemeVue;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

public class ControlNvTheme {
    private CreerThemeVue vue;
    private ThemeService model;

    public ControlNvTheme(CreerThemeVue v,ThemeService t){
        this.vue = v;
        this.model = t;
    }

    public void chargerThemes() {
        if (model != null) {
            List<Theme> themes = model.listerThemes();
            if (themes.isEmpty()) {
                vue.setLbinfo("Aucun thème disponible en base de données.", Color.RED);
            } else {
                vue.getCombo().setItems(FXCollections.observableArrayList(themes));
            }
        }
    }

    public void ajout(){
        try{Integer.parseInt(vue.getTf());}catch(NumberFormatException e){vue.setLbinfo("ID de theme non valide", Color.RED);return;}
        if (!model.idvalide(Integer.parseInt(vue.getTf()))){vue.setLbinfo("ID de theme deja existante", Color.RED);return;}
        if((!(vue.getTf() == null) && !(vue.getTf2() == null)))model.ajouterTheme(new Theme(Integer.parseInt(vue.getTf()), vue.getTf2(), null));
        else model.ajouterTheme(new Theme(Integer.parseInt(vue.getTf()), vue.getTf2(), vue.getTheme()));
        vue.setLbinfo("Nouveau theme ajouté avec succes",Color.GREEN);

    }
}
