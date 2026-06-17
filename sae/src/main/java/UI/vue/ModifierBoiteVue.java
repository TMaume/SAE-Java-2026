package UI.vue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

import App.Boite;
import App.BoiteQuantite;
import App.BoiteService;
import App.Figurine;
import App.FigurineQuantite;
import App.Piece;
import App.PieceQuantite;
import App.PieceService;
import App.Theme;
import App.ThemeService;

public class ModifierBoiteVue extends BorderPane {

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
    
    // Nouveaux composants pour la recherche visuelle
    private TextField txtRechercheAjout;
    private ComboBox<String> comboTypeAjout;
    private FlowPane conteneurResultatsAjout;
    private TextField txtQuantiteAjout;
    private Label lblSelection;
    private String referenceSelectionnee = "";

    private ObservableList<Theme> tousLesThemes;

    public ModifierBoiteVue(Boite boite, BoiteService boiteService, ThemeService themeService, PieceService pieceService, Runnable actionRetour) {
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

        setTop(creerEnTete(actionRetour, "Modification de boîte"));
        setCenter(creerContenu());

        configurerEcouteurs();
        appliquerFiltreContenu();
    }

    private void configurerEcouteurs() {
        txtRechercheTheme.textProperty().addListener((observable, oldValue, newValue) -> {
            appliquerFiltreTheme();
        });

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
        String texteRecherche = txtRechercheTheme.getText().trim().toLowerCase();
        
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

    /**
     * Effectue la recherche et génère les cartes visuelles.
     */
    private void rechercherElementsAjout(String recherche, String type) {
        conteneurResultatsAjout.getChildren().clear();
        referenceSelectionnee = "";
        lblSelection.setText("Aucune sélection");
        
        if (recherche == null || recherche.trim().length() < 2) {
            return;
        }
        
        String motCle = recherche.trim();
        
        if (type.equals("Pièce") && pieceService != null) {
            List<Piece> resultats = pieceService.rechercherPiecesParMotCle(motCle);
            for (Piece p : resultats) {
                String cat = p.getCategorie() != null ? p.getCategorie().getNom() : "";
                conteneurResultatsAjout.getChildren().add(creerCarteResultat(p.getNumero(), p.getNom(), cat, null));
            }
        } else if (type.equals("Figurine") && boiteService != null) {
            List<Figurine> resultats = boiteService.rechercherFigurinesParMotCle(motCle);
            for (Figurine f : resultats) {
                conteneurResultatsAjout.getChildren().add(creerCarteResultat(f.getIdFigurine(), f.getNom(), f.getNbParties() + " parties", f.getImageF()));
            }
        } else if (type.equals("Sous-boîte") && boiteService != null) {
            List<Boite> resultats = boiteService.rechercherBoitesParNom(motCle);
            for (Boite b : resultats) {
                String infos = (b.getTheme() != null ? b.getTheme().getNom() : "") + " • " + b.getAnnee();
                conteneurResultatsAjout.getChildren().add(creerCarteResultat(b.getNumero(), b.getNom(), infos, b.getImageBoite()));
            }
        }
    }

    /**
     * Crée une carte visuelle sans le bouton "Ajouter à ma collection".
     */
    private VBox creerCarteResultat(String ref, String nom, String infos, String urlImage) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(10));
        // Style calqué sur ton thème bordeaux/or
        carte.setStyle("-fx-background-color: #5c1416; -fx-border-color: #d4af37; -fx-border-radius: 5; -fx-background-radius: 5;");
        carte.setPrefWidth(160);
        carte.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(130);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        if (urlImage != null && !urlImage.isBlank()) {
            try {
                imageView.setImage(new Image(urlImage, true));
            } catch (Exception e) {}
        }
        VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setStyle("-fx-background-color: white; -fx-background-radius: 3;");
        conteneurImage.setPrefHeight(110);
        conteneurImage.setPadding(new Insets(5));

        Label lblRef = new Label("#" + ref);
        lblRef.setStyle("-fx-text-fill: #d4af37; -fx-font-weight: bold;");

        Label lblNom = new Label(nom);
        lblNom.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 12px;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(35);

        Label lblInfos = new Label(infos);
        lblInfos.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 10px;");

