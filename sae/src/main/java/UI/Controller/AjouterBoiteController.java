package UI.Controller;

import App.Boite;
import App.BoiteService;
import App.ThemeService;
import App.Theme;
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
        this.vue.ajouterButton.setOnAction(e -> handleAjouterBoite());
        chargerThemes();
    }

    private void chargerThemes() {
        if (themeService != null) {
            List<Theme> themes = themeService.listerThemes();
            if (themes.isEmpty()) {
                afficherMessage("Aucun thème disponible en base de données.", Color.RED);
            } else {
                vue.themeComboBox.setItems(FXCollections.observableArrayList(themes));
            }
        }
    }

    private void handleAjouterBoite() {
        String numero = vue.numeroField.getText().trim();
        String nom = vue.nomField.getText().trim();
        String anneeStr = vue.anneeField.getText().trim();
        Theme themeSelectionne = vue.themeComboBox.getValue();

        if (numero.isEmpty() || nom.isEmpty() || anneeStr.isEmpty() || themeSelectionne == null) {
            afficherMessage("Erreur : Veuillez remplir tous les champs et sélectionner un thème.", Color.RED);
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            // TODO: vérifier le 5e paramètre attendu par le constructeur Boite (String, String, Integer, Theme, String)
            // Remplace "" par la vraie valeur (ex: description, image, état...)
            Boite nouvelleBoite = new Boite(numero, nom, annee, themeSelectionne, "");
            boiteService.ajouterBoite(nouvelleBoite); // TODO: vérifie le nom exact de cette méthode dans BoiteService
            afficherMessage("La boîte " + numero + " a été ajoutée avec succès !", Color.GREEN);
            viderChamps();
        } catch (NumberFormatException e) {
            afficherMessage("Erreur : L'année doit être un nombre entier valide.", Color.RED);
        }
    }

    private void afficherMessage(String message, Color couleur) {
        vue.messageLabel.setText(message);
        vue.messageLabel.setTextFill(couleur);
    }

    private void viderChamps() {
        vue.numeroField.clear();
        vue.nomField.clear();
        vue.anneeField.clear();
        vue.themeComboBox.getSelectionModel().clearSelection();
    }
}