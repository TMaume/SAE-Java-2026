package UI.vue;

import UI.Controller.AjouterBoiteController;
import UI.Controller.CollectionController; // Ajout de l'import pour le nouveau controller
import App.RoleUtilisateur;
import UI.Controller.ParametreController;
import UI.Controller.AuthController;
import UI.Controller.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardVue {
    private final Stage stage;
    private final DashboardController controller;
    private final AuthController authController;
    private Button boutonActif = null;
    private int compteurEasterEgg = 0;

    public DashboardVue(Stage stage, DashboardController controller, AuthController authController) {
        this.stage = stage;
        this.controller = controller;
        this.authController = authController;
    }

    public void afficher() {
        BorderPane root = new BorderPane();

        StackPane conteneurCentral = new StackPane();
        conteneurCentral.setPadding(new Insets(30));
        conteneurCentral.getChildren().add(creerVueDefaut()); 

        VBox sidebar = creerSidebar(conteneurCentral);
        sidebar.getStyleClass().add("sidebar-pane");
        
        HBox header = creerHeader(sidebar);
        header.getStyleClass().add("header-pane");

        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(conteneurCentral);

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

    private VBox creerVueDefaut() {
        VBox vueDefaut = new VBox(10);
        vueDefaut.setAlignment(Pos.CENTER);
        
        Label lblMessage1 = new Label("Bienvenue sur le tableau de bord.");
        lblMessage1.getStyleClass().add("title-label");
        
        Label lblMessage2 = new Label("Utilisez le menu de gauche pour naviguer.");
        lblMessage2.getStyleClass().add("subtitle-label");
        
        vueDefaut.getChildren().addAll(lblMessage1, lblMessage2);
        return vueDefaut;
    }

    private void afficherCatalogue(StackPane conteneurCentral, Button btnCatalogue) {
        CatalogueVue catalogueVue = new CatalogueVue(controller.getBoiteService(), controller.getThemeService(), controller.getCollectionService(), boite -> {
            // APRÈS
            DetailBoiteVue detailVue = new DetailBoiteVue(boite, controller.getBoiteService(), controller.getCollectionService(), () -> afficherCatalogue(conteneurCentral, btnCatalogue));
            controller.chargerContenu(conteneurCentral, detailVue);
        });
        controller.chargerContenu(conteneurCentral, catalogueVue.getVue());
        activerBouton(btnCatalogue);
    }

    // --- NOUVELLE MÉTHODE POUR LA COLLECTION ---
    private void afficherCollection(StackPane conteneurCentral, Button btnCollection) {
        CollectionVue collectionVue = new CollectionVue();

        CollectionController collectionController = new CollectionController(
            collectionVue, 
            controller.getCollectionService(), 
            itemClique -> {
                // On réutilise DetailBoiteVue : on lui passe la boîte et on configure le retour vers la collection !
                DetailBoiteVue detailVue = new DetailBoiteVue(
                    itemClique.getBoite(), 
                    controller.getBoiteService(),
                    controller.getCollectionService(),
                    () -> afficherCollection(conteneurCentral, btnCollection)
                );
                controller.chargerContenu(conteneurCentral, detailVue);
            }
        );

        controller.chargerContenu(conteneurCentral, collectionVue.getVue());
        activerBouton(btnCollection);
    }
    // -------------------------------------------

    private void afficherCreationBoite(StackPane conteneurCentral, Button btnAddBoite) {
        CreerBoiteVue creerBoiteVue = new CreerBoiteVue();
        new AjouterBoiteController(creerBoiteVue, controller.getBoiteService(), controller.getThemeService());
        controller.chargerContenu(conteneurCentral, creerBoiteVue);
        activerBouton(btnAddBoite);
    }
    private void affichageCreationTheme(StackPane conteneurCentral, Button btn) {
        CreerThemeVue vue = new CreerThemeVue(controller.getThemeService());
        controller.chargerContenu(conteneurCentral, vue);
        activerBouton(btn);
    }

    // private void afficherModifBoite(StackPane conteneurCentral, Button btnModContenu) {
    //     ModifierBoiteVue modifierBoiteVue = new ModifierBoiteVue();
    //     controller.chargerContenu(conteneurCentral, modifierBoiteVue);
    //     activerBouton(btnModContenu);
    // }

    private VBox creerSidebar(StackPane conteneurCentral){
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(240);

        Label lblMenuClient = new Label("Menu Client");
        lblMenuClient.getStyleClass().add("subtitle-label");
        sidebar.getChildren().add(lblMenuClient);

        Button btnCatalogue = creerBoutonMenu("Consulter le catalogue");
        Button btnTheme = creerBoutonMenu("Explorer par thème");
        Button btnStats = creerBoutonMenu("Statistiques d'une boîte");
        Button btnPiece = creerBoutonMenu("Rechercher par pièce");
        Button btnCollection = creerBoutonMenu("Gérer ma collection");
        Button btnComposer = creerBoutonMenu("Composer une boîte");

        sidebar.getChildren().addAll(btnCatalogue, btnTheme, btnStats, btnPiece, btnCollection, btnComposer);

        btnCatalogue.setOnAction(e -> afficherCatalogue(conteneurCentral, btnCatalogue));

        btnTheme.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Exploration par thèmes (À faire)"));
            activerBouton(btnTheme);
        });
        btnStats.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Statistiques détaillées (À faire)"));
            activerBouton(btnStats);
        });
        btnPiece.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Recherche par pièces (À faire)"));
            activerBouton(btnPiece);
        });
        
        btnCollection.setOnAction(e -> afficherCollection(conteneurCentral, btnCollection));
        
        btnComposer.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Outil de composition personnalisée (À faire)"));
            activerBouton(btnComposer);
        });

        if (controller.getUtilisateurConnecte().getRole() == RoleUtilisateur.ADMIN) {
            Separator separator = new Separator();
            separator.setPadding(new Insets(10, 0, 10, 0));

            Label lblMenuAdmin = new Label("Options Administrateur");
            lblMenuAdmin.getStyleClass().add("subtitle-label");

            Button btnAddBoite = creerBoutonMenu("[Admin] Ajouter une boîte");
            Button btnAddPiece = creerBoutonMenu("[Admin] Ajouter une pièce");
            Button btnAddTheme = creerBoutonMenu("[Admin] Créer un thème");
            Button btnModContenu = creerBoutonMenu("[Admin] Modifier un contenu");

            sidebar.getChildren().addAll(separator, lblMenuAdmin, btnAddBoite, btnAddPiece, btnAddTheme, btnModContenu);

            btnAddBoite.setOnAction(e -> afficherCreationBoite(conteneurCentral, btnAddBoite));
            btnAddPiece.setOnAction(e -> {
                controller.chargerContenu(conteneurCentral, new Label("Formulaire : Ajouter une pièce (À faire)"));
                activerBouton(btnAddPiece);
            });
            btnAddTheme.setOnAction(e -> affichageCreationTheme(conteneurCentral, btnAddTheme));
            // btnModContenu.setOnAction(e -> afficherModifBoite(conteneurCentral, btnModContenu));
        }

        return sidebar;
    }

    public void activerBouton(Button nouveauBouton) {
        if (boutonActif != null) {
            boutonActif.getStyleClass().remove("btn-primary");
        }
        
        if (!nouveauBouton.getStyleClass().contains("btn-primary")) {
            nouveauBouton.getStyleClass().add("btn-primary");
        }
        
        boutonActif = nouveauBouton;
    }

    private HBox creerHeader(VBox sidebar) {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Button btnToggleMenu = new Button("☰");
        btnToggleMenu.setOnAction(e -> {
            boolean estVisible = sidebar.isVisible();
            sidebar.setVisible(!estVisible);
            sidebar.setManaged(!estVisible); 

            compteurEasterEgg++;
            if (compteurEasterEgg == 30) {

                ParametreController.setThemeActuel("TripleT");

                ParametreController.appliquerTheme(stage.getScene());

                this.afficher(); 
                
                compteurEasterEgg = 0;
            }
        });

        Label lblBienvenue = new Label("Bienvenue, " + (ParametreController.isTripleTActif() ? "Triple-T" : controller.getUtilisateurConnecte().getIdentifiant()));
        lblBienvenue.getStyleClass().add("subtitle-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 

        // BOUTON PARAMÈTRES
        Button btnParametres = new Button("Paramètres");
        try {
            Image imgParams = new Image(getClass().getResourceAsStream("/images/settings.png"));
            ImageView vueIcone = new ImageView(imgParams);
            vueIcone.setFitHeight(18);
            vueIcone.setFitWidth(18);
            btnParametres.setGraphic(vueIcone);
        } catch (Exception ex) {
            btnParametres.setText("⚙ Paramètres");
        }
        btnParametres.getStyleClass().add("button");
        btnParametres.setOnAction(e -> {
            ParametresVue parametresVue = new ParametresVue(stage);
            parametresVue.afficher();
        });

        Button btnDeconnexion = new Button("Déconnexion");
        btnDeconnexion.getStyleClass().add("btn-danger");
        btnDeconnexion.setOnAction(e -> {
            LoginVue loginVue = new LoginVue(stage, authController);
            loginVue.afficher();
        });

        header.getChildren().addAll(btnToggleMenu, lblBienvenue, spacer, btnParametres, btnDeconnexion);
        
        return header;
    }

    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(8, 12, 8, 12));
        return btn;
    }

    public void resetCompteurEasterEgg() {
        this.compteurEasterEgg = 0;
    }
}