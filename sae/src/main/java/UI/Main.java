package UI;

import App.GestionUtilisateurs;
import UI.Controller.AuthController;
import UI.vue.LoginVue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialisation du modèle
        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(GestionUtilisateurs.cheminParDefaut());
        
        // 2. Initialisation du contrôleur
        AuthController authController = new AuthController(gestionUtilisateurs);

        // 3. Initialisation de la vue de départ
        LoginVue loginVue = new LoginVue(primaryStage, authController);
        Scene scene = loginVue.getScene();

        // 4. Configuration de la fenêtre principale
        primaryStage.setTitle("Briqu'IUTO - Application de gestion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}