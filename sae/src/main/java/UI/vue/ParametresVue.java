package UI.vue;

import UI.Controller.ParametreController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ParametresVue {
    private final Stage parentStage;

    public ParametresVue(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void afficher() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(parentStage);
        popup.setTitle("Paramètres");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label lblTitre = new Label("Paramètres de l'application");
        lblTitre.getStyleClass().add("title-label");

        Label lblTheme = new Label("Thème de l'interface :");
        lblTheme.getStyleClass().add("subtitle-label");

        ComboBox<String> comboTheme = new ComboBox<>();

        comboTheme.getItems().addAll("Clair", "Sombre", "Forêt");
        
        comboTheme.setValue(ParametreController.getThemeActuel());

        comboTheme.setOnAction(e -> {
            ParametreController.setThemeActuel(comboTheme.getValue());
            
            ParametreController.appliquerTheme(popup.getScene());
            if (parentStage.getScene() != null) {
                ParametreController.appliquerTheme(parentStage.getScene());
            }
        });

        Button btnFermer = new Button("Fermer");
        btnFermer.getStyleClass().add("btn-primary");
        btnFermer.setOnAction(e -> popup.close());

        root.getChildren().addAll(lblTitre, lblTheme, comboTheme, btnFermer);

        Scene scene = new Scene(root, 350, 250);
        popup.setScene(scene);
        ParametreController.appliquerTheme(scene); 
        
        popup.showAndWait();
    }
}