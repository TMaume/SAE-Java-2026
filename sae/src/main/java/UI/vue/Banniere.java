// package UI.vue;

// import javafx.scene.control.Button;
// import javafx.scene.layout.HBox;
// import javafx.scene.control.Label;
// import javafx.geometry.Insets;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.geometry.Pos;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.Priority;


// public class Banniere extends HBox {
        
//     private Button btn_Paramètres;
//     private Button btn_Acceuil;
//     private Button btn_SeDeconnecter;
//     private Button btn_Arreter;
//     private Label bienvenue;

//     public Banniere() {
//         super();

//         this.setPadding(new Insets(15, 20, 15, 20));
//         this.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
//         this.setAlignment(Pos.CENTER_LEFT);

//         bienvenue = new Label("Bienvenue, " + controller.getUtilisateurConnecte().getIdentifiant());
//         bienvenue.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
//         this.getChildren().add(bienvenue);

//         btn_Paramètres = new Button("Paramètres");
//         btn_Paramètres.setGraphic(createIcon("/images/settings.png"));
//         btn_Paramètres.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
//         btn_Paramètres.setOnAction(e -> {
//             //ouvrir la vue des paramètres
//         });
//         this.getChildren().add(btn_Paramètres);

//         Region spacer = new Region();
//         HBox.setHgrow(spacer, Priority.ALWAYS);
//         this.getChildren().add(spacer);

//         btn_SeDeconnecter = new Button("Se déconnecter");
//         btn_SeDeconnecter.setGraphic(createIcon("/images/logout.png"));
//         btn_SeDeconnecter.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
//         btn_SeDeconnecter.setOnAction(e -> {
//             LoginVue loginVue = new LoginVue(stage, authController);
//             loginVue.afficher();
//         });
//         this.getChildren().add(btn_SeDeconnecter);

//         btn_Arreter = new Button("Arrêter");
//         btn_Arreter.setGraphic(createIcon("/images/stop.png"));
//         btn_Arreter.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
//         btn_Arreter.setOnAction(e -> {
//             System.exit(0);
//         });
//         this.getChildren().add(btn_Arreter);
//     }

//     /**
//      * Méthode utilitaire pour charger et redimensionner une image
//      * @param path Le chemin vers l'image dans le dossier resources
//      * @return ImageView prêt à être utilisé dans un bouton
//      */
//     private ImageView createIcon(String path) {
//         try {
//             Image img = new Image(getClass().getResourceAsStream(path));
//             ImageView imageView = new ImageView(img);
            
//             imageView.setFitWidth(20); 
//             imageView.setFitHeight(20);
//             return imageView;

//         } catch (Exception e) {
//             System.err.println("Image introuvable : " + path);
//             return null;
//         }
//     }
// }