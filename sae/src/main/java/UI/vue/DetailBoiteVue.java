package UI.vue;

import App.Boite;
import App.BoiteService;
import App.BoiteStats;
import App.Couleur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.Map;

/**
 * Vue affichant les détails complets et les statistiques d'une boîte LEGO.
 */
public class DetailBoiteVue extends BorderPane {

    private final BoiteService boiteService;

    /**
     * Construit la vue détaillée d'une boîte.
     *
     * @param boite la boîte à afficher
     * @param boiteService le service permettant de récupérer les statistiques
     * @param actionRetour l'action déclenchée pour revenir au catalogue
     */
    public DetailBoiteVue(Boite boite, BoiteService boiteService, Runnable actionRetour) {
        this.boiteService = boiteService;
        
        setPadding(new Insets(30));
        setStyle("-fx-background-color: transparent;");

        setTop(creerEnTete(actionRetour, boite));
        setCenter(creerContenu(boite));
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
     * Crée la zone centrale contenant l'image, les informations et les statistiques.
     *
     * @param boite la boîte à afficher
     * @return un HBox structurant l'affichage
     */
    private HBox creerContenu(Boite boite) {
        HBox contenu = new HBox(30);
        contenu.setAlignment(Pos.TOP_LEFT);

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
     * Crée le conteneur affichant l'image de la boîte.
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

        ImageView imageView = new ImageView();
        imageView.setFitWidth(330);
        imageView.setFitHeight(330);
        imageView.setPreserveRatio(true);

        String url = boite.getImageBoite();
        if (url != null && !url.isBlank()) {
            Image image = new Image(url, true);
            imageView.setImage(image);
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

        VBox colonneGauche = new VBox(20);

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
        flowCouleurs.setPrefWrapLength(350);

        PieChart graphiqueCouleurs = new PieChart();
        graphiqueCouleurs.setPrefSize(400, 400);
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

            tranche.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: " + rgbHex + ";");
                }
            });
        }

        ScrollPane scrollCouleurs = new ScrollPane(flowCouleurs);
        scrollCouleurs.setFitToWidth(true);
        scrollCouleurs.setPrefHeight(200);
        scrollCouleurs.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        colonneGauche.getChildren().addAll(grilleStats, lblCouleurs, scrollCouleurs);
        HBox.setHgrow(colonneGauche, Priority.ALWAYS);

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