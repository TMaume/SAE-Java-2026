package UI.vue;
import App.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreerBoiteVue extends VBox {
    public TextField numeroField;
    public TextField nomField;
    public TextField anneeField;
    public ComboBox<Theme> themeComboBox;
    public TextField imageField;
    public Button visionnerImageButton;
    public ImageView apercuImageView;
    public Button ajouterButton;
    public Label messageLabel;

    public CreerBoiteVue() {
        this.setSpacing(20.0);
        this.setPadding(new Insets(25.0));
        this.setMaxWidth(420);

        Label titreLabel = new Label("Ajouter une nouvelle boîte");
        titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15.0);
        grid.setVgap(14.0);

        grid.add(new Label("Numéro de la boîte :"), 0, 0);
        numeroField = new TextField();
        numeroField.setPromptText("Ex: 75192");
        numeroField.setPrefWidth(240);
        grid.add(numeroField, 1, 0);

        grid.add(new Label("Nom de la boîte :"), 0, 1);
        nomField = new TextField();
        nomField.setPromptText("Ex: Millennium Falcon");
        nomField.setPrefWidth(240);
        grid.add(nomField, 1, 1);

        grid.add(new Label("Année de sortie :"), 0, 2);
        anneeField = new TextField();
        anneeField.setPromptText("Ex: 2017");
        anneeField.setPrefWidth(240);
        grid.add(anneeField, 1, 2);

        grid.add(new Label("Thème :"), 0, 3);
        themeComboBox = new ComboBox<>();
        themeComboBox.setPromptText("Sélectionner un thème");
        themeComboBox.setPrefWidth(240);
        grid.add(themeComboBox, 1, 3);

        grid.add(new Label("Image (URL) :"), 0, 4);
        imageField = new TextField();
        imageField.setPromptText("https://...");
        imageField.setPrefWidth(240);
        grid.add(imageField, 1, 4);

        visionnerImageButton = new Button("Visionner l'image");
        grid.add(visionnerImageButton, 1, 5);

        // Zone d'aperçu de l'image, masquée tant qu'aucune image n'est chargée
        apercuImageView = new ImageView();
        apercuImageView.setFitWidth(200);
        apercuImageView.setFitHeight(200);
        apercuImageView.setPreserveRatio(true);
        apercuImageView.setVisible(false);

        VBox conteneurApercu = new VBox(apercuImageView);
        conteneurApercu.setAlignment(Pos.CENTER);

        VBox actionBox = new VBox(10);
        ajouterButton = new Button("Ajouter la boîte");
        messageLabel = new Label();
        actionBox.getChildren().addAll(ajouterButton, messageLabel);

        this.getChildren().addAll(titreLabel, grid, conteneurApercu, actionBox);

        // Action par défaut : charge l'aperçu à partir de l'URL saisie
        visionnerImageButton.setOnAction(e -> chargerApercu());
    }

    /**
     * Charge et affiche l'image depuis l'URL saisie dans imageField.
     * Affiche un message d'erreur dans messageLabel si l'URL est vide ou invalide.
     */
    private void chargerApercu() {
        String url = imageField.getText().trim();
        if (url.isEmpty()) {
            messageLabel.setText("Veuillez saisir une URL d'image avant de visionner.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            apercuImageView.setVisible(false);
            return;
        }
        try {
            Image image = new Image(url, true);
            apercuImageView.setImage(image);
            apercuImageView.setVisible(true);
            messageLabel.setText("");
            image.errorProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    messageLabel.setText("Impossible de charger l'image depuis cette URL.");
                    messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                    apercuImageView.setVisible(false);
                }
            });
        } catch (Exception ex) {
            messageLabel.setText("URL d'image invalide.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            apercuImageView.setVisible(false);
        }
    }
}