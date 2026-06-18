package UI.vue;

import App.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CreerBoiteVue extends VBox {
    
    private GridPane g = new GridPane();
    private Label titre = new Label("Ajouter une nouvelle boîte");
    
    private TextField tfNum = new TextField();
    private TextField tfNom = new TextField();
    private TextField tfAnnee = new TextField();
    private ComboBox<Theme> themeBox = new ComboBox<>();
    private TextField tfImg = new TextField();
    
    private Button bVisio = new Button("Visionner l'image");
    private ImageView apercu = new ImageView();
    private Button b = new Button("Ajouter la boîte");
    private Label lbinfo = new Label("");

    public CreerBoiteVue() {
        this.setSpacing(20.0);
        this.setPadding(new Insets(25.0));
        this.setMaxWidth(500);

        this.titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        g.setHgap(15.0);
        g.setVgap(14.0);

        g.add(new Label("Numéro de la boîte :"), 0, 0);
        this.tfNum.setPromptText("Ex: 75192");
        this.tfNum.setPrefWidth(240);
        g.add(tfNum, 1, 0);

        g.add(new Label("Nom de la boîte :"), 0, 1);
        this.tfNom.setPromptText("Ex: Millennium Falcon");
        this.tfNom.setPrefWidth(240);
        g.add(tfNom, 1, 1);

        g.add(new Label("Année de sortie :"), 0, 2);
        this.tfAnnee.setPromptText("Ex: 2017");
        this.tfAnnee.setPrefWidth(240);
        g.add(tfAnnee, 1, 2);

        g.add(new Label("Thème :"), 0, 3);
        this.themeBox.setPromptText("Sélectionner un thème");
        this.themeBox.setPrefWidth(240);
        g.add(themeBox, 1, 3);

        g.add(new Label("Image (URL) :"), 0, 4);
        this.tfImg.setPromptText("https://...");
        this.tfImg.setPrefWidth(240);
        g.add(tfImg, 1, 4);

        g.add(bVisio, 1, 5);

        // Configuration de l'aperçu
        this.apercu.setFitWidth(200);
        this.apercu.setFitHeight(200);
        this.apercu.setPreserveRatio(true);
        this.apercu.setVisible(false);

        VBox conteneurApercu = new VBox(apercu);
        conteneurApercu.setAlignment(Pos.CENTER);

        VBox actionBox = new VBox(10);
        actionBox.getChildren().addAll(this.b, this.lbinfo);

        this.getChildren().addAll(this.titre, g, conteneurApercu, actionBox);

        // Action par défaut pour l'image
        this.bVisio.setOnAction(e -> chargerApercu());
    }

    private void chargerApercu() {
        String url = tfImg.getText().trim();
        if (url.isEmpty()) {
            setLbinfo("Veuillez saisir une URL d'image avant de visionner.", Color.RED);
            apercu.setVisible(false);
            return;
        }
        try {
            Image image = new Image(url, true);
            apercu.setImage(image);
            apercu.setVisible(true);
            setLbinfo("", Color.BLACK);
            image.errorProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    setLbinfo("Impossible de charger l'image depuis cette URL.", Color.RED);
                    apercu.setVisible(false);
                }
            });
        } catch (Exception ex) {
            setLbinfo("URL d'image invalide.", Color.RED);
            apercu.setVisible(false);
        }
    }

    public void clear() {
        this.tfNum.clear();
        this.tfNom.clear();
        this.tfAnnee.clear();
        this.tfImg.clear();
        this.themeBox.getSelectionModel().clearSelection();
        this.apercu.setVisible(false);
        this.apercu.setImage(null);
        this.setLbinfo("", Color.BLACK);
    }

    // --- Getters ---
    public String getTfNum() {
        return tfNum.getText();
    }
    
    public String getTfNom() {
        return tfNom.getText();
    }
    
    public String getTfAnnee() {
        return tfAnnee.getText();
    }
    
    public String getTfImg() {
        return tfImg.getText();
    }
    
    public Theme getTheme() {
        return themeBox.getValue();
    }
    
    public ComboBox<Theme> getThemeBox() {
        return themeBox;
    }

    // Permet au contrôleur externe d'attacher l'événement (ex: c.ajout())
    public Button getBoutonAjouter() {
        return b;
    }

    // --- Setters ---
    public void setLbinfo(String txt, Color couleur) {
        this.lbinfo.setText(txt);
        this.lbinfo.setTextFill(couleur);
    }
}