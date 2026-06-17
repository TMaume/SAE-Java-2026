package UI.vue;

import App.Utilisateur;
import UI.Controller.AuthController;
import UI.Controller.ParametreController;
import UI.Controller.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterVue {
    private final Stage stage;
    private final AuthController authController;

    public RegisterVue(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
    }

    public void afficher() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label lblTitre = new Label("Briqu'IUTO - Inscription");
        lblTitre.getStyleClass().add("title-label");

        TextField txtIdentifiant = new TextField();
        txtIdentifiant.setPromptText("Choisissez un identifiant");
        txtIdentifiant.setMaxWidth(250);

        PasswordField txtMotDePasse = new PasswordField();
        txtMotDePasse.setPromptText("Mot de passe");
        txtMotDePasse.setMaxWidth(250);

        PasswordField txtConfirmMotDePasse = new PasswordField();
        txtConfirmMotDePasse.setPromptText("Confirmer le mot de passe");
        txtConfirmMotDePasse.setMaxWidth(250);

        Label lblErreur = new Label();
        lblErreur.setStyle("-fx-text-fill: red;");

        Button btnInscription = new Button("Créer mon compte");
        btnInscription.getStyleClass().add("btn-primary");
        btnInscription.setPrefWidth(250);

        Hyperlink linkConnexion = new Hyperlink("Déjà un compte ? Se connecter");

        btnInscription.setOnAction(e -> {
            Utilisateur u = authController.creerCompte(
                txtIdentifiant.getText(), 
                txtMotDePasse.getText(), 
                txtConfirmMotDePasse.getText(), 
                lblErreur
            );
            if (u != null) {
                DashboardController dashboardController = new DashboardController(u, null, null, null, null);
                DashboardVue dashboardVue = new DashboardVue(stage, dashboardController, authController);
                dashboardVue.afficher(); 
            }
        });

        linkConnexion.setOnAction(e -> {
            LoginVue loginVue = new LoginVue(stage, authController);
            loginVue.afficher(); 
        });

        root.getChildren().addAll(lblTitre, txtIdentifiant, txtMotDePasse, txtConfirmMotDePasse, btnInscription, linkConnexion, lblErreur);

        Scene sceneActuelle = stage.getScene();
        if (sceneActuelle == null) {
            Scene scene = new Scene(root, 1024, 768);
            stage.setScene(scene);
            ParametreController.appliquerTheme(scene);
        } else {
            sceneActuelle.setRoot(root);
            ParametreController.appliquerTheme(sceneActuelle);
        }
    }
}