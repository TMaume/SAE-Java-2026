package UI.vue;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Banniere extends HBox {
        
    private Button btn_Paramètres;
    private Button btn_Acceuil;
    private Button btn_SeDeconnecter;
    private Button btn_Arreter;
    private Label bienvenue;

    public Banniere() {
        super();
        this.setPadding(new Insets(10, 20, 10, 20));
        this.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        bienvenue = new Label("Bienvenue sur notre application!");
        this.getChildren().add(bienvenue);

        btn_Acceuil = new Button("Accueil");
        btn_Acceuil.setGraphic(createIcon("/images/home.png"));
        this.getChildren().add(btn_Acceuil);

        btn_Paramètres = new Button("Paramètres");
        btn_Paramètres.setGraphic(createIcon("/images/settings.png"));
        this.getChildren().add(btn_Paramètres);

        btn_SeDeconnecter = new Button("Se déconnecter");
        btn_SeDeconnecter.setGraphic(createIcon("/images/logout.png"));
        this.getChildren().add(btn_SeDeconnecter);

        btn_Arreter = new Button("quitter");
        btn_Arreter.setGraphic(createIcon("/images/stop.png"));
        this.getChildren().add(btn_Arreter);
    }

    /**
     * Méthode utilitaire pour charger et redimensionner une image
     * @param path Le chemin vers l'image dans le dossier resources
     * @return ImageView prêt à être utilisé dans un bouton
     */
    private ImageView createIcon(String path) {
        try {
            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView imageView = new ImageView(img);
            
            imageView.setFitWidth(20); 
            imageView.setFitHeight(20);
            return imageView;

        } catch (Exception e) {
            System.err.println("Image introuvable : " + path);
            return null;
        }
    }
}