package UI.vue;

import App.Utilisateur;
import UI.Controller.AuthController;
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

    public Scene getScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f6f9;");

        Label lblTitre = new Label("Briqu'IUTO - Inscription");
        lblTitre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

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
        btnInscription.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnInscription.setPrefWidth(250);

        Hyperlink linkConnexion = new Hyperlink("Déjà un compte ? Se connecter");

        // --- ACTIONS ---
        btnInscription.setOnAction(e -> {
            Utilisateur u = authController.creerCompte(
                txtIdentifiant.getText(), 
                txtMotDePasse.getText(), 
                txtConfirmMotDePasse.getText(), 
                lblErreur
            );
            if (u != null) {
                System.out.println("Compte créé avec succès !");
                // TODO: Rediriger vers le tableau de bord (DashboardVue)
            }
        });

        linkConnexion.setOnAction(e -> {
            // Basculer vers la vue de connexion
            LoginVue loginVue = new LoginVue(stage, authController);
            stage.setScene(loginVue.getScene());
        });

        root.getChildren().addAll(lblTitre, txtIdentifiant, txtMotDePasse, txtConfirmMotDePasse, btnInscription, linkConnexion, lblErreur);

        return new Scene(root, 800, 600);
    }
}