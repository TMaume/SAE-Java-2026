package UI.Controller;

import javafx.scene.Scene;

public class ParametreController {
    
    private static String themeActuel = "Clair"; 

    public static String getThemeActuel() {
        return themeActuel;
    }

    public static void setThemeActuel(String theme) {
        themeActuel = theme;
    }

    public static void appliquerTheme(Scene scene) {
        if (scene == null) return;
        
        scene.getStylesheets().clear();
        
        String fichierCss;
        switch (themeActuel) {
            case "Sombre":
                fichierCss = "sombre-theme.css";
                break;
            case "Forêt":
                fichierCss = "foret-theme.css";
                break;
            case "Clair":
            default:
                fichierCss = "clair-theme.css";
                break;
        }
        
        scene.getStylesheets().add(ParametreController.class.getResource("/UI/vue/" + fichierCss).toExternalForm());
    }
}