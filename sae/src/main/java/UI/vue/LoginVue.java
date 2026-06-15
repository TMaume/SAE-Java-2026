package UI.vue;

import App.Utilisateur;
import UI.Controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginVue {
    private final Stage stage;
    private final AuthController authController;

    public LoginVue(Stage stage, AuthController authController) {
        this.stage = stage;
        this.authController = authController;
    }

    public Scene getScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f6f9;");

        Label lblTitre = new Label("Briqu'IUTO - Connexion");
        lblTitre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField txtIdentifiant = new TextField();
        txtIdentifiant.setPromptText("Identifiant");
        txtIdentifiant.setMaxWidth(250);

        PasswordField txtMotDePasse = new PasswordField();
        txtMotDePasse.setPromptText("Mot de passe");
        txtMotDePasse.setMaxWidth(250);

        Label lblErreur = new Label();
        lblErreur.setStyle("-fx-text-fill: red;");

        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnConnexion.setPrefWidth(250);

        Hyperlink linkInscription = new Hyperlink("Pas encore de compte ? S'inscrire");

        // --- ACTIONS ---
        btnConnexion.setOnAction(e -> {
            Utilisateur u = authController.connecter(txtIdentifiant.getText(), txtMotDePasse.getText(), lblErreur);
            if (u != null) {
                System.out.println("Connexion réussie : " + u.getIdentifiant() + " (Rôle: " + u.getRole() + ")");
                // TODO: Rediriger vers le tableau de bord (DashboardVue) en passant l'utilisateur
            }
        });

        linkInscription.setOnAction(e -> {
            // Basculer vers la vue d'inscription
            RegisterVue registerVue = new RegisterVue(stage, authController);
            stage.setScene(registerVue.getScene());
        });

        root.getChildren().addAll(lblTitre, txtIdentifiant, txtMotDePasse, btnConnexion, linkInscription, lblErreur);

        return new Scene(root, 800, 600);
    }
}