package UI.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreerMenuVue extends VBox {

    public CreerMenuVue(Runnable actionAjouterBoite, Runnable actionAjouterPiece, Runnable actionCreerTheme) {
        setAlignment(Pos.CENTER);
        setSpacing(40);
        setPadding(new Insets(40));

        Label lblTitre = new Label("Menu de Création Administrateur");
        lblTitre.getStyleClass().add("title-label");

        Label lblSousTitre = new Label("Sélectionnez l'élément que vous souhaitez ajouter au catalogue de Briqu'IUTO.");
        lblSousTitre.getStyleClass().add("subtitle-label");

        HBox conteneurBoutons = new HBox(40);
        conteneurBoutons.setAlignment(Pos.CENTER);

        Button btnBoite = creerGrosBouton("Ajouter une boîte", "/images/add_box.png");
        btnBoite.setOnAction(e -> actionAjouterBoite.run());

        Button btnPiece = creerGrosBouton("Ajouter une pièce", "/images/add_piece.png");
        btnPiece.setOnAction(e -> actionAjouterPiece.run());

        Button btnTheme = creerGrosBouton("Créer un thème", "/images/add_theme.png");
        btnTheme.setOnAction(e -> actionCreerTheme.run());

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

        Label lblFallback = new Label("+");
        lblFallback.setStyle("-fx-font-size: 60px;");
        contenu.getChildren().add(lblFallback);
    
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