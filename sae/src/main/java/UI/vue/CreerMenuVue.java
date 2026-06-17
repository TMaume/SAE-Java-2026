package UI.vue;

import UI.Controller.AjouterBoiteController;
import UI.Controller.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CreerMenuVue extends VBox {

    /**
     * Constructeur de la vue.
     * @param conteneurCentral Le panneau central de l'application où l'on va charger les formulaires
     * @param controller Le contrôleur principal qui contient les services (BoiteService, etc.)
     */
    public CreerMenuVue(StackPane conteneurCentral, DashboardController controller) {
        setAlignment(Pos.CENTER);
        setSpacing(40);
        setPadding(new Insets(40));

        Label lblTitre = new Label("Menu de Création Administrateur");
        lblTitre.getStyleClass().add("title-label");

        Label lblSousTitre = new Label("Sélectionnez l'élément que vous souhaitez ajouter au catalogue de Briqu'IUTO.");
        lblSousTitre.getStyleClass().add("subtitle-label");

        HBox conteneurBoutons = new HBox(40);
        conteneurBoutons.setAlignment(Pos.CENTER);

        Button btnBoite = creerGrosBouton("Ajouter une boîte", "/UI/images/add_box.png");
        Button btnPiece = creerGrosBouton("Ajouter une pièce", "/UI/images/add_piece.png");
        Button btnTheme = creerGrosBouton("Créer un thème", "/UI/images/add_theme.png");

        btnBoite.setOnAction(e -> {
            CreerBoiteVue creerBoiteVue = new CreerBoiteVue();
            new AjouterBoiteController(creerBoiteVue, controller.getBoiteService(), controller.getThemeService());
            controller.chargerContenu(conteneurCentral, creerBoiteVue);
        });

        btnPiece.setOnAction(e -> {
            CreerPieceVue CreerPieceVue = new CreerPieceVue(controller.getPieceService());
            controller.chargerContenu(conteneurCentral, CreerPieceVue);
        });

        btnTheme.setOnAction(e -> {
            CreerThemeVue CreerThemeVue = new CreerThemeVue(controller.getThemeService());
            controller.chargerContenu(conteneurCentral, CreerThemeVue);
        });

        conteneurBoutons.getChildren().addAll(btnBoite, btnPiece, btnTheme);

        getChildren().addAll(lblTitre, lblSousTitre, conteneurBoutons);
    }

    private Button creerGrosBouton(String texte, String iconPath) {
        Button btn = new Button();
        btn.setPrefSize(220, 220);
        btn.getStyleClass().add("card");

        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        VBox contenu = new VBox(25);
        contenu.setAlignment(Pos.CENTER);

        try {
            Image img = new Image(getClass().getResourceAsStream(iconPath));
            ImageView vueIcone = new ImageView(img);
            vueIcone.setFitHeight(80);
            vueIcone.setFitWidth(80);
            vueIcone.setPreserveRatio(true);
            contenu.getChildren().add(vueIcone);
        } catch (Exception ex) {
            Label lblFallback = new Label("+");
            lblFallback.setStyle("-fx-font-size: 60px;");
            contenu.getChildren().add(lblFallback);
        }
    
        Label lblTexte = new Label(texte);
        lblTexte.getStyleClass().add("label");
        lblTexte.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        contenu.getChildren().add(lblTexte);
        btn.setGraphic(contenu);

        btn.setOnMouseEntered(e -> btn.setOpacity(0.7));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        return btn;
    }

}