        // Bouton de sélection (Remplace le bouton Collection)
        Button btnSelect = new Button("Sélectionner");
        btnSelect.setStyle("-fx-background-color: #3b0d0e; -fx-border-color: #d4af37; -fx-text-fill: #d4af37; -fx-cursor: hand; -fx-border-radius: 3; -fx-background-radius: 3;");
        btnSelect.setMaxWidth(Double.MAX_VALUE);
        btnSelect.setOnAction(e -> {
            referenceSelectionnee = ref;
            lblSelection.setText("Réf sélectionnée : " + ref);
        });

        carte.getChildren().addAll(conteneurImage, lblRef, lblNom, lblInfos, btnSelect);
        return carte;
    }

    private HBox creerEnTete(Runnable actionRetour, String titre) {
        HBox entete = new HBox(20);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(0, 0, 30, 0));

        Button btnRetour = new Button("◄ Retour");
        btnRetour.getStyleClass().add("button");
        btnRetour.setOnAction(e -> actionRetour.run());

        Label lblTitre = new Label(titre);
        lblTitre.getStyleClass().add("title-label");

        entete.getChildren().addAll(btnRetour, lblTitre);
        return entete;
    }

    private HBox creerContenu() {
        HBox contenu = new HBox(30);
        contenu.setAlignment(Pos.TOP_LEFT);

        VBox zoneInformations = new VBox(20);
        zoneInformations.getChildren().addAll(
            creerZoneOnglets()
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
        conteneurImage.getStyleClass().add("card");
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
        conteneurGeneral.getStyleClass().add("card");

        TabPane tabPaneEdition = new TabPane();
        tabPaneEdition.setPrefHeight(350);

        Tab tabInfos = new Tab("Détails");
        tabInfos.setClosable(false);
        tabInfos.setContent(creerOngletInformations());
        
        Tab tabAjout = new Tab("Ajouter");
        tabAjout.setClosable(false);
        tabAjout.setContent(creerOngletAjout());

        Tab tabContenu = new Tab("Inventaire");
        tabContenu.setClosable(false);
        tabContenu.setContent(creerOngletContenu());

        tabPaneEdition.getTabs().addAll(tabInfos, tabAjout, tabContenu);

        Button btnSauvegarder = new Button("Enregistrer les modifications");
        btnSauvegarder.setMaxWidth(Double.MAX_VALUE);
        btnSauvegarder.getStyleClass().add("button");
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

        ajouterLigneInfo(grille, 0, "Référence :", boite.getNumero());
        ajouterLigneEdition(grille, 1, "Nom :", txtNom);
        ajouterLigneEdition(grille, 2, "Année :", txtAnnee);

        VBox zoneTheme = new VBox(10);
        zoneTheme.setPadding(new Insets(10, 0, 0, 0));
        
        Label lblTheme = new Label("Rechercher un thème :");
        lblTheme.getStyleClass().add("subtitle-label");

        txtRechercheTheme = new TextField();
        txtRechercheTheme.setPromptText("Ex: Star Wars");
        txtRechercheTheme.setPrefWidth(200);
        
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
        
        Label lblTitre = new Label("Rechercher un élément à ajouter");
        lblTitre.getStyleClass().add("subtitle-label");
        
        HBox barreRecherche = new HBox(10);
        barreRecherche.setAlignment(Pos.CENTER_LEFT);
        
        comboTypeAjout = new ComboBox<>();
        comboTypeAjout.getItems().addAll("Pièce", "Figurine", "Sous-boîte");
        comboTypeAjout.setValue("Pièce");
        
        txtRechercheAjout = new TextField();
        txtRechercheAjout.setPromptText("Mot-clé ou référence");
        txtRechercheAjout.setPrefWidth(200);
        
        barreRecherche.getChildren().addAll(comboTypeAjout, txtRechercheAjout);
        
        // C'est ici que la magie opère : la galerie de cartes remplace la vieille liste !
        conteneurResultatsAjout = new FlowPane(15, 15);
        conteneurResultatsAjout.setAlignment(Pos.TOP_LEFT);
        ScrollPane scrollResultats = new ScrollPane(conteneurResultatsAjout);
        scrollResultats.setFitToWidth(true);
        scrollResultats.setPrefHeight(250);
        scrollResultats.setStyle("-fx-background-color: transparent;");
        
        HBox zoneAction = new HBox(15);
        zoneAction.setAlignment(Pos.CENTER_LEFT);
        
        lblSelection = new Label("Aucune sélection");
        lblSelection.setStyle("-fx-font-weight: bold; -fx-text-fill: #e67e22; -fx-min-width: 150px;");
        
        Label lblQuantite = new Label("Qté :");
        txtQuantiteAjout = new TextField("1");
        txtQuantiteAjout.setPrefWidth(50);
        
        Button btnAjouter = new Button("➕ Ajouter");
        btnAjouter.getStyleClass().add("button");
        btnAjouter.setOnAction(e -> gererAjoutElement());
        
        zoneAction.getChildren().addAll(lblSelection, lblQuantite, txtQuantiteAjout, btnAjouter);
        
        conteneur.getChildren().addAll(lblTitre, barreRecherche, scrollResultats, zoneAction);
        return conteneur;
    }

    private void gererAjoutElement() {
        String type = comboTypeAjout.getValue();
        
        if (referenceSelectionnee == null || referenceSelectionnee.isEmpty()) {
            afficherAlerte(AlertType.WARNING, "Sélection requise", "Veuillez cliquer sur le bouton 'Sélectionner' d'une des cartes.");
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
        
        String ref = referenceSelectionnee;
        
        if (type.equals("Pièce") && pieceService != null) {
            Piece piece = pieceService.rechercherPiece(ref);
            if (piece != null) {
                PieceQuantite pq = new PieceQuantite(piece, quantite, false, null);
                boite.ajouterPiece(pq);
                boiteService.ajouterPieceABoite(boite.getNumero(), pq);
                afficherAlerte(AlertType.INFORMATION, "Succès", "Pièce ajoutée avec succès");
            }
        } else if (type.equals("Figurine") && boiteService != null) {
            Figurine fig = boiteService.rechercherFigurine(ref);
            if (fig != null) {
                FigurineQuantite fq = new FigurineQuantite(fig, quantite);
                boite.ajouterFigurine(fq);
                boiteService.ajouterFigurineABoite(boite.getNumero(), fq);
                afficherAlerte(AlertType.INFORMATION, "Succès", "Figurine ajoutée avec succès");
            }
        } else if (type.equals("Sous-boîte") && boiteService != null) {
            Boite b = boiteService.rechercherBoiteParNumero(ref);
            if (b != null) {
                BoiteQuantite bq = new BoiteQuantite(b, quantite);
                boite.ajouterBoiteIncluse(bq);
                boiteService.ajouterSousBoiteABoite(boite.getNumero(), bq);
                afficherAlerte(AlertType.INFORMATION, "Succès", "Sous-boîte ajoutée avec succès");
            }
        }
        
        rafraichirAffichage();
        
        // On remet à zéro la sélection après un ajout réussi
        referenceSelectionnee = "";
        lblSelection.setText("Aucune sélection");
    }

    private void rafraichirAffichage() {
        appliquerFiltreContenu();
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

        Label lblFiltre = new Label("Filtrer :");
        lblFiltre.getStyleClass().add("subtitle-label");

        comboFiltreContenu = new ComboBox<>();
        comboFiltreContenu.getItems().addAll("Tout afficher", "Pièces", "Figurines", "Sous-boîtes");
        comboFiltreContenu.setValue("Tout afficher");

        barreFiltre.getChildren().addAll(lblFiltre, comboFiltreContenu);

        listeComposition = new ListView<>();
        listeComposition.setPrefHeight(250);

        conteneur.getChildren().addAll(barreFiltre, listeComposition);
        return conteneur;
    }

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
            afficherAlerte(AlertType.WARNING, "Champs incomplets", "Veuillez renseigner le nom et l'année");
            return;
        }

        try {
            int nouvelleAnnee = Integer.parseInt(nouvelleAnneeStr);
            boite.setNom(nouveauNom);
            boite.setAnnee(nouvelleAnnee);
            if (themeSelectionne != null) {
                boite.setTheme(themeSelectionne);
            }
            
            afficherAlerte(AlertType.INFORMATION, "Succès", "Informations modifiées avec succès");

        } catch (NumberFormatException ex) {
            afficherAlerte(AlertType.ERROR, "Erreur de saisie", "L'année doit être un nombre entier");
        }
    }

    private void ajouterLigneInfo(GridPane grille, int ligne, String libelle, String valeur) {
        Label lblLibelle = new Label(libelle);
        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-weight: bold;");
        grille.add(lblLibelle, 0, ligne);
        grille.add(lblValeur, 1, ligne);
    }

    private void ajouterLigneEdition(GridPane grille, int ligne, String libelle, TextField champ) {
        Label lblLibelle = new Label(libelle);
        champ.setPrefWidth(220);
        grille.add(lblLibelle, 0, ligne);
        grille.add(champ, 1, ligne);
    }
}