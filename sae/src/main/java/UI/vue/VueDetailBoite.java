import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// On fait hériter directement de BorderPane
public class VueDetailBoite extends BorderPane {
    
    public VueDetailBoite() {
        super();

        // 1. CONTENEUR GLOBAL (qui est maintenant la classe elle-même : "this")
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #121212;"); // Fond sombre pour la maquette

        // 2. GAUCHE : L'IMAGE
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(""));
            imageView.setImage(image);
            imageView.setFitWidth(230);
            imageView.setFitHeight(230);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Image introuvable. Vérifie le chemin d'accès.");
        }
        
        VBox imageWrapper = new VBox(imageView);
        imageWrapper.setAlignment(Pos.CENTER);
        imageWrapper.setPrefSize(250, 250);
        imageWrapper.setStyle("-fx-border-color: white; -fx-border-width: 1;");

        // On place l'image à gauche du conteneur principal
        this.setLeft(imageWrapper);

        // 3. DROITE : LE BORDERPANE DES DETAILS
        BorderPane detailPrecis = new BorderPane();
        BorderPane.setMargin(detailPrecis, new Insets(0, 0, 0, 30)); 

        Label titleLabel = new Label("Titre par défaut");
        titleLabel.setPrefSize(300, 50);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        detailPrecis.setTop(titleLabel);

        // 3b. EN BAS : Les détails composés avec des labels
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(25);
        detailsGrid.setVgap(0);
        detailsGrid.setPadding(new Insets(20, 0, 0, 0));

        String[] cles = {"Numéro", "Nom", "Thème", "Année", "Nombre de pièces"};
        String[] valeurs = {"", "", "", "", ""};

        // Implémentation des labels pour les détails de la boîte
        for (int i = 0; i < cles.length; i++) {
            Label cleLabel = new Label(cles[i]);
            cleLabel.setPrefSize(100, 30);
            cleLabel.setAlignment(Pos.CENTER);
            cleLabel.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-text-fill: white;");

            Label valeurLabel = new Label(valeurs[i]);
            valeurLabel.setPrefSize(175, 30);
            valeurLabel.setAlignment(Pos.CENTER);
            valeurLabel.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-text-fill: white;");

            detailsGrid.add(cleLabel, 0, i);
            detailsGrid.add(valeurLabel, 1, i);
        }

        detailPrecis.setBottom(detailsGrid);
        this.setCenter(detailPrecis);
    }
}