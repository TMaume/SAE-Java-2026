package UI.vue;

import App.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComposerBoiteVue extends HBox {
    private final CollectionService collectionService;
    private final ThemeService themeService;
    private final PieceService pieceService;
    private final BoiteService boiteService; // Indispensable pour chercher les figurines
    private final Runnable actionApresCreation;

    // Champs Formulaire Boîte
    private TextField numeroField;
    private TextField nomField;
    private TextField anneeField;
    private ComboBox<Theme> themeComboBox;
    private TextField imageField;
    private ImageView apercuImageView;
    private Label messageLabel;

    // Champs Inventaire (Style TableView comme dans ModifierBoiteVue)
    private ComboBox<String> comboTypeAjout;
    private TextField txtRechercheAjout;
    private TableView<String[]> tableResultats;
    private TextField txtQuantiteAjout;
    private Label lblSelection;
    private String referenceSelectionnee = "";
    private ListView<String> listeInventaireVisuel;

    // Données Temporaires
    private final List<PieceQuantite> piecesTemporaires = new ArrayList<>();
    private final List<FigurineQuantite> figurinesTemporaires = new ArrayList<>();

    public ComposerBoiteVue(CollectionService collectionService, ThemeService themeService, PieceService pieceService, BoiteService boiteService, Runnable actionApresCreation) {
        this.collectionService = collectionService;
        this.themeService = themeService;
        this.pieceService = pieceService;
        this.boiteService = boiteService;
        this.actionApresCreation = actionApresCreation;

        this.setSpacing(30);
        this.setPadding(new Insets(25));
        this.setAlignment(Pos.TOP_CENTER);

        // ================= ZONE GAUCHE : FORMULAIRE DE LA BOÎTE =================
        VBox zoneFormulaire = new VBox(20);
        zoneFormulaire.setMaxWidth(400);

        Label titreLabel = new Label("1. Informations du modèle");
        titreLabel.getStyleClass().add("title-label");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(14);

        grid.add(new Label("Référence :"), 0, 0);
        numeroField = new TextField(); numeroField.setPromptText("Ex: PERSO-01");
        grid.add(numeroField, 1, 0);

        grid.add(new Label("Nom :"), 0, 1);
        nomField = new TextField(); nomField.setPromptText("Ex: Mon Vaisseau");
        grid.add(nomField, 1, 1);

        grid.add(new Label("Année :"), 0, 2);
        anneeField = new TextField(String.valueOf(LocalDate.now().getYear()));
        grid.add(anneeField, 1, 2);

        grid.add(new Label("Thème :"), 0, 3);
        themeComboBox = new ComboBox<>(); themeComboBox.setPromptText("Sélectionner...");
        grid.add(themeComboBox, 1, 3);

        grid.add(new Label("Image (URL) :"), 0, 4);
        imageField = new TextField();
        grid.add(imageField, 1, 4);

        Button visionnerImageButton = new Button("Visionner");
        visionnerImageButton.setOnAction(e -> chargerApercu());
        grid.add(visionnerImageButton, 1, 5);

        apercuImageView = new ImageView();
        apercuImageView.setFitWidth(150); apercuImageView.setFitHeight(150);
        apercuImageView.setPreserveRatio(true);
        VBox conteneurApercu = new VBox(apercuImageView);
        conteneurApercu.setAlignment(Pos.CENTER);

        zoneFormulaire.getChildren().addAll(titreLabel, grid, conteneurApercu);

        // ================= ZONE DROITE : INVENTAIRE =================
        VBox zoneInventaire = new VBox(12);
        zoneInventaire.getStyleClass().add("card");
        zoneInventaire.setPadding(new Insets(20));
        zoneInventaire.setPrefWidth(500);
        HBox.setHgrow(zoneInventaire, Priority.ALWAYS);

        Label titreInventaire = new Label("2. Chercher & Ajouter à l'inventaire");
        titreInventaire.getStyleClass().add("subtitle-label");

        HBox barreRecherche = new HBox(10);
        barreRecherche.setAlignment(Pos.CENTER_LEFT);

        comboTypeAjout = new ComboBox<>(FXCollections.observableArrayList("Pièce", "Figurine"));
        comboTypeAjout.setValue("Pièce");

        txtRechercheAjout = new TextField();
        txtRechercheAjout.setPromptText("Mot-clé ou réf (min. 2 car.)");
        txtRechercheAjout.setPrefWidth(220);
        HBox.setHgrow(txtRechercheAjout, Priority.ALWAYS);

        barreRecherche.getChildren().addAll(comboTypeAjout, txtRechercheAjout);

        // Tableau des résultats
        tableResultats = new TableView<>();
        tableResultats.setPlaceholder(new Label("Aucun résultat — saisissez au moins 2 caractères (ou ajoutez comme élément personnalisé)"));
        tableResultats.setPrefHeight(180);
        tableResultats.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableResultats.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                referenceSelectionnee = newRow[0];
                lblSelection.setText("Sélection : " + newRow[1]);
            }
        });

        configurerColonnesTableau("Pièce");

        // Action d'ajout
        HBox zoneAction = new HBox(12);
        zoneAction.setAlignment(Pos.CENTER_LEFT);

        lblSelection = new Label("Aucune sélection");
        lblSelection.setStyle("-fx-font-weight: bold; -fx-text-fill: #e67e22;");
        HBox.setHgrow(lblSelection, Priority.ALWAYS);

        txtQuantiteAjout = new TextField("1");
        txtQuantiteAjout.setPrefWidth(50);

        Button btnAjouterItem = new Button("➕ Ajouter");
        btnAjouterItem.getStyleClass().add("button");
        btnAjouterItem.setOnAction(e -> gererAjoutElement());

        zoneAction.getChildren().addAll(lblSelection, new Label("Qté:"), txtQuantiteAjout, btnAjouterItem);

        // Liste finale
        listeInventaireVisuel = new ListView<>();
        listeInventaireVisuel.setPrefHeight(150);

        Button btnSupprimerElement = new Button("🗑 Supprimer l'élément sélectionné");
        btnSupprimerElement.getStyleClass().add("button");
        btnSupprimerElement.setOnAction(e -> gererSuppressionElement());

        // Bouton de validation globale
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button ajouterButton = new Button("Enregistrer la boîte personnalisée");
        ajouterButton.getStyleClass().add("btn-primary");
        ajouterButton.setMaxWidth(Double.MAX_VALUE);
        ajouterButton.setOnAction(e -> handleCreerBoitePerso());
        
        messageLabel = new Label();
        actionBox.getChildren().addAll(ajouterButton, messageLabel);

        zoneInventaire.getChildren().addAll(titreInventaire, barreRecherche, tableResultats, zoneAction, listeInventaireVisuel, btnSupprimerElement, actionBox);

        this.getChildren().addAll(zoneFormulaire, zoneInventaire);

        chargerThemes();
        configurerEcouteursRecherche();
    }

    private void configurerEcouteursRecherche() {
        txtRechercheAjout.textProperty().addListener((obs, o, n) -> rechercherElementsAjout(n, comboTypeAjout.getValue()));
        comboTypeAjout.setOnAction(e -> {
            configurerColonnesTableau(comboTypeAjout.getValue());
            rechercherElementsAjout(txtRechercheAjout.getText(), comboTypeAjout.getValue());
        });
    }

    @SuppressWarnings("unchecked")
    private void configurerColonnesTableau(String type) {
        tableResultats.getColumns().clear();

        TableColumn<String[], String> colRef = new TableColumn<>("Réf");
        colRef.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colRef.setPrefWidth(90);

        TableColumn<String[], String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        colNom.setPrefWidth(180);

        TableColumn<String[], String> colInfo = new TableColumn<>(type.equals("Pièce") ? "Catégorie" : "Détails");
        colInfo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        colInfo.setPrefWidth(130);

        tableResultats.getColumns().addAll(colRef, colNom, colInfo);
    }

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
        }
    }

    private void gererAjoutElement() {
        String type = comboTypeAjout.getValue();
        String recherche = txtRechercheAjout.getText().trim();
        
        int quantite;
        try {
            quantite = Integer.parseInt(txtQuantiteAjout.getText().trim());
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            messageLabel.setText("La quantité doit être un nombre valide.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Cas 1 : Ajout depuis la base de données
        if (!referenceSelectionnee.isEmpty()) {
            if (type.equals("Pièce") && pieceService != null) {
                Piece piece = pieceService.rechercherPiece(referenceSelectionnee);
                if (piece != null) {
                    piecesTemporaires.add(new PieceQuantite(piece, quantite, false, null));
                }
            } else if (type.equals("Figurine") && boiteService != null) {
                Figurine fig = boiteService.rechercherFigurine(referenceSelectionnee);
                if (fig != null) {
                    figurinesTemporaires.add(new FigurineQuantite(fig, quantite));
                }
            }
        } 
        // Cas 2 : Ajout personnalisé si rien n'est sélectionné
        else if (!recherche.isEmpty()) {
            if (type.equals("Pièce")) {
                String refGeneree = "PERSO-P-" + Math.abs(recherche.hashCode());
                piecesTemporaires.add(new PieceQuantite(new Piece(refGeneree, recherche, null, null), quantite, false, null));
            } else {
                String refGeneree = "PERSO-F-" + Math.abs(recherche.hashCode());
                figurinesTemporaires.add(new FigurineQuantite(new Figurine(refGeneree, recherche, 0, ""), quantite));
            }
        } else {
            messageLabel.setText("Veuillez sélectionner ou chercher un élément.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        rafraichirListeVisuelle();
        txtRechercheAjout.clear();
        txtQuantiteAjout.setText("1");
        messageLabel.setText("");
        referenceSelectionnee = "";
        lblSelection.setText("Aucune sélection");
    }

    private void gererSuppressionElement() {
        int index = listeInventaireVisuel.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            if (index < piecesTemporaires.size()) {
                piecesTemporaires.remove(index);
            } else {
                figurinesTemporaires.remove(index - piecesTemporaires.size());
            }
            rafraichirListeVisuelle();
        }
    }

    private void rafraichirListeVisuelle() {
        listeInventaireVisuel.getItems().clear();
        for (PieceQuantite pq : piecesTemporaires) {
            String origine = pq.getPiece().getNumero().startsWith("PERSO") ? " (Perso)" : "";
            listeInventaireVisuel.getItems().add(pq.getQuantite() + "x Pièce" + origine + " : " + pq.getPiece().getNom());
        }
        for (FigurineQuantite fq : figurinesTemporaires) {
            String origine = fq.getFigurine().getIdFigurine().startsWith("PERSO") ? " (Perso)" : "";
            listeInventaireVisuel.getItems().add(fq.getQuantite() + "x Figurine" + origine + " : " + fq.getFigurine().getNom());
        }
    }

    private void chargerThemes() {
        if (themeService != null) {
            List<Theme> themes = themeService.listerThemes();
            themeComboBox.setItems(FXCollections.observableArrayList(themes));
        }
    }

    private void chargerApercu() {
        String url = imageField.getText().trim();
        if (!url.isEmpty()) {
            try { apercuImageView.setImage(new Image(url, true)); } 
            catch (Exception ex) { System.err.println("Erreur URL image"); }
        }
    }

    private void handleCreerBoitePerso() {
        String numero = numeroField.getText().trim();
        String nom = nomField.getText().trim();
        String anneeStr = anneeField.getText().trim();
        String image = imageField.getText().trim();
        Theme themeSelectionne = themeComboBox.getValue();

        if (numero.isEmpty() || nom.isEmpty() || anneeStr.isEmpty() || themeSelectionne == null) {
            messageLabel.setText("Erreur : Veuillez remplir référence, nom, année et thème.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (collectionService.obtenerItem(numero) != null) {
            messageLabel.setText("Erreur : Une boîte avec cette référence existe déjà.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            Boite nouvelleBoite = new Boite(numero, nom, annee, themeSelectionne, image.isEmpty() ? null : image);
            collectionService.ajouterBoitePersonnalisee(nouvelleBoite, EtatBoite.COMPLETE, piecesTemporaires, figurinesTemporaires);
            
            Alert alerte = new Alert(Alert.AlertType.INFORMATION);
            alerte.setTitle("Succès");
            alerte.setHeaderText(null);
            alerte.setContentText("Votre modèle et son inventaire ont été ajoutés à la collection !");
            alerte.showAndWait();

            if (actionApresCreation != null) actionApresCreation.run();
        } catch (NumberFormatException e) {
            messageLabel.setText("Erreur : L'année doit être un nombre.");
            messageLabel.setTextFill(Color.RED);
        }
    }
}