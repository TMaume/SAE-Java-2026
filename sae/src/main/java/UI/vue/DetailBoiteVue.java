package UI.vue;

import App.Boite;
import App.BoiteService;
import App.BoiteStats;
import App.Couleur;
import App.FigurineQuantite;
import App.PieceQuantite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.Map;

/**
 * Vue affichant les détails complets, les statistiques, les pièces et les figurines d'une boîte LEGO.
 */
public class DetailBoiteVue extends BorderPane {

    private final BoiteService boiteService;
    private final Boite boiteComplete;
    
    private int pageCourantePieces = 1;
    private final int taillePagePieces = 20;
    private FlowPane conteneurPiecesGrille;
    private Label lblPaginationPieces;
    private Button btnPrecedentPieces;
    private Button btnSuivantPieces;

    /**
     * Construit la vue détaillée d'une boîte.
     *
     * @param boiteLegere la boîte allégée provenant du catalogue
     * @param boiteService le service permettant de récupérer les statistiques et l'inventaire
     * @param actionRetour l'action déclenchée pour revenir au catalogue
     */
    public DetailBoiteVue(Boite boiteLegere, BoiteService boiteService, Runnable actionRetour) {
        this.boiteService = boiteService;
        
        Boite boiteChargee = boiteService.chargerBoiteComplete(boiteLegere.getNumero());
        this.boiteComplete = (boiteChargee != null) ? boiteChargee : boiteLegere;
        
        setPadding(new Insets(30));
        setStyle("-fx-background-color: transparent;");

        setTop(creerEnTete(actionRetour, this.boiteComplete));
        setCenter(creerOnglets(this.boiteComplete));
    }

    /**
     * Formate le code RGB pour garantir l'utilisation dans JavaFX.
     *
     * @param rgb le code RGB brut
     * @return le code RGB formaté avec le préfixe #
     */
    private String formaterRgb(String rgb) {
        if (rgb == null || rgb.isBlank()) {
            return "#ecf0f1";
        }
        if (!rgb.startsWith("#")) {
            return "#" + rgb;
        }
        return rgb;
    }

    /**
     * Calcule la couleur de texte idéale (noir ou blanc) pour contraster avec le fond.
     *
     * @param rgbHex la couleur de fond en format hexadécimal
     * @return la couleur du texte (black ou white)
     */
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

    /**
     * Crée l'en-tête contenant le bouton de retour et le titre.
     *
     * @param actionRetour l'action du bouton retour
     * @param boite la boîte à afficher
     * @return un HBox configuré
     */
    private HBox creerEnTete(Runnable actionRetour, Boite boite) {
        HBox entete = new HBox(20);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(0, 0, 30, 0));

