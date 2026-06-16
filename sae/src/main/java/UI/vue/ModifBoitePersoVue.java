package UI.vue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import App.Boite;
import App.BoiteQuantite;
import App.BoiteService;
import App.BoiteStats;
import App.Couleur;
import App.Figurine;
import App.FigurineQuantite;
import App.Piece;
import App.PieceQuantite;
import App.PieceService;
import App.Theme;
import App.ThemeService;

public class ModifBoitePersoVue extends BorderPane {

    private final BoiteService boiteService;
    private final ThemeService themeService;
    private final PieceService pieceService;
    private Boite boite;

    private TextField txtNom;
    private TextField txtAnnee;
    private TextField txtRechercheTheme;
    private ComboBox<Theme> comboTheme;
    
    private ListView<String> listeComposition;
    private ComboBox<String> comboFiltreContenu;
    
    private TextField txtRechercheAjout;
    private ComboBox<String> comboTypeAjout;
    private ListView<String> listeResultatsAjout;
    private TextField txtQuantiteAjout;

    private ObservableList<Theme> tousLesThemes;
    
    
    private VBox sectionStatistiques;

    public ModifBoitePersoVue(Boite boite, BoiteService boiteService, ThemeService themeService, PieceService pieceService, Runnable actionRetour) {
        this.boiteService = boiteService;
        this.themeService = themeService;
        this.pieceService = pieceService;
        
        Boite boiteComplete = boiteService.chargerBoiteComplete(boite.getNumero());
        this.boite = (boiteComplete != null) ? boiteComplete : boite;

        tousLesThemes = FXCollections.observableArrayList();
        if (themeService != null) {
            tousLesThemes.addAll(themeService.listerThemes());
        }

        setPadding(new Insets(30));
        setStyle("-fx-background-color: transparent;");

        setTop(creerEnTete(actionRetour, "Modification de la boîte personnalisée"));
        setCenter(creerContenu());

        configurerEcouteurs();
        appliquerFiltreContenu();
    }

