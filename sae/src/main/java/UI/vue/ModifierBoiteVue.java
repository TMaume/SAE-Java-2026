package UI.vue;

import javafx.beans.property.SimpleStringProperty;
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

    // Composants pour la recherche en tableau
    private TextField txtRechercheAjout;
    private ComboBox<String> comboTypeAjout;
    private TableView<String[]> tableResultats;
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
        txtRechercheTheme.textProperty().addListener((obs, o, n) -> appliquerFiltreTheme());

        comboFiltreContenu.setOnAction(e -> appliquerFiltreContenu());

        txtRechercheAjout.textProperty().addListener((obs, o, n) ->
                rechercherElementsAjout(n, comboTypeAjout.getValue()));

        comboTypeAjout.setOnAction(e -> {
            configurerColonnesTableau(comboTypeAjout.getValue());
            rechercherElementsAjout(txtRechercheAjout.getText(), comboTypeAjout.getValue());
        });
    }

    // -----------------------------------------------------------------------
    // FILTRAGE THEME
    // -----------------------------------------------------------------------

    private void appliquerFiltreTheme() {
        String texte = txtRechercheTheme.getText().trim().toLowerCase();
        if (texte.isEmpty()) {
            comboTheme.setItems(tousLesThemes);
        } else {
            ObservableList<Theme> filtres = FXCollections.observableArrayList();
            for (Theme t : tousLesThemes) {
                if (t.getNom().toLowerCase().contains(texte)) filtres.add(t);
            }
            comboTheme.setItems(filtres);
            if (!filtres.isEmpty()) comboTheme.show();
        }
    }

    // -----------------------------------------------------------------------
    // FILTRAGE INVENTAIRE
    // -----------------------------------------------------------------------

    private void appliquerFiltreContenu() {
        String filtre = comboFiltreContenu.getValue();
        listeComposition.getItems().clear();

        boolean pieces    = filtre.equals("Tout afficher") || filtre.equals("Pièces");
        boolean figurines = filtre.equals("Tout afficher") || filtre.equals("Figurines");
        boolean sousBoites = filtre.equals("Tout afficher") || filtre.equals("Sous-boîtes");

        if (pieces && boite.getPieces() != null)
            for (PieceQuantite pq : boite.getPieces())
                listeComposition.getItems().add("[Pièce] Réf: " + pq.getPiece().getNumero() + " | Quantité: " + pq.getQuantite());

        if (figurines && boite.getFigurines() != null)
            for (FigurineQuantite fq : boite.getFigurines())
                listeComposition.getItems().add("[Figurine] Réf: " + fq.getFigurine().getIdFigurine() + " | Quantité: " + fq.getQuantite());

        if (sousBoites && boite.getBoitesIncluses() != null)
            for (BoiteQuantite bq : boite.getBoitesIncluses())
                listeComposition.getItems().add("[Sous-boîte] N°: " + bq.getBoite().getNumero() + " | Quantité: " + bq.getQuantite());
    }

    // -----------------------------------------------------------------------
    // RECHERCHE → TABLEAU
    // -----------------------------------------------------------------------

    /**
     * Configure les colonnes du tableau selon le type sélectionné.
     */
    @SuppressWarnings("unchecked")
    private void configurerColonnesTableau(String type) {
        tableResultats.getColumns().clear();

        TableColumn<String[], String> colRef = new TableColumn<>("Référence");
        colRef.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colRef.setPrefWidth(120);

        TableColumn<String[], String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        colNom.setPrefWidth(200);

        TableColumn<String[], String> colInfo = new TableColumn<>(
                type.equals("Pièce") ? "Catégorie"
                : type.equals("Figurine") ? "Nb parties"
                : "Thème • Année");
        colInfo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        colInfo.setPrefWidth(150);

        tableResultats.getColumns().addAll(colRef, colNom, colInfo);
    }

    /**
     * Effectue la recherche et remplit le tableau.
     */
    private void rechercherElementsAjout(String recherche, String type) {
        tableResultats.getItems().clear();
        referenceSelectionnee = "";
        lblSelection.setText("Aucune sélection");

        if (recherche == null || recherche.trim().length() < 2) return;

        String motCle = recherche.trim();

        if (type.equals("Pièce") && pieceService != null) {
            List<Piece> resultats = pieceService.rechercherPiecesParMotCle(motCle);
            for (Piece p : resultats) {
                String cat = (p.getCategorie() != null) ? p.getCategorie().getNom() : "";
                tableResultats.getItems().add(new String[]{p.getNumero(), p.getNom(), cat});
            }

        } else if (type.equals("Figurine") && boiteService != null) {
            List<Figurine> resultats = boiteService.rechercherFigurinesParMotCle(motCle);
            for (Figurine f : resultats) {
                String parties = f.getNbParties() != null ? f.getNbParties() + " parties" : "";
                tableResultats.getItems().add(new String[]{f.getIdFigurine(), f.getNom(), parties});
            }

        } else if (type.equals("Sous-boîte") && boiteService != null) {
            List<Boite> resultats = boiteService.rechercherBoitesParNom(motCle);
            for (Boite b : resultats) {
                String infos = (b.getTheme() != null ? b.getTheme().getNom() : "") + " • " + b.getAnnee();
                tableResultats.getItems().add(new String[]{b.getNumero(), b.getNom(), infos});
            }
        }
    }

    // -----------------------------------------------------------------------
    // CONSTRUCTION DE L'UI
    // -----------------------------------------------------------------------

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
        zoneInformations.getChildren().add(creerZoneOnglets());
        HBox.setHgrow(zoneInformations, Priority.ALWAYS);

        contenu.getChildren().addAll(creerSectionImage(this.boite), zoneInformations);
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
            imageView.setImage(new Image(url, true));
        }

        conteneurImage.getChildren().add(imageView);
        return conteneurImage;
    }

    private VBox creerZoneOnglets() {
        VBox conteneurGeneral = new VBox(15);
        conteneurGeneral.setPadding(new Insets(20));
        conteneurGeneral.getStyleClass().add("card");

        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(380);

        Tab tabInfos = new Tab("Détails");
        tabInfos.setClosable(false);
        tabInfos.setContent(creerOngletInformations());

        Tab tabAjout = new Tab("Ajouter");
        tabAjout.setClosable(false);
        tabAjout.setContent(creerOngletAjout());

        Tab tabContenu = new Tab("Inventaire");
        tabContenu.setClosable(false);
        tabContenu.setContent(creerOngletContenu());

        tabPane.getTabs().addAll(tabInfos, tabAjout, tabContenu);

        Button btnSauvegarder = new Button("Enregistrer les modifications");
        btnSauvegarder.setMaxWidth(Double.MAX_VALUE);
        btnSauvegarder.getStyleClass().add("button");
        btnSauvegarder.setOnAction(e -> gererActionSauvegarde());

        conteneurGeneral.getChildren().addAll(tabPane, btnSauvegarder);
        return conteneurGeneral;
    }

    // -----------------------------------------------------------------------
    // ONGLETS
    // -----------------------------------------------------------------------

    private VBox creerOngletInformations() {
        VBox conteneur = new VBox(20);
        conteneur.setPadding(new Insets(20));

        GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(40);

        txtNom = new TextField(boite.getNom() != null ? boite.getNom() : "");
        int anneeActuelle = LocalDate.now().getYear();
        txtAnnee = new TextField(boite.getAnnee() != null ? String.valueOf(boite.getAnnee()) : String.valueOf(anneeActuelle));

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

    @SuppressWarnings("unchecked")
    private VBox creerOngletAjout() {
        VBox conteneur = new VBox(12);
        conteneur.setPadding(new Insets(20));

        Label lblTitre = new Label("Rechercher un élément à ajouter");
        lblTitre.getStyleClass().add("subtitle-label");

        // Barre de recherche
        HBox barreRecherche = new HBox(10);
        barreRecherche.setAlignment(Pos.CENTER_LEFT);

        comboTypeAjout = new ComboBox<>();
        comboTypeAjout.getItems().addAll("Pièce", "Figurine", "Sous-boîte");
        comboTypeAjout.setValue("Pièce");

        txtRechercheAjout = new TextField();
        txtRechercheAjout.setPromptText("Mot-clé ou référence (min. 2 caractères)");
        txtRechercheAjout.setPrefWidth(250);
        HBox.setHgrow(txtRechercheAjout, Priority.ALWAYS);

        barreRecherche.getChildren().addAll(comboTypeAjout, txtRechercheAjout);

        // Tableau des résultats
        tableResultats = new TableView<>();
        tableResultats.setPlaceholder(new Label("Aucun résultat — saisissez au moins 2 caractères"));
        tableResultats.setPrefHeight(220);
        tableResultats.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Sélection dans le tableau → maj label
        tableResultats.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                referenceSelectionnee = newRow[0];
                lblSelection.setText("Réf sélectionnée : " + newRow[0] + " — " + newRow[1]);
            }
        });

        // Initialiser les colonnes pour le type par défaut (Pièce)
        configurerColonnesTableau("Pièce");

        // Zone action
        HBox zoneAction = new HBox(12);
        zoneAction.setAlignment(Pos.CENTER_LEFT);

        lblSelection = new Label("Aucune sélection");
        lblSelection.setStyle("-fx-font-weight: bold; -fx-text-fill: #e67e22;");
        HBox.setHgrow(lblSelection, Priority.ALWAYS);

        Label lblQte = new Label("Qté :");
        txtQuantiteAjout = new TextField("1");
        txtQuantiteAjout.setPrefWidth(55);

        Button btnAjouter = new Button("➕ Ajouter");
        btnAjouter.getStyleClass().add("button");
        btnAjouter.setOnAction(e -> gererAjoutElement());

        zoneAction.getChildren().addAll(lblSelection, lblQte, txtQuantiteAjout, btnAjouter);

        conteneur.getChildren().addAll(lblTitre, barreRecherche, tableResultats, zoneAction);
        return conteneur;
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

    // -----------------------------------------------------------------------
    // LOGIQUE MÉTIER
    // -----------------------------------------------------------------------

    private void gererAjoutElement() {
        String type = comboTypeAjout.getValue();

        if (referenceSelectionnee == null || referenceSelectionnee.isEmpty()) {
            afficherAlerte(AlertType.WARNING, "Sélection requise", "Veuillez sélectionner une ligne dans le tableau.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(txtQuantiteAjout.getText().trim());
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            afficherAlerte(AlertType.WARNING, "Quantité invalide", "Veuillez saisir un nombre entier positif.");
            return;
        }

        String ref = referenceSelectionnee;

        if (type.equals("Pièce") && pieceService != null) {
            Piece piece = pieceService.rechercherPiece(ref);
            if (piece != null) {
                PieceQuantite pq = new PieceQuantite(piece, quantite, false, null);
                boite.ajouterPiece(pq);
                boiteService.ajouterPieceABoite(boite.getNumero(), pq);
                afficherAlerte(AlertType.INFORMATION, "Succès", "Pièce ajoutée avec succès.");
            } else {
                afficherAlerte(AlertType.ERROR, "Introuvable", "Pièce introuvable en base (réf : " + ref + ").");
            }

        } else if (type.equals("Figurine") && boiteService != null) {
            Figurine fig = boiteService.rechercherFigurine(ref);
            if (fig != null) {
                FigurineQuantite fq = new FigurineQuantite(fig, quantite);
                boite.ajouterFigurine(fq);
                boiteService.ajouterFigurineABoite(boite.getNumero(), fq);
                afficherAlerte(AlertType.INFORMATION, "Succès", "Figurine ajoutée avec succès.");
            } else {
                afficherAlerte(AlertType.ERROR, "Introuvable", "Figurine introuvable en base (réf : " + ref + ").");
            }

        } else if (type.equals("Sous-boîte") && boiteService != null) {
            Boite b = boiteService.rechercherBoiteParNumero(ref);
            if (b != null) {
                BoiteQuantite bq = new BoiteQuantite(b, quantite);
                boite.ajouterBoiteIncluse(bq);
                boiteService.ajouterSousBoiteABoite(boite.getNumero(), bq);
                afficherAlerte(AlertType.INFORMATION, "Succès", "Sous-boîte ajoutée avec succès.");
            } else {
                afficherAlerte(AlertType.ERROR, "Introuvable", "Boîte introuvable en base (réf : " + ref + ").");
            }
        }

        rafraichirAffichage();
        referenceSelectionnee = "";
        lblSelection.setText("Aucune sélection");
    }

    private void gererActionSauvegarde() {
        String nouveauNom = txtNom.getText().trim();
        String anneeStr = txtAnnee.getText().trim();
        Theme themeSelectionne = comboTheme.getValue();

        if (nouveauNom.isEmpty() || anneeStr.isEmpty()) {
            afficherAlerte(AlertType.WARNING, "Champs incomplets", "Veuillez renseigner le nom et l'année.");
            return;
        }

        try {
            int nouvelleAnnee = Integer.parseInt(anneeStr);
            boite.setNom(nouveauNom);
            boite.setAnnee(nouvelleAnnee);
            if (themeSelectionne != null) boite.setTheme(themeSelectionne);
            afficherAlerte(AlertType.INFORMATION, "Succès", "Informations modifiées avec succès.");
        } catch (NumberFormatException ex) {
            afficherAlerte(AlertType.ERROR, "Erreur de saisie", "L'année doit être un nombre entier.");
        }
    }

    private void rafraichirAffichage() {
        appliquerFiltreContenu();
    }

    // -----------------------------------------------------------------------
    // HELPERS
    // -----------------------------------------------------------------------

    private void configurerControleTheme() {
        comboTheme.setItems(tousLesThemes);
        comboTheme.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Theme item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getNom());
            }
        });
        comboTheme.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Theme item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getNom());
            }
        });
        if (boite.getTheme() != null) comboTheme.setValue(boite.getTheme());
    }

    private void ajouterLigneInfo(GridPane grille, int ligne, String libelle, String valeur) {
        Label lblLib = new Label(libelle);
        Label lblVal = new Label(valeur);
        lblVal.setStyle("-fx-font-weight: bold;");
        grille.add(lblLib, 0, ligne);
        grille.add(lblVal, 1, ligne);
    }

    private void ajouterLigneEdition(GridPane grille, int ligne, String libelle, TextField champ) {
        Label lblLib = new Label(libelle);
        champ.setPrefWidth(220);
        grille.add(lblLib, 0, ligne);
        grille.add(champ, 1, ligne);
    }

    private void afficherAlerte(AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}