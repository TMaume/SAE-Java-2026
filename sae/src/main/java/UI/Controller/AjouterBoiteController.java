package UI.Controller;

import App.Boite;
import App.BoiteService;
import App.ThemeService;
import App.Theme;
import UI.Exception.BoiteExistanteException;
import UI.vue.CreerBoiteVue;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import java.util.List;

public class AjouterBoiteController {
    private CreerBoiteVue vue;
    private BoiteService boiteService;
    private ThemeService themeService;

    public AjouterBoiteController(CreerBoiteVue vue, BoiteService boiteService, ThemeService themeService) {
        this.vue = vue;
        this.boiteService = boiteService;
        this.themeService = themeService;
        
        // Utilisation du getter pour récupérer le bouton et lui assigner l'action
        this.vue.getBoutonAjouter().setOnAction(e -> handleAjouterBoite());
        chargerThemes();
    }

    private void chargerThemes() {
        if (themeService != null) {
            // Ta vraie méthode pour lister les thèmes
            List<Theme> themes = themeService.listerThemes();
            if (themes.isEmpty()) {
                // Utilisation de la nouvelle méthode de la vue pour les messages
                vue.setLbinfo("Aucun thème disponible en base de données.", Color.RED);
            } else {
                // Utilisation du getter pour accéder à la ComboBox
                vue.getThemeBox().setItems(FXCollections.observableArrayList(themes));
            }
        }
    }

    private void handleAjouterBoite() {
        // Utilisation des nouveaux getters de la vue pour récupérer les saisies
        String numero = vue.getTfNum().trim();
        String nom = vue.getTfNom().trim();
        String anneeStr = vue.getTfAnnee().trim();
        String image = vue.getTfImg().trim();
        Theme themeSelectionne = vue.getTheme();

        if (numero.isEmpty() || nom.isEmpty() || anneeStr.isEmpty() || themeSelectionne == null) {
            vue.setLbinfo("Erreur : Veuillez remplir tous les champs et sélectionner un thème.", Color.RED);
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            
            // On respecte le constructeur de ta classe Boite tel que tu l'avais défini
            Boite nouvelleBoite = new Boite(numero, nom, annee, themeSelectionne, image.isEmpty() ? null : image);
            
            boiteService.ajouterBoite(nouvelleBoite);
            
            vue.setLbinfo("La boîte " + numero + " a été ajoutée avec succès !", Color.GREEN);
            
            // Utilisation de la méthode clear() centralisée dans la vue
            vue.clear();
            
        } catch (NumberFormatException e) {
            vue.setLbinfo("Erreur : L'année doit être un nombre entier valide.", Color.RED);
        } catch (BoiteExistanteException e) {
            vue.setLbinfo(e.getMessage(), Color.RED);
        }
    }
}