        Button btnRetour = new Button("◄ Retour au catalogue");
        btnRetour.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> actionRetour.run());

        String nomTheme = (boite.getTheme() != null) ? boite.getTheme().getNom() : "Inconnu";
        Label lblTitre = new Label("LEGO " + nomTheme + " " + boite.getNumero() + " - " + boite.getNom());
        lblTitre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        entete.getChildren().addAll(btnRetour, lblTitre);
        return entete;
    }

    /**
     * Crée le système d'onglets pour séparer les statistiques, les pièces et les figurines.
     *
     * @param boite la boîte à afficher
     * @return le composant TabPane contenant les vues
     */
    private TabPane creerOnglets(Boite boite) {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

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

    /**
     * Crée la zone contenant les informations générales et les statistiques.
     *
     * @param boite la boîte à afficher
     * @return un HBox structurant l'affichage
     */
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

    /**
     * Crée le conteneur paginé affichant la liste des pièces.
     *
     * @param boite la boîte complète contenant les pièces
     * @return un BorderPane contenant la grille et la pagination
     */
    private BorderPane creerContenuPieces(Boite boite) {
        BorderPane conteneurGlobal = new BorderPane();
        conteneurGlobal.setPadding(new Insets(20));

        conteneurPiecesGrille = new FlowPane(20, 20);
        conteneurPiecesGrille.setAlignment(Pos.TOP_LEFT);

        if (boite.getPieces() == null || boite.getPieces().isEmpty()) {
            Label lblVide = new Label("Aucun inventaire de pièces répertorié pour cette boîte.");
            lblVide.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            conteneurPiecesGrille.getChildren().add(lblVide);
            conteneurGlobal.setCenter(conteneurPiecesGrille);
            return conteneurGlobal;
        }

        ScrollPane scroll = new ScrollPane(conteneurPiecesGrille);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        conteneurGlobal.setCenter(scroll);

        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));

        btnPrecedentPieces = new Button("◄ Précédent");
        btnPrecedentPieces.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnPrecedentPieces.setOnAction(e -> {
            if (pageCourantePieces > 1) {
                pageCourantePieces--;
                chargerPagePieces(boite);
            }
        });

        lblPaginationPieces = new Label();
        lblPaginationPieces.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        btnSuivantPieces = new Button("Suivant ►");
        btnSuivantPieces.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnSuivantPieces.setOnAction(e -> {
            pageCourantePieces++;
            chargerPagePieces(boite);
        });

        footer.getChildren().addAll(btnPrecedentPieces, lblPaginationPieces, btnSuivantPieces);
        conteneurGlobal.setBottom(footer);

        chargerPagePieces(boite);

        return conteneurGlobal;
    }

    /**
     * Charge et affiche la page actuelle des pièces.
     *
     * @param boite la boîte contenant la liste globale des pièces
     */
    private void chargerPagePieces(Boite boite) {
        int totalItems = boite.getPieces().size();
        int totalPages = (int) Math.ceil((double) totalItems / taillePagePieces);
        
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (pageCourantePieces > totalPages) {
            pageCourantePieces = totalPages;
        }
        if (pageCourantePieces < 1) {
            pageCourantePieces = 1;
        }

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

    /**
     * Crée une carte graphique pour une pièce individuelle.
     *
     * @param pq l'objet contenant la pièce et sa quantité
     * @return un VBox stylisé
     */
    private VBox creerCartePiece(PieceQuantite pq) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(15));
        carte.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        carte.setPrefWidth(220);
        carte.setAlignment(Pos.CENTER);

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
        lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");

        Label lblNom = new Label(pq.getPiece().getNom());
        lblNom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(40);

        String nomCouleur = (pq.getPiece().getCouleur() != null) ? pq.getPiece().getCouleur().getNom() : "Couleur inconnue";
        Label lblCouleur = new Label(nomCouleur);
        lblCouleur.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label lblQte = new Label("Quantité incluse : " + pq.getQuantite());
        lblQte.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        carte.getChildren().addAll(conteneurImage, lblId, lblNom, lblCouleur, lblQte);

        if (pq.isEnSupplement()) {
            Label lblSupp = new Label("(Pièce de supplément)");
            lblSupp.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: #e74c3c;");
            carte.getChildren().add(lblSupp);
        }

        return carte;
    }

    /**
     * Crée le conteneur affichant la liste des figurines sous forme de cartes.
     *
     * @param boite la boîte complète contenant les figurines
     * @return un ScrollPane contenant les cartes
     */
    private ScrollPane creerContenuFigurines(Boite boite) {
        FlowPane flowFigurines = new FlowPane(20, 20);
        flowFigurines.setPadding(new Insets(20));
        flowFigurines.setAlignment(Pos.TOP_LEFT);

        if (boite.getFigurines() == null || boite.getFigurines().isEmpty()) {
            Label lblVide = new Label("Aucune figurine répertoriée pour cette boîte.");
            lblVide.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            flowFigurines.getChildren().add(lblVide);
        } else {
            for (FigurineQuantite fq : boite.getFigurines()) {
                flowFigurines.getChildren().add(creerCarteFigurine(fq));
            }
        }

        ScrollPane scroll = new ScrollPane(flowFigurines);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    /**
     * Crée une carte graphique pour une figurine individuelle.
     *
     * @param fq l'objet contenant la figurine et sa quantité
     * @return un VBox stylisé
     */
    private VBox creerCarteFigurine(FigurineQuantite fq) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(15));
        carte.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        carte.setPrefWidth(220);
        carte.setAlignment(Pos.CENTER);

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
                System.err.println("Impossible de charger l'image de la figurine (URL invalide) : " + url);
            }
        }

        VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefHeight(130);

        Label lblId = new Label("#" + fq.getFigurine().getIdFigurine());
        lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");

        Label lblNom = new Label(fq.getFigurine().getNom());
        lblNom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(40);

        Label lblQte = new Label("Quantité incluse : " + fq.getQuantite());
        lblQte.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        carte.getChildren().addAll(conteneurImage, lblId, lblNom, lblQte);
        return carte;
    }

    /**
     * Crée le conteneur affichant l'image principale de la boîte.
     *
     * @param boite la boîte contenant l'URL de l'image
     * @return un VBox stylisé contenant l'image
     */
    private VBox creerSectionImage(Boite boite) {
        VBox conteneurImage = new VBox();
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPadding(new Insets(10));
        conteneurImage.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");
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
                System.err.println("Impossible de charger l'image de la boîte (URL invalide) : " + url);
            }
        }

        conteneurImage.getChildren().add(imageView);
        return conteneurImage;
    }

    /**
     * Crée la grille détaillant les métadonnées de base de la boîte.
     *
     * @param boite la boîte contenant les informations
     * @return un GridPane contenant les labels
     */
    private GridPane creerSectionInformations(Boite boite) {
        GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(50);
        grille.setPadding(new Insets(20));
        grille.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");

        String nomTheme = (boite.getTheme() != null) ? boite.getTheme().getNom() : "Inconnu";
        String annee = (boite.getAnnee() != null) ? String.valueOf(boite.getAnnee()) : "Non renseignée";
        String pieces = (boite.getNbPieces() != null) ? String.valueOf(boite.getNbPieces()) : "Inconnu";

        ajouterLigneInfo(grille, 0, "Numéro de référence :", boite.getNumero());
        ajouterLigneInfo(grille, 1, "Thème :", nomTheme);
        ajouterLigneInfo(grille, 2, "Année de sortie :", annee);
        ajouterLigneInfo(grille, 3, "Pièces annoncées :", pieces);

        return grille;
    }

    /**
     * Crée la section affichant les statistiques réelles du contenu de la boîte.
     *
     * @param boite la boîte à analyser
     * @return un VBox contenant les statistiques calculées
     */
    private VBox creerSectionStatistiques(Boite boite) {
        VBox sectionComplete = new VBox(15);
        sectionComplete.setPadding(new Insets(20));
        sectionComplete.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");
        VBox.setVgrow(sectionComplete, Priority.ALWAYS);
        
        Label lblTitreStats = new Label("Statistiques du contenu réel");
        lblTitreStats.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Separator separator = new Separator();

        BoiteStats stats = boiteService.calculerStatsBoite(boite.getNumero());
        
        if (stats == null || stats.getTotalPieces() == 0) {
            Label lblVide = new Label("Aucun inventaire détaillé disponible pour cette boîte en base de données.");
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
        lblCouleurs.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

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
                tranche.getNode().setStyle("-fx-pie-color: " + rgbHex + "; -fx-border-color: white; -fx-border-width: 1px;");
            }
            
            tranche.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: " + rgbHex + "; -fx-border-color: white; -fx-border-width: 1px;");
                }
            });
        }

        ScrollPane scrollCouleurs = new ScrollPane(flowCouleurs);
        scrollCouleurs.setFitToWidth(true);
        scrollCouleurs.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollCouleurs, Priority.ALWAYS);

        colonneGauche.getChildren().addAll(grilleStats, lblCouleurs, scrollCouleurs);

        HBox.setHgrow(graphiqueCouleurs, Priority.ALWAYS);
        conteneurDivise.getChildren().addAll(colonneGauche, graphiqueCouleurs);

        sectionComplete.getChildren().addAll(lblTitreStats, separator, conteneurDivise);
        return sectionComplete;
    }

    /**
     * Ajoute une ligne de données dans un GridPane.
     *
     * @param grille le conteneur cible
     * @param ligne l'index de la ligne
     * @param libelle le nom de la caractéristique
     * @param valeur la valeur de la caractéristique
     */
    private void ajouterLigneInfo(GridPane grille, int ligne, String libelle, String valeur) {
        Label lblLibelle = new Label(libelle);
        lblLibelle.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        grille.add(lblLibelle, 0, ligne);
        grille.add(lblValeur, 1, ligne);
    }
}