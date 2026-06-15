package UI.vue;

import App.RoleUtilisateur;
import UI.Controller.AuthController;
import UI.Controller.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardVue {
    private final Stage stage;
    private final DashboardController controller;
    private final AuthController authController;

    private Button boutonActif = null;

    public DashboardVue(Stage stage, DashboardController controller, AuthController authController) {
        this.stage = stage;
        this.controller = controller;
        this.authController = authController;
    }

    public void afficher() {
        BorderPane root = new BorderPane();

        // 1. Initialisation de la zone centrale
        StackPane conteneurCentral = new StackPane();
        conteneurCentral.setPadding(new Insets(30));
        conteneurCentral.setStyle("-fx-background-color: #fafbfc;");
        conteneurCentral.getChildren().add(creerVueDefaut()); 

        // 2. Initialisation des menus
        VBox sidebar = creerSidebar(conteneurCentral);
        HBox header = creerHeader(sidebar);

        // 3. Assemblage
        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(conteneurCentral);

        // 4. Gestion de la taille d'écran dynamique
        Scene sceneActuelle = stage.getScene();
        if (sceneActuelle == null) {
            stage.setScene(new Scene(root, 1024, 768));
        } else {
            sceneActuelle.setRoot(root);
        }
    }

    private VBox creerVueDefaut() {
        VBox vueDefaut = new VBox(10);
        vueDefaut.setAlignment(Pos.CENTER);
        
        Label lblMessage1 = new Label("Bienvenue sur le tableau de bord.");
        lblMessage1.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        Label lblMessage2 = new Label("Utilisez le menu de gauche pour naviguer.");
        lblMessage2.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        vueDefaut.getChildren().addAll(lblMessage1, lblMessage2);
        return vueDefaut;
    }

    private VBox creerSidebar(StackPane conteneurCentral){
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        // --- Section Client ---
        Label lblMenuClient = new Label("Menu Client");
        lblMenuClient.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
        sidebar.getChildren().add(lblMenuClient);

        Button btnCatalogue = creerBoutonMenu("Consulter le catalogue");
        Button btnTheme = creerBoutonMenu("Explorer par thème");
        Button btnStats = creerBoutonMenu("Statistiques d'une boîte");
        Button btnPiece = creerBoutonMenu("Rechercher par pièce");
        Button btnCollection = creerBoutonMenu("Gérer ma collection");
        Button btnComposer = creerBoutonMenu("Composer une boîte");

        sidebar.getChildren().addAll(btnCatalogue, btnTheme, btnStats, btnPiece, btnCollection, btnComposer);

        // Actions Client modifiées
        btnCatalogue.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Catalogue des boîtes LEGO (À faire)"));
            activerBouton(btnCatalogue);
        });
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
        btnCollection.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Ma Collection personnelle (À faire)"));
            activerBouton(btnCollection);
        });
        btnComposer.setOnAction(e -> {
            controller.chargerContenu(conteneurCentral, new Label("Vue : Outil de composition personnalisée (À faire)"));
            activerBouton(btnComposer);
        });

       // --- Section Admin ---
        if (controller.getUtilisateurConnecte().getRole() == RoleUtilisateur.ADMIN) {
            Separator separator = new Separator();
            separator.setPadding(new Insets(10, 0, 10, 0));

            Label lblMenuAdmin = new Label("Options Administrateur");
            lblMenuAdmin.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #c0392b;");

            Button btnAddBoite = creerBoutonMenu("[Admin] Ajouter une boîte");
            Button btnAddPiece = creerBoutonMenu("[Admin] Ajouter une pièce");
            Button btnAddTheme = creerBoutonMenu("[Admin] Créer un thème");
            Button btnModContenu = creerBoutonMenu("[Admin] Modifier un contenu");

            sidebar.getChildren().addAll(separator, lblMenuAdmin, btnAddBoite, btnAddPiece, btnAddTheme, btnModContenu);

            btnAddBoite.setOnAction(e -> {
                controller.chargerContenu(conteneurCentral, new Label("Formulaire : Ajouter une boîte (À faire)"));
                activerBouton(btnAddBoite);
            });
            btnAddPiece.setOnAction(e -> {
                controller.chargerContenu(conteneurCentral, new Label("Formulaire : Ajouter une pièce (À faire)"));
                activerBouton(btnAddPiece);
            });
            btnAddTheme.setOnAction(e -> {
                controller.chargerContenu(conteneurCentral, new Label("Formulaire : Créer un thème (À faire)"));
                activerBouton(btnAddTheme);
            });
            btnModContenu.setOnAction(e -> {
                controller.chargerContenu(conteneurCentral, new Label("Formulaire : Modifier le contenu d'une boîte (À faire)"));
                activerBouton(btnModContenu);
            });
        }

        return sidebar;
    }

    public void activerBouton(Button nouveauBouton) {
        if (boutonActif != null) {
            boutonActif.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f5f6f; -fx-font-size: 13px; -fx-cursor: hand;");
        }
        
        nouveauBouton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold;");
        

        boutonActif = nouveauBouton;
    }

    private HBox creerHeader(VBox sidebar) {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        // Bouton pour plier le menu
        Button btnToggleMenu = new Button("☰");
        btnToggleMenu.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: #2c3e50;");
        btnToggleMenu.setOnAction(e -> {
            boolean estVisible = sidebar.isVisible();
            sidebar.setVisible(!estVisible);
            sidebar.setManaged(!estVisible); 
        });

        Label lblBienvenue = new Label("Bienvenue, " + controller.getUtilisateurConnecte().getIdentifiant());
        lblBienvenue.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 

        Button btnDeconnexion = new Button("Déconnexion");
        btnDeconnexion.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDeconnexion.setOnAction(e -> {
            LoginVue loginVue = new LoginVue(stage, authController);
            loginVue.afficher();
        });

        header.getChildren().addAll(btnToggleMenu, lblBienvenue, spacer, btnDeconnexion);
        
        return header;
    }

    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(8, 12, 8, 12));

        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f5f6f; -fx-font-size: 13px; -fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> {
            if (btn != boutonActif) {
                btn.setStyle("-fx-background-color: #f0f2f5; -fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-cursor: hand;");
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (btn != boutonActif) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f5f6f; -fx-font-size: 13px; -fx-cursor: hand;");
            }
        });
        
        return btn;
    }
}