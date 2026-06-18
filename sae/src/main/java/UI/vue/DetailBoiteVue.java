package UI.vue;

import App.Boite;
import App.BoiteService;
import App.BoiteStats;
import App.CollectionItem;
import App.CollectionService;
import App.Couleur;
import App.EtatBoite;
import App.FigurineQuantite;
import App.PieceQuantite;
import UI.Controller.ParametreController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Map;

/**
 * Vue affichant les détails complets, les statistiques, les pièces et les figurines d'une boîte LEGO.
 */
public class DetailBoiteVue extends BorderPane {
    private final BoiteService boiteService;
    private final CollectionService collectionService;
    private final Boite boiteComplete;
    
    private int pageCourantePieces = 1;
    private final int taillePagePieces = 20;
    private FlowPane conteneurPiecesGrille;
    private Label lblPaginationPieces;
    private Button btnPrecedentPieces;
    private Button btnSuivantPieces;
    private Button btnAjouterCollection;
    private Label lblMessageCollection;

    /**
     * Construit la vue détaillée d'une boîte (sans gestion de la collection personnelle).
     */
    public DetailBoiteVue(Boite boiteLegere, BoiteService boiteService, Runnable actionRetour) {
        this(boiteLegere, boiteService, null, actionRetour);
    }
 
    /**
     * Construit la vue détaillée d'une boîte.
     */
    public DetailBoiteVue(Boite boiteLegere, BoiteService boiteService, CollectionService collectionService, Runnable actionRetour) {
        this.boiteService = boiteService;
        this.collectionService = collectionService;
        Boite boiteChargee = boiteService.chargerBoiteComplete(boiteLegere.getNumero());
        this.boiteComplete = (boiteChargee != null) ? boiteChargee : boiteLegere;
        
        setPadding(new Insets(30));
        setTop(creerEnTete(actionRetour, this.boiteComplete));
        setCenter(creerOnglets(this.boiteComplete));
    }

    private String formaterRgb(String rgb) {
        if (rgb == null || rgb.isBlank()) {
            return "#ecf0f1";
        }
        if (!rgb.startsWith("#")) {
            return "#" + rgb;
        }
        return rgb;
    }

    private String obtenirCouleurTexte(String rgbHex) {
        if (rgbHex == null || rgbHex.length() != 7) {
            return "black";
        }
        try {
            int r = Integer.parseInt(rgbHex.substring(1, 3), 16);
            int g = Integer.parseInt(rgbHex.substring(3, 5), 16);
            int b = Integer.parseInt(rgbHex.substring(5, 7), 16);
            double luminance = (0.299 * r + 0.587 * g + 0.114 * b);
            return luminance > 150 ? "black" : "white";
        } catch (NumberFormatException e) {
            return "black";
        }
    }

    private HBox creerEnTete(Runnable actionRetour, Boite boite) {
        HBox entete = new HBox(20);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(0, 0, 30, 0));
        
        Button btnRetour = new Button("◄ Retour");
        btnRetour.getStyleClass().add("button");
        btnRetour.setOnAction(e -> actionRetour.run());
        
        String nomTheme = (boite.getTheme() != null) ? boite.getTheme().getNom() : "Inconnu";
        Label lblTitre = new Label("LEGO " + nomTheme + " " + boite.getNumero() + " - " + boite.getNom());
        lblTitre.getStyleClass().add("title-label");
        
        entete.getChildren().addAll(btnRetour, lblTitre);
        return entete;
    }

    private TabPane creerOnglets(Boite boite) {
        TabPane tabPane = new TabPane();
        
        Tab tabInfos = new Tab("Informations & Statistiques");
        tabInfos.setClosable(false);
        tabInfos.setContent(creerContenu(boite));
        
        Tab tabPieces = new Tab("Pièces incluses");
        tabPieces.setClosable(false);
        tabPieces.setContent(creerContenuPieces(boite));
        
        Tab tabFigurines = new Tab("Figurines incluses");
        tabFigurines.setClosable(false);
        tabFigurines.setContent(creerContenuFigurines(boite));
        
        tabPane.getTabs().addAll(tabInfos, tabPieces, tabFigurines);
        return tabPane;
    }

    private HBox creerContenu(Boite boite) {
        HBox contenu = new HBox(30);
        contenu.setAlignment(Pos.TOP_LEFT);
        contenu.setPadding(new Insets(20, 0, 0, 0));
        
        VBox zoneInformations = new VBox(20);
        zoneInformations.getChildren().addAll(
            creerSectionInformations(boite),
            creerSectionStatistiques(boite)
        );
        HBox.setHgrow(zoneInformations, Priority.ALWAYS);
        
        contenu.getChildren().addAll(
            creerSectionImage(boite),
            zoneInformations
        );
        return contenu;
    }

    private BorderPane creerContenuPieces(Boite boite) {
        BorderPane conteneurGlobal = new BorderPane();
        conteneurGlobal.setPadding(new Insets(20));
        conteneurPiecesGrille = new FlowPane(20, 20);
        conteneurPiecesGrille.setAlignment(Pos.TOP_LEFT);
        
        if (boite.getPieces() == null || boite.getPieces().isEmpty()) {
            Label lblVide = new Label("Aucun inventaire de pièces répertorié pour cette boîte.");
            lblVide.getStyleClass().add("subtitle-label");
            lblVide.setStyle("-fx-font-style: italic;");
            conteneurPiecesGrille.getChildren().add(lblVide);
            conteneurGlobal.setCenter(conteneurPiecesGrille);
            return conteneurGlobal;
        }
        
        ScrollPane scroll = new ScrollPane(conteneurPiecesGrille);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        conteneurGlobal.setCenter(scroll);
        
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));
        
        btnPrecedentPieces = new Button("◄ Précédent");
        btnPrecedentPieces.getStyleClass().add("btn-primary");
        btnPrecedentPieces.setOnAction(e -> {
            if (pageCourantePieces > 1) {
                pageCourantePieces--;
                chargerPagePieces(boite);
            }
        });
        
        lblPaginationPieces = new Label();
        lblPaginationPieces.getStyleClass().add("label");
        lblPaginationPieces.setStyle("-fx-font-weight: bold;");
        
        btnSuivantPieces = new Button("Suivant ►");
        btnSuivantPieces.getStyleClass().add("btn-primary");
        btnSuivantPieces.setOnAction(e -> {
            pageCourantePieces++;
            chargerPagePieces(boite);
        });
        
        footer.getChildren().addAll(btnPrecedentPieces, lblPaginationPieces, btnSuivantPieces);
        conteneurGlobal.setBottom(footer);
        
        chargerPagePieces(boite);
        return conteneurGlobal;
    }

    private void chargerPagePieces(Boite boite) {
        int totalItems = boite.getPieces().size();
        int totalPages = (int) Math.ceil((double) totalItems / taillePagePieces);
        
        if (totalPages == 0) totalPages = 1;
        if (pageCourantePieces > totalPages) pageCourantePieces = totalPages;
        if (pageCourantePieces < 1) pageCourantePieces = 1;
        
        lblPaginationPieces.setText("Page " + pageCourantePieces + " sur " + totalPages);
        btnPrecedentPieces.setDisable(pageCourantePieces <= 1);
        btnSuivantPieces.setDisable(pageCourantePieces >= totalPages);
        
        conteneurPiecesGrille.getChildren().clear();
        int indexDebut = (pageCourantePieces - 1) * taillePagePieces;
        int indexFin = Math.min(indexDebut + taillePagePieces, totalItems);
        
        for (int i = indexDebut; i < indexFin; i++) {
            conteneurPiecesGrille.getChildren().add(creerCartePiece(boite.getPieces().get(i)));
        }
    }

    private VBox creerCartePiece(PieceQuantite pq) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(15));
        carte.getStyleClass().add("card");
        carte.setStyle("-fx-cursor: hand;"); // Indique que c'est cliquable
        carte.setPrefWidth(220);
        carte.setAlignment(Pos.CENTER);
        
        // --- Événement de clic pour ouvrir le popup ---
        carte.setOnMouseClicked(e -> afficherPopupPiece(pq));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        
        String url = pq.getImageP();
        if (url != null && !url.isBlank() && !url.equalsIgnoreCase("null")) {
            try {
                Image image = new Image(url, true);
                imageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Impossible de charger l'image de la pièce : " + url);
            }
        }
        
        VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefHeight(130);
        
        Label lblId = new Label("#" + pq.getPiece().getNumero());
        lblId.getStyleClass().add("subtitle-label");
        lblId.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        
        Label lblNom = new Label(pq.getPiece().getNom());
        lblNom.getStyleClass().add("label");
        lblNom.setStyle("-fx-font-weight: bold;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(40);
        
        String nomCouleur = (pq.getPiece().getCouleur() != null) ? pq.getPiece().getCouleur().getNom() : "Couleur inconnue";
        Label lblCouleur = new Label(nomCouleur);
        lblCouleur.getStyleClass().add("subtitle-label");
        
        Label lblQte = new Label("Quantité incluse : " + pq.getQuantite());
        lblQte.getStyleClass().add("label");
        lblQte.setStyle("-fx-font-weight: bold;");
        
        carte.getChildren().addAll(conteneurImage, lblId, lblNom, lblCouleur, lblQte);
        
        if (pq.isEnSupplement()) {
            Label lblSupp = new Label("(Pièce de supplément)");
            lblSupp.getStyleClass().add("subtitle-label");
            lblSupp.setStyle("-fx-font-style: italic; -fx-text-fill: #e74c3c;");
            carte.getChildren().add(lblSupp);
        }
        return carte;
    }

    private ScrollPane creerContenuFigurines(Boite boite) {
        FlowPane flowFigurines = new FlowPane(20, 20);
        flowFigurines.setPadding(new Insets(20));
        flowFigurines.setAlignment(Pos.TOP_LEFT);
        
        if (boite.getFigurines() == null || boite.getFigurines().isEmpty()) {
            Label lblVide = new Label("Aucune figurine répertoriée pour cette boîte.");
            lblVide.getStyleClass().add("subtitle-label");
            lblVide.setStyle("-fx-font-style: italic;");
            flowFigurines.getChildren().add(lblVide);
        } else {
            for (FigurineQuantite fq : boite.getFigurines()) {
                flowFigurines.getChildren().add(creerCarteFigurine(fq));
            }
        }
        
        ScrollPane scroll = new ScrollPane(flowFigurines);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        return scroll;
    }

    private VBox creerCarteFigurine(FigurineQuantite fq) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(15));
        carte.getStyleClass().add("card");
        carte.setStyle("-fx-cursor: hand;"); // Indique que c'est cliquable
        carte.setPrefWidth(220);
        carte.setAlignment(Pos.CENTER);

        // --- Événement de clic pour ouvrir le popup ---
        carte.setOnMouseClicked(e -> afficherPopupFigurine(fq));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        
        String url = fq.getFigurine().getImageF();
        if (url != null && !url.isBlank() && !url.equalsIgnoreCase("null")) {
            try {
                Image image = new Image(url, true);
                imageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Impossible de charger l'image de la figurine : " + url);
            }
        }
        
        VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefHeight(130);
        
        Label lblId = new Label("#" + fq.getFigurine().getIdFigurine());
        lblId.getStyleClass().add("subtitle-label");
        lblId.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        
        Label lblNom = new Label(fq.getFigurine().getNom());
        lblNom.getStyleClass().add("label");
        lblNom.setStyle("-fx-font-weight: bold;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(40);
        
        Label lblQte = new Label("Quantité incluse : " + fq.getQuantite());
        lblQte.getStyleClass().add("label");
        lblQte.setStyle("-fx-font-weight: bold;");
        
        carte.getChildren().addAll(conteneurImage, lblId, lblNom, lblQte);
        return carte;
    }

    // ====================================================================================
    // NOUVELLES MÉTHODES POUR LES POP-UPS DE DÉTAILS
    // ====================================================================================

    private void afficherPopupPiece(PieceQuantite pq) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Détail de la pièce");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        ImageView imgView = new ImageView();
        imgView.setFitWidth(250);
        imgView.setFitHeight(250);
        imgView.setPreserveRatio(true);
        if (pq.getImageP() != null && !pq.getImageP().isBlank() && !pq.getImageP().equalsIgnoreCase("null")) {
            try {
                imgView.setImage(new Image(pq.getImageP(), true));
            } catch(Exception e) {}
        }
        
        VBox conteneurImage = new VBox(imgView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.getStyleClass().add("card");
        conteneurImage.setPadding(new Insets(10));

        Label lblNom = new Label(pq.getPiece().getNom());
        lblNom.getStyleClass().add("title-label");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(30);
        grille.setAlignment(Pos.CENTER);
        grille.getStyleClass().add("stats-box");
        grille.setPadding(new Insets(20));

        ajouterLigneInfo(grille, 0, "Référence :", pq.getPiece().getNumero());
        String nomCat = (pq.getPiece().getCategorie() != null) ? pq.getPiece().getCategorie().getNom() : "Inconnue";
        ajouterLigneInfo(grille, 1, "Catégorie :", nomCat);
        String nomCoul = (pq.getPiece().getCouleur() != null) ? pq.getPiece().getCouleur().getNom() : "Inconnue";
        ajouterLigneInfo(grille, 2, "Couleur :", nomCoul);
        ajouterLigneInfo(grille, 3, "Quantité :", String.valueOf(pq.getQuantite()));
        ajouterLigneInfo(grille, 4, "Supplément :", pq.isEnSupplement() ? "Oui" : "Non");

        Button btnFermer = new Button("Fermer");
        btnFermer.getStyleClass().add("btn-primary");
        btnFermer.setPrefWidth(150);
        btnFermer.setOnAction(e -> popup.close());

        root.getChildren().addAll(lblNom, conteneurImage, grille, btnFermer);

        Scene scene = new Scene(root, 450, 650);
        ParametreController.appliquerTheme(scene); // Application du thème global
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void afficherPopupFigurine(FigurineQuantite fq) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Détail de la figurine");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        ImageView imgView = new ImageView();
        imgView.setFitWidth(250);
        imgView.setFitHeight(250);
        imgView.setPreserveRatio(true);
        if (fq.getFigurine().getImageF() != null && !fq.getFigurine().getImageF().isBlank() && !fq.getFigurine().getImageF().equalsIgnoreCase("null")) {
            try {
                imgView.setImage(new Image(fq.getFigurine().getImageF(), true));
            } catch(Exception e) {}
        }
        
        VBox conteneurImage = new VBox(imgView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.getStyleClass().add("card");
        conteneurImage.setPadding(new Insets(10));

        Label lblNom = new Label(fq.getFigurine().getNom());
        lblNom.getStyleClass().add("title-label");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(30);
        grille.setAlignment(Pos.CENTER);
        grille.getStyleClass().add("stats-box");
        grille.setPadding(new Insets(20));

        ajouterLigneInfo(grille, 0, "Identifiant :", fq.getFigurine().getIdFigurine());
        String parties = (fq.getFigurine().getNbParties() != null) ? String.valueOf(fq.getFigurine().getNbParties()) : "Inconnu";
        ajouterLigneInfo(grille, 1, "Nombre de parties :", parties);
        ajouterLigneInfo(grille, 2, "Quantité :", String.valueOf(fq.getQuantite()));

        Button btnFermer = new Button("Fermer");
        btnFermer.getStyleClass().add("btn-primary");
        btnFermer.setPrefWidth(150);
        btnFermer.setOnAction(e -> popup.close());

        root.getChildren().addAll(lblNom, conteneurImage, grille, btnFermer);

        Scene scene = new Scene(root, 450, 600);
        ParametreController.appliquerTheme(scene); // Application du thème global
        popup.setScene(scene);
        popup.showAndWait();
    }
    
    // ====================================================================================

    private VBox creerSectionImage(Boite boite) {
        VBox conteneurImage = new VBox();
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPadding(new Insets(10));
        conteneurImage.getStyleClass().add("stats-box");
        conteneurImage.setPrefWidth(350);
        conteneurImage.setMaxHeight(350);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(330);
        imageView.setFitHeight(330);
        imageView.setPreserveRatio(true);
        
        String url = boite.getImageBoite();
        if (url != null && !url.isBlank() && !url.equalsIgnoreCase("null")) {
            try {
                Image image = new Image(url, true);
                imageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Impossible de charger l'image de la boîte : " + url);
            }
        }
        conteneurImage.getChildren().add(imageView);
 
        // --- Bouton "Ajouter à ma collection" ---
        VBox conteneurAction = new VBox(8);
        conteneurAction.setAlignment(Pos.CENTER);
        conteneurAction.setPadding(new Insets(15, 0, 0, 0));
 
        btnAjouterCollection = new Button("Ajouter à ma collection");
        btnAjouterCollection.getStyleClass().add("btn-primary");
        btnAjouterCollection.setMaxWidth(Double.MAX_VALUE);
 
        lblMessageCollection = new Label();
        lblMessageCollection.setStyle("-fx-font-size: 11px;");
        lblMessageCollection.setWrapText(true);
 
        if (collectionService == null) {
            btnAjouterCollection.setDisable(true);
        } else {
            mettreAJourBoutonCollection(boite);
            btnAjouterCollection.setOnAction(e -> ajouterALaCollection(boite));
        }
 
        conteneurAction.getChildren().addAll(btnAjouterCollection, lblMessageCollection);
        conteneurImage.getChildren().add(conteneurAction);
        
        return conteneurImage;
    }
 
    private void ajouterALaCollection(Boite boite) {
        try {
            collectionService.ajouterBoite(boite, EtatBoite.COMPLETE);
            lblMessageCollection.setText("Boîte ajoutée à votre collection !");
            lblMessageCollection.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            mettreAJourBoutonCollection(boite);
        } catch (Exception ex) {
            lblMessageCollection.setText("Erreur lors de l'ajout : " + ex.getMessage());
            lblMessageCollection.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");
        }
    }
 
    private void mettreAJourBoutonCollection(Boite boite) {
        CollectionItem item = collectionService.obtenerItem(boite.getNumero());
        if (item != null) {
            btnAjouterCollection.setText("Déjà dans ma collection");
            btnAjouterCollection.setDisable(true);
        } else {
            btnAjouterCollection.setText("Ajouter à ma collection");
            btnAjouterCollection.setDisable(false);
        }
    }
 
    private GridPane creerSectionInformations(Boite boite) {
        GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(50);
        grille.setPadding(new Insets(20));
        grille.getStyleClass().add("stats-box");
        
        String nomTheme = (boite.getTheme() != null) ? boite.getTheme().getNom() : "Inconnu";
        String annee = (boite.getAnnee() != null) ? String.valueOf(boite.getAnnee()) : "Non renseignée";
        String pieces = (boite.getNbPieces() != null) ? String.valueOf(boite.getNbPieces()) : "Inconnu";
        
        ajouterLigneInfo(grille, 0, "Numéro de référence :", boite.getNumero());
        ajouterLigneInfo(grille, 1, "Thème :", nomTheme);
        ajouterLigneInfo(grille, 2, "Année de sortie :", annee);
        ajouterLigneInfo(grille, 3, "Pièces annoncées :", pieces);
        
        return grille;
    }

    private VBox creerSectionStatistiques(Boite boite) {
        VBox sectionComplete = new VBox(15);
        sectionComplete.setPadding(new Insets(20));
        sectionComplete.getStyleClass().add("stats-box");
        VBox.setVgrow(sectionComplete, Priority.ALWAYS);
        
        Label lblTitreStats = new Label("Statistiques du contenu réel");
        lblTitreStats.getStyleClass().add("title-label");
        Separator separator = new Separator();
        
        BoiteStats stats = boiteService.calculerStatsBoite(boite.getNumero());
        if (stats == null || stats.getTotalPieces() == 0) {
            Label lblVide = new Label("Aucun inventaire détaillé disponible pour cette boîte en base de données.");
            lblVide.getStyleClass().add("subtitle-label");
            lblVide.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            sectionComplete.getChildren().addAll(lblTitreStats, separator, lblVide);
            return sectionComplete;
        }
        
        HBox conteneurDivise = new HBox(30);
        conteneurDivise.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(conteneurDivise, Priority.ALWAYS);
        
        VBox colonneGauche = new VBox(20);
        colonneGauche.setPrefWidth(450);
        colonneGauche.setMaxWidth(450);
        
        GridPane grilleStats = new GridPane();
        grilleStats.setVgap(15);
        grilleStats.setHgap(30);
        
        ajouterLigneInfo(grilleStats, 0, "Total de pièces répertoriées :", String.valueOf(stats.getTotalPieces()));
        ajouterLigneInfo(grilleStats, 1, "Dont pièces en supplément :", String.valueOf(stats.getTotalSupplement()));
        ajouterLigneInfo(grilleStats, 2, "Nombre de figurines :", String.valueOf(stats.getTotalFigurines()));
        ajouterLigneInfo(grilleStats, 3, "Sous-boîtes incluses :", String.valueOf(stats.getTotalSousBoites()));
        
        Label lblCouleurs = new Label("Répartition par couleurs :");
        lblCouleurs.getStyleClass().add("subtitle-label");
        lblCouleurs.setStyle("-fx-font-weight: bold;");
        
        FlowPane flowCouleurs = new FlowPane(10, 10);
        flowCouleurs.setPrefWrapLength(420);
        
        PieChart graphiqueCouleurs = new PieChart();
        graphiqueCouleurs.setPrefSize(600, 600);
        graphiqueCouleurs.setLegendVisible(false);
        
        for (Map.Entry<Couleur, Integer> entree : stats.getRepartitionCouleurs().entrySet()) {
            Couleur couleur = entree.getKey();
            int quantite = entree.getValue();
            String rgbHex = formaterRgb(couleur.getRgb());
            String textCouleur = obtenirCouleurTexte(rgbHex);
            String borderColor = rgbHex.equalsIgnoreCase("#FFFFFF") ? "#bdc3c7" : rgbHex;
            
            Label tagCouleur = new Label(couleur.getNom() + " (" + quantite + ")");
            tagCouleur.setStyle("-fx-background-color: " + rgbHex + "; -fx-text-fill: " + textCouleur + "; -fx-border-color: " + borderColor + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
            flowCouleurs.getChildren().add(tagCouleur);
            
            PieChart.Data tranche = new PieChart.Data(couleur.getNom() + " (" + quantite + ")", quantite);
            graphiqueCouleurs.getData().add(tranche);
            
            if (tranche.getNode() != null) {
                tranche.getNode().setStyle("-fx-pie-color: " + rgbHex + "; -fx-border-color: transparent;");
            }
            tranche.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: " + rgbHex + "; -fx-border-color: transparent;");
                }
            });
        }
        
        ScrollPane scrollCouleurs = new ScrollPane(flowCouleurs);
        scrollCouleurs.setFitToWidth(true);
        scrollCouleurs.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollCouleurs, Priority.ALWAYS);
        
        colonneGauche.getChildren().addAll(grilleStats, lblCouleurs, scrollCouleurs);
        HBox.setHgrow(graphiqueCouleurs, Priority.ALWAYS);
        
        conteneurDivise.getChildren().addAll(colonneGauche, graphiqueCouleurs);
        sectionComplete.getChildren().addAll(lblTitreStats, separator, conteneurDivise);
        
        return sectionComplete;
    }

    private void ajouterLigneInfo(GridPane grille, int ligne, String libelle, String valeur) {
        Label lblLibelle = new Label(libelle);
        lblLibelle.getStyleClass().add("subtitle-label");
        Label lblValeur = new Label(valeur);
        lblValeur.getStyleClass().add("label");
        lblValeur.setStyle("-fx-font-weight: bold;");
        grille.add(lblLibelle, 0, ligne);
        grille.add(lblValeur, 1, ligne);
    }
}