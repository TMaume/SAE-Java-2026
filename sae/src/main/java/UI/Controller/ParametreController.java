package UI.Controller;

import javafx.scene.Scene;

public class ParametreController {
    
    private static String themeActuel = "Clair"; 
    private static boolean tripleTActif = false;

    public static String getThemeActuel() {
        return themeActuel;
    }

    public static void setThemeActuel(String theme) {
        themeActuel = theme;
    }

    public static boolean isTripleTActif() {
        return tripleTActif;
    }
    

    public static void appliquerTheme(Scene scene) {
        if (scene == null) return;
        if (themeActuel.equals("TripleT")) tripleTActif = true;
        scene.getStylesheets().clear();
        
        String fichierCss;
        switch (themeActuel) {
            case "Sombre":
                fichierCss = "sombre-theme.css";
                break;
            case "Forêt":
                fichierCss = "foret-theme.css";
                break;
            case "TripleT":
                fichierCss = "triplet-theme.css";
                break;
            case "Lego":
                fichierCss = "Lego-theme.css";
                break;
            case "Clair":
            default:
                fichierCss = "clair-theme.css";
                break;
        }
        
        
        scene.getStylesheets().add(ParametreController.class.getResource("/UI/Themes/" + fichierCss).toExternalForm());
        if (themeActuel.equals("TripleT")) tripleTActif = true;
    }
}