package UI.vue;

import App.Boite;
import App.BoiteService;
import App.BoiteStats;
import App.Couleur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

        setTop(creerEnTete(actionRetour, boite.getNom()));
        setCenter(creerContenu(boite));
    }

    /**
     * Crée l'en-tête contenant le bouton de retour et le titre.
     *
     * @param actionRetour l'action du bouton retour
     * @param titre le nom de la boîte
     * @return un HBox configuré
     */
    private HBox creerEnTete(Runnable actionRetour, String titre) {
        HBox entete = new HBox(20);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(0, 0, 30, 0));

        Button btnRetour = new Button("◄ Retour au catalogue");
        btnRetour.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> actionRetour.run());

        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

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
        HBox contenu = new HBox(50);
        contenu.setAlignment(Pos.TOP_LEFT);

        VBox zoneInformations = new VBox(30);
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
        conteneurImage.setPadding(new Insets(20));
        conteneurImage.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        conteneurImage.setPrefSize(400, 400);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(350);
        imageView.setFitHeight(350);
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
        grille.setHgap(30);

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
        
        Label lblTitreStats = new Label("Statistiques du contenu réel");
        lblTitreStats.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 5 0;");
        
        BoiteStats stats = boiteService.calculerStatsBoite(boite.getNumero());
        
        if (stats == null || stats.getTotalPieces() == 0) {
            Label lblVide = new Label("Aucun inventaire détaillé disponible pour cette boîte en base de données.");
            lblVide.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            sectionComplete.getChildren().addAll(lblTitreStats, lblVide);
            return sectionComplete;
        }

        HBox conteneurDivise = new HBox(40);
        conteneurDivise.setAlignment(Pos.TOP_LEFT);

        VBox colonneGauche = new VBox(15);

        GridPane grilleStats = new GridPane();
        grilleStats.setVgap(10);
        grilleStats.setHgap(30);
        
        ajouterLigneInfo(grilleStats, 0, "Total de pièces répertoriées :", String.valueOf(stats.getTotalPieces()));
        ajouterLigneInfo(grilleStats, 1, "Dont pièces en supplément :", String.valueOf(stats.getTotalSupplement()));
        ajouterLigneInfo(grilleStats, 2, "Nombre de figurines :", String.valueOf(stats.getTotalFigurines()));
        
        if (stats.getTotalSousBoites() > 0) {
            ajouterLigneInfo(grilleStats, 3, "Sous-boîtes incluses :", String.valueOf(stats.getTotalSousBoites()));
        }

        Label lblCouleurs = new Label("Répartition par couleurs :");
        lblCouleurs.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");

        FlowPane flowCouleurs = new FlowPane(10, 10);
        flowCouleurs.setPrefWrapLength(350);

        for (Map.Entry<Couleur, Integer> entree : stats.getRepartitionCouleurs().entrySet()) {
            Couleur couleur = entree.getKey();
            int quantite = entree.getValue();
            
            Label tagCouleur = new Label(couleur.getNom() + " (" + quantite + ")");
            tagCouleur.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 12px;");
            flowCouleurs.getChildren().add(tagCouleur);
        }

        ScrollPane scrollCouleurs = new ScrollPane(flowCouleurs);
        scrollCouleurs.setFitToWidth(true);
        scrollCouleurs.setPrefSize(350, 150);
        scrollCouleurs.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        colonneGauche.getChildren().addAll(grilleStats, lblCouleurs, scrollCouleurs);

        PieChart graphiqueCouleurs = new PieChart();
        graphiqueCouleurs.setPrefSize(350, 350);
        graphiqueCouleurs.setLegendVisible(false);

        for (Map.Entry<Couleur, Integer> entree : stats.getRepartitionCouleurs().entrySet()) {
            Couleur couleur = entree.getKey();
            int quantite = entree.getValue();
            
            PieChart.Data tranche = new PieChart.Data(couleur.getNom(), quantite);
            graphiqueCouleurs.getData().add(tranche);
        }

        conteneurDivise.getChildren().addAll(colonneGauche, graphiqueCouleurs);
        HBox.setHgrow(colonneGauche, Priority.ALWAYS);

        sectionComplete.getChildren().addAll(lblTitreStats, conteneurDivise);
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
        lblLibelle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");

        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        grille.add(lblLibelle, 0, ligne);
        grille.add(lblValeur, 1, ligne);
    }
}