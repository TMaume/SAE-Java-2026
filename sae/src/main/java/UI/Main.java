package UI;

import App.GestionUtilisateurs;
import UI.Controller.AuthController;
import UI.vue.LoginVue;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée principal de l'application JavaFX.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(GestionUtilisateurs.cheminParDefaut());
        AuthController authController = new AuthController(gestionUtilisateurs);

        LoginVue loginVue = new LoginVue(primaryStage, authController);
        loginVue.afficher();

        primaryStage.setTitle("Briqu'IUTO - Application de gestion");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}