    private void configurerEcouteurs() {

        // Le listener reagit en direct a chaque lettre tapee dans la barre de recherche
        txtRechercheTheme.textProperty().addListener((observable, oldValue, newValue) -> {
            appliquerFiltreTheme();
        });

        // Met a jour l affichage de l inventaire quand on change de categorie dans le menu deroulant
        comboFiltreContenu.setOnAction(event -> {
            appliquerFiltreContenu();
        });
        
        txtRechercheAjout.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherElementsAjout(newValue, comboTypeAjout.getValue());
        });
        
        comboTypeAjout.setOnAction(event -> {
            rechercherElementsAjout(txtRechercheAjout.getText(), comboTypeAjout.getValue());
        });
    }

    private void appliquerFiltreTheme() {

        String texteRecherche = txtRechercheTheme.getText().trim().toLowerCase(); // trim qui sert a nettoyer le txt de tout espace inutile
        
        if (texteRecherche.isEmpty()) {
            comboTheme.setItems(tousLesThemes);
        } else {
            ObservableList<Theme> themesFiltres = FXCollections.observableArrayList();
            for (Theme t : tousLesThemes) {
                if (t.getNom().toLowerCase().contains(texteRecherche)) {
                    themesFiltres.add(t);
                }
            }
            comboTheme.setItems(themesFiltres);
            if (!themesFiltres.isEmpty()) {
                comboTheme.show();
            }
        }
    }

    private void appliquerFiltreContenu() {

        String filtreSelectionne = comboFiltreContenu.getValue();
        listeComposition.getItems().clear();

        boolean afficherPieces = filtreSelectionne.equals("Tout afficher") || filtreSelectionne.equals("Pièces");
        boolean afficherFigurines = filtreSelectionne.equals("Tout afficher") || filtreSelectionne.equals("Figurines");
        boolean afficherSousBoites = filtreSelectionne.equals("Tout afficher") || filtreSelectionne.equals("Sous-boîtes");

        if (afficherPieces && boite.getPieces() != null) {
            for (PieceQuantite pq : boite.getPieces()) {
                listeComposition.getItems().add("[Pièce] Réf: " + pq.getPiece().getNumero() + " | Quantité: " + pq.getQuantite());
            }
        }
        if (afficherFigurines && boite.getFigurines() != null) {
            for (FigurineQuantite fq : boite.getFigurines()) {
                listeComposition.getItems().add("[Figurine] Réf: " + fq.getFigurine().getIdFigurine() + " | Quantité: " + fq.getQuantite());
            }
        }
        if (afficherSousBoites && boite.getBoitesIncluses() != null) {
            for (BoiteQuantite bq : boite.getBoitesIncluses()) {
                listeComposition.getItems().add("[Sous-boîte] N°: " + bq.getBoite().getNumero() + " | Quantité: " + bq.getQuantite());
            }
        }
    }

    // Lance la recherche dans la base de donnees avec les services selon le type d element choisi
    private void rechercherElementsAjout(String recherche, String type) {
        listeResultatsAjout.getItems().clear();
        
        if (recherche == null || recherche.trim().length() < 2) {
            return;
        }
        
        if (type.equals("Pièce") && pieceService != null) {

            // Remplacez par la methode de recherche de pieces
            listeResultatsAjout.getItems().add("Simulation: Pièce trouvée (Réf: " + recherche + ")");
        } else if (type.equals("Figurine")) {
            listeResultatsAjout.getItems().add("Simulation: Figurine trouvée (Réf: " + recherche + ")");
        } else if (type.equals("Sous-boîte")) {
            listeResultatsAjout.getItems().add("Simulation: Boîte trouvée (Réf: " + recherche + ")");
        }
    }

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

    private HBox creerContenu() {
        HBox contenu = new HBox(30);
        contenu.setAlignment(Pos.TOP_LEFT);

        VBox zoneInformations = new VBox(20);
        
        // Initialisation de la boite qui va contenir les stats
        sectionStatistiques = new VBox();
        sectionStatistiques.getChildren().add(creerSectionStatistiques(this.boite));
        
        zoneInformations.getChildren().addAll(
            creerZoneOnglets(),
            sectionStatistiques
        );
        HBox.setHgrow(zoneInformations, Priority.ALWAYS);

        contenu.getChildren().addAll(
            creerSectionImage(this.boite),
            zoneInformations
        );

        return contenu;
    }

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

    private VBox creerZoneOnglets() {
        VBox conteneurGeneral = new VBox(15);
        conteneurGeneral.setPadding(new Insets(20));
        conteneurGeneral.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");

        TabPane tabPaneEdition = new TabPane();
        tabPaneEdition.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 4;");
        tabPaneEdition.setPrefHeight(350);

        Tab tabInfos = new Tab("Informations textuelles");
        tabInfos.setClosable(false);
        tabInfos.setContent(creerOngletInformations());
        
        Tab tabAjout = new Tab("Ajouter du contenu");
        tabAjout.setClosable(false);
        tabAjout.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        tabAjout.setContent(creerOngletAjout());

        Tab tabContenu = new Tab("Contenu de la boîte");
        tabContenu.setClosable(false);
        tabContenu.setContent(creerOngletContenu());

        tabPaneEdition.getTabs().addAll(tabInfos, tabAjout, tabContenu);

        Button btnSauvegarder = new Button("Enregistrer les informations textuelles");
        btnSauvegarder.setMaxWidth(Double.MAX_VALUE);
        btnSauvegarder.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-cursor: hand;");
        btnSauvegarder.setOnAction(e -> gererActionSauvegarde());

        conteneurGeneral.getChildren().addAll(tabPaneEdition, btnSauvegarder);
        return conteneurGeneral;
    }

    private VBox creerOngletInformations() {
        VBox conteneur = new VBox(20);
        conteneur.setPadding(new Insets(20));

        GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(40);

        txtNom = new TextField(boite.getNom() != null ? boite.getNom() : "");
        
        int anneeActuelle = LocalDate.now().getYear();
        String valeurAnnee = boite.getAnnee() != null ? String.valueOf(boite.getAnnee()) : String.valueOf(anneeActuelle);
        txtAnnee = new TextField(valeurAnnee);

        ajouterLigneInfo(grille, 0, "Numéro de référence :", boite.getNumero());
        ajouterLigneEdition(grille, 1, "Nom de la boîte :", txtNom);
        ajouterLigneEdition(grille, 2, "Année de production :", txtAnnee);

        VBox zoneTheme = new VBox(10);
        zoneTheme.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #e9ecef; -fx-border-radius: 5;");
        
        Label lblTheme = new Label("Rechercher et associer un thème :");
        lblTheme.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        txtRechercheTheme = new TextField();
        txtRechercheTheme.setPromptText("Ex: Star Wars");
        txtRechercheTheme.setPrefWidth(250);
        
        comboTheme = new ComboBox<>();
        configurerControleTheme();

        HBox ligneTheme = new HBox(15);
        ligneTheme.setAlignment(Pos.CENTER_LEFT);
        ligneTheme.getChildren().addAll(txtRechercheTheme, comboTheme);

        zoneTheme.getChildren().addAll(lblTheme, ligneTheme);

        conteneur.getChildren().addAll(grille, zoneTheme);
        return conteneur;
    }
    
    private VBox creerOngletAjout() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));
        
        Label lblTitre = new Label("Rechercher un élément à ajouter à cette boîte personnalisée");
        lblTitre.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox barreRecherche = new HBox(10);
        barreRecherche.setAlignment(Pos.CENTER_LEFT);
        
        comboTypeAjout = new ComboBox<>();
        comboTypeAjout.getItems().addAll("Pièce", "Figurine", "Sous-boîte");
        comboTypeAjout.setValue("Pièce");
        
        txtRechercheAjout = new TextField();
        txtRechercheAjout.setPromptText("Mot-clé ou référence");
        txtRechercheAjout.setPrefWidth(200);
        
        barreRecherche.getChildren().addAll(comboTypeAjout, txtRechercheAjout);
        
        listeResultatsAjout = new ListView<>();
        listeResultatsAjout.setPrefHeight(120);
        
        HBox zoneAction = new HBox(15);
        zoneAction.setAlignment(Pos.CENTER_LEFT);
        
        Label lblQuantite = new Label("Quantité :");
        txtQuantiteAjout = new TextField("1");
        txtQuantiteAjout.setPrefWidth(60);
        
        Button btnAjouter = new Button("➕ Ajouter à l'inventaire");
        btnAjouter.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        btnAjouter.setOnAction(e -> gererAjoutElement());
        
        zoneAction.getChildren().addAll(lblQuantite, txtQuantiteAjout, btnAjouter);
        
        conteneur.getChildren().addAll(lblTitre, barreRecherche, listeResultatsAjout, zoneAction);
        return conteneur;
    }

    private void gererAjoutElement() {
        String selection = listeResultatsAjout.getSelectionModel().getSelectedItem();
        String type = comboTypeAjout.getValue();
        
        if (selection == null) {
            afficherAlerte(AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un élément dans la liste");
            return;
        }
        
        int quantite;
        try {
            quantite = Integer.parseInt(txtQuantiteAjout.getText().trim()); 
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            afficherAlerte(AlertType.WARNING, "Quantité invalide", "Veuillez saisir un nombre entier positif");
            return;
        }
        
        // Extraction du code et sauvegarde en base puis en local pour actualiser la vue
        if (type.equals("Pièce")) {
            Piece piece = new Piece("REF-SIMULEE", "Pièce simulée", null, null); 
            PieceQuantite pq = new PieceQuantite(piece, quantite, false, null);
            boite.ajouterPiece(pq);
            boiteService.ajouterPieceABoite(boite.getNumero(), pq);
            afficherAlerte(AlertType.INFORMATION, "Succès", "Pièce ajoutée avec succès");
        } else if (type.equals("Figurine")) {
            Figurine fig = new Figurine("FIG-SIMULEE", "Figurine simulée", 0, null);
            FigurineQuantite fq = new FigurineQuantite(fig, quantite);
            boite.ajouterFigurine(fq);
            afficherAlerte(AlertType.INFORMATION, "Succès", "Figurine ajoutée avec succès");
        } else if (type.equals("Sous-boîte")) {
            Boite b = new Boite("BOX-SIMULEE", "Boîte simulée", 2026, null, null);
            BoiteQuantite bq = new BoiteQuantite(b, quantite);
            boite.ajouterBoiteIncluse(bq);
            afficherAlerte(AlertType.INFORMATION, "Succès", "Sous-boîte ajoutée avec succès");
        }
        
        // Appel pour relancer le calcul des pieces et mettre la liste a jour sur l ecran
        rafraichirAffichage();
    }

    // Vide le conteneur des statistiques et le remplace par un nouveau genere avec les dernieres donnees
    private void rafraichirAffichage() {
        appliquerFiltreContenu();
        
        VBox nouvellesStats = creerSectionStatistiques(this.boite);
        sectionStatistiques.getChildren().setAll(nouvellesStats.getChildren());
    }

    private void afficherAlerte(AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }

    private VBox creerOngletContenu() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        HBox barreFiltre = new HBox(15);
        barreFiltre.setAlignment(Pos.CENTER_LEFT);

        Label lblFiltre = new Label("Filtrer l'affichage :");
        lblFiltre.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        comboFiltreContenu = new ComboBox<>();
        comboFiltreContenu.getItems().addAll("Tout afficher", "Pièces", "Figurines", "Sous-boîtes");
        comboFiltreContenu.setValue("Tout afficher");

        barreFiltre.getChildren().addAll(lblFiltre, comboFiltreContenu);

        listeComposition = new ListView<>();
        listeComposition.setPrefHeight(250);

        conteneur.getChildren().addAll(barreFiltre, listeComposition);
        return conteneur;
    }

    // Force la liste deroulante a afficher correctement le champ nom de l objet Theme
    private void configurerControleTheme() {
        comboTheme.setItems(tousLesThemes);
        
        comboTheme.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Theme item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getNom());
            }
        });
        comboTheme.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Theme item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getNom());
            }
        });

        if (boite.getTheme() != null) {
            comboTheme.setValue(boite.getTheme());
        }
    }

    private void gererActionSauvegarde() {
        String nouveauNom = txtNom.getText().trim();
        String nouvelleAnneeStr = txtAnnee.getText().trim();
        Theme themeSelectionne = comboTheme.getValue();

        if (nouveauNom.isEmpty() || nouvelleAnneeStr.isEmpty()) {
            afficherAlerte(AlertType.WARNING, "Champs incomplets", "Veuillez renseigner le nom et l'année avant de sauvegarder");
            return;
        }

        try {
            int nouvelleAnnee = Integer.parseInt(nouvelleAnneeStr);
            boite.setNom(nouveauNom);
            boite.setAnnee(nouvelleAnnee);
            if (themeSelectionne != null) {
                boite.setTheme(themeSelectionne);
            }
            
            afficherAlerte(AlertType.INFORMATION, "Mise à jour réussie", "Les informations textuelles ont été modifiées avec succès");

        } catch (NumberFormatException ex) {
            afficherAlerte(AlertType.ERROR, "Erreur de saisie", "L'année de production doit être un nombre entier");
        }
    }

    private VBox creerSectionStatistiques(Boite boite) {
        VBox sectionComplete = new VBox(15);
        sectionComplete.setPadding(new Insets(20));
        sectionComplete.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label lblTitreStats = new Label("Statistiques du contenu réel");
        lblTitreStats.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Separator separator = new Separator();
        BoiteStats stats = boiteService.calculerStatsBoite(boite.getNumero());
        
        if (stats == null || stats.getTotalPieces() == 0) {
            Label lblVide = new Label("Aucun inventaire détaillé disponible pour cette boîte");
            lblVide.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            sectionComplete.getChildren().addAll(lblTitreStats, separator, lblVide);
            return sectionComplete;
        }

        HBox conteneurDivise = new HBox(40);
        conteneurDivise.setAlignment(Pos.TOP_LEFT);

        VBox colonneGauche = new VBox(20);
        colonneGauche.setPrefWidth(350);
        colonneGauche.setMaxWidth(350);

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
        flowCouleurs.setPrefWrapLength(320);

        PieChart graphiqueCouleurs = new PieChart();
        graphiqueCouleurs.setPrefSize(300, 300);
        graphiqueCouleurs.setLegendVisible(false);

        for (Map.Entry<Couleur, Integer> entree : stats.getRepartitionCouleurs().entrySet()) {
            Couleur couleur = entree.getKey();
            int quantite = entree.getValue();
            String rgbHex = formaterRgb(couleur.getRgb());
            
            Label tagCouleur = new Label(couleur.getNom() + " (" + quantite + ")");
            tagCouleur.setStyle("-fx-background-color: " + rgbHex + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
            flowCouleurs.getChildren().add(tagCouleur);

            PieChart.Data tranche = new PieChart.Data(couleur.getNom(), quantite);
            graphiqueCouleurs.getData().add(tranche);
        }

        ScrollPane scrollCouleurs = new ScrollPane(flowCouleurs);
        scrollCouleurs.setFitToWidth(true);
        scrollCouleurs.setPrefHeight(120);

        colonneGauche.getChildren().addAll(grilleStats, lblCouleurs, scrollCouleurs);
        conteneurDivise.getChildren().addAll(colonneGauche, graphiqueCouleurs);
        HBox.setHgrow(graphiqueCouleurs, Priority.ALWAYS);

        sectionComplete.getChildren().addAll(lblTitreStats, separator, conteneurDivise);
        return sectionComplete;
    }

    private void ajouterLigneInfo(GridPane grille, int ligne, String libelle, String valeur) {
        Label lblLibelle = new Label(libelle);
        lblLibelle.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        grille.add(lblLibelle, 0, ligne);
        grille.add(lblValeur, 1, ligne);
    }

    private void ajouterLigneEdition(GridPane grille, int ligne, String libelle, TextField champ) {
        Label lblLibelle = new Label(libelle);
        lblLibelle.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        champ.setStyle("-fx-font-size: 14px;");
        champ.setPrefWidth(220);
        grille.add(lblLibelle, 0, ligne);
        grille.add(champ, 1, ligne);
    }

    private String formaterRgb(String rgb) {
        if (rgb == null || rgb.isBlank()) return "#ffffff";
        return rgb.startsWith("#") ? rgb : "#" + rgb;
    }
}