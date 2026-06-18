package UI.vue;

import UI.Controller.ParametreController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private final Stage popup = new Stage();

    private VBox root = new VBox(20);
    private Label lblTitre = new Label("Paramètres de l'application");
    private Label lblTheme = new Label("Thème de l'interface :");
    private ComboBox<String> comboTheme = new ComboBox<>();
    private Button btnFermer = new Button("Fermer");

    public ParametresVue(Stage parentStage) {
        this.parentStage = parentStage;
        initialiserInterface();
    }

    private void initialiserInterface() {
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(parentStage);
        popup.setTitle("Paramètres");

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        lblTitre.getStyleClass().add("title-label");
        lblTheme.getStyleClass().add("subtitle-label");
        btnFermer.getStyleClass().add("btn-primary");

        comboTheme.getItems().addAll("Clair", "Sombre", "Forêt", "Lego");
        comboTheme.setValue(ParametreController.getThemeActuel());

        // Remplacement de la lambda par une classe anonyme
        comboTheme.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ParametreController.setThemeActuel(comboTheme.getValue());
                
                ParametreController.appliquerTheme(popup.getScene());
                if (parentStage.getScene() != null) {
                    ParametreController.appliquerTheme(parentStage.getScene());
                }
            }
        });

        // Remplacement de la lambda par une classe anonyme
        btnFermer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.close();
            }
        });

        root.getChildren().addAll(lblTitre, lblTheme, comboTheme, btnFermer);

        Scene scene = new Scene(root, 350, 250);
        popup.setScene(scene);
        ParametreController.appliquerTheme(scene); 
    }

    public void afficher() {
        popup.showAndWait();
    }

    public ComboBox<String> getComboTheme() {
        return comboTheme;
    }

    public Button getBtnFermer() {
        return btnFermer;
    }
}