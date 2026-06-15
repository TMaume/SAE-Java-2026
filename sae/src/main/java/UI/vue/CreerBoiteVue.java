package vue;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CreerBoiteVue extends VBox {

    TextField numeroField;
    TextField nomField;
    TextField anneeField;
    ComboBox<Theme> themeComboBox;
    Button ajouterButton;
    Label messageLabel;

    public CreerBoiteVue() {
        this.setSpacing(15.0);
        this.setPadding(new Insets(20.0));

        Label titreLabel = new Label("Ajouter une nouvelle boîte");
        titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10.0);
        grid.setVgap(10.0);

        grid.add(new Label("Numéro de la boîte :"), 0, 0);
        numeroField = new TextField();
        numeroField.setPromptText("Ex: 75192");
        grid.add(numeroField, 1, 0);

        grid.add(new Label("Nom de la boîte :"), 0, 1);
        nomField = new TextField();
        nomField.setPromptText("Ex: Millennium Falcon");
        grid.add(nomField, 1, 1);

        grid.add(new Label("Année de sortie :"), 0, 2);
        anneeField = new TextField();
        anneeField.setPromptText("Ex: 2017");
        grid.add(anneeField, 1, 2);

        grid.add(new Label("Thème :"), 0, 3);
        themeComboBox = new ComboBox<>();
        themeComboBox.setPromptText("Sélectionner un thème");
        grid.add(themeComboBox, 1, 3);

        ajouterButton = new Button("Ajouter la boîte");
        messageLabel = new Label();

        this.getChildren().addAll(titreLabel, grid, ajouterButton, messageLabel);
    }
}  