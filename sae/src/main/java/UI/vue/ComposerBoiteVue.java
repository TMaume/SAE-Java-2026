package UI.vue;

import App.*;
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
    private final Runnable actionApresCreation;

    // Champs Formulaire Boîte
    private TextField numeroField;
    private TextField nomField;
    private TextField anneeField;
    private ComboBox<Theme> themeComboBox;
    private TextField imageField;
    private ImageView apercuImageView;
    private Label messageLabel;

    // Champs Inventaire (Nouveau)
    private ComboBox<String> comboTypeItem;
    private TextField txtRefAjout;
    private TextField txtQteAjout;
    private ListView<String> listeInventaireVisuel;

    // Données Temporaires
    private final List<PieceQuantite> piecesTemporaires = new ArrayList<>();
    private final List<FigurineQuantite> figurinesTemporaires = new ArrayList<>();

    public ComposerBoiteVue(CollectionService collectionService, ThemeService themeService, PieceService pieceService, Runnable actionApresCreation) {
        this.collectionService = collectionService;
        this.themeService = themeService;
        this.pieceService = pieceService;
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


        // ================= ZONE DROITE : INVENTAIRE (NOUVEAU) =================
        VBox zoneInventaire = new VBox(15);
        zoneInventaire.getStyleClass().add("card");
        zoneInventaire.setPadding(new Insets(20));
        zoneInventaire.setPrefWidth(450);
        HBox.setHgrow(zoneInventaire, Priority.ALWAYS);

        Label titreInventaire = new Label("2. Inventaire (Optionnel)");
        titreInventaire.getStyleClass().add("subtitle-label");

        HBox barreAjout = new HBox(10);
        barreAjout.setAlignment(Pos.CENTER_LEFT);
        
        comboTypeItem = new ComboBox<>(FXCollections.observableArrayList("Pièce", "Figurine"));
        comboTypeItem.setValue("Pièce");
        comboTypeItem.setPrefWidth(100);

        txtRefAjout = new TextField();
        txtRefAjout.setPromptText("Réf/ID");
        txtRefAjout.setPrefWidth(100);

        txtQteAjout = new TextField("1");
        txtQteAjout.setPromptText("Qté");
        txtQteAjout.setPrefWidth(50);

        Button btnAjouterItem = new Button("Ajouter");
        btnAjouterItem.getStyleClass().add("button");
        btnAjouterItem.setOnAction(e -> ajouterItemInventaire());

        barreAjout.getChildren().addAll(comboTypeItem, txtRefAjout, txtQteAjout, btnAjouterItem);

        listeInventaireVisuel = new ListView<>();
        listeInventaireVisuel.setPrefHeight(200);

        // Bouton de validation globale
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button ajouterButton = new Button("Enregistrer la boîte personnalisée");
        ajouterButton.getStyleClass().add("btn-primary");
        ajouterButton.setMaxWidth(Double.MAX_VALUE);
        ajouterButton.setOnAction(e -> handleCreerBoitePerso());
        
        messageLabel = new Label();
        actionBox.getChildren().addAll(ajouterButton, messageLabel);

        zoneInventaire.getChildren().addAll(titreInventaire, barreAjout, listeInventaireVisuel, actionBox);

        // Ajout des deux zones au layout principal
        this.getChildren().addAll(zoneFormulaire, zoneInventaire);

        chargerThemes();
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
            try {
                apercuImageView.setImage(new Image(url, true));
            } catch (Exception ex) {
                System.err.println("Erreur URL image");
            }
        }
    }

    private void ajouterItemInventaire() {
        String ref = txtRefAjout.getText().trim();
        String qteStr = txtQteAjout.getText().trim();
        if (ref.isEmpty() || qteStr.isEmpty()) return;

        try {
            int qte = Integer.parseInt(qteStr);
            if (qte <= 0) return;

            if (comboTypeItem.getValue().equals("Pièce")) {
                Piece p = (pieceService != null) ? pieceService.rechercherPiece(ref) : null;
                if (p == null) p = new Piece(ref, "Pièce personnalisée", null, null); // Création à la volée si introuvable
                
                piecesTemporaires.add(new PieceQuantite(p, qte, false, null));
                listeInventaireVisuel.getItems().add(qte + "x Pièce : " + p.getNom() + " (" + ref + ")");
            } else {
                // Pour les figurines, on crée un objet générique puisqu'il n'y a pas de FigurineService direct ici
                Figurine f = new Figurine(ref, "Figurine personnalisée", 0, "");
                figurinesTemporaires.add(new FigurineQuantite(f, qte));
                listeInventaireVisuel.getItems().add(qte + "x Figurine : " + f.getNom() + " (" + ref + ")");
            }
            
            txtRefAjout.clear();
            txtQteAjout.setText("1");
        } catch (NumberFormatException ex) {
            messageLabel.setText("La quantité doit être un nombre.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void handleCreerBoitePerso() {
        String numero = numeroField.getText().trim();
        String nom = nomField.getText().trim();
        String anneeStr = anneeField.getText().trim();
        String image = imageField.getText().trim();
        Theme themeSelectionne = themeComboBox.getValue();

        if (numero.isEmpty() || nom.isEmpty() || anneeStr.isEmpty() || themeSelectionne == null) {
            messageLabel.setText("Erreur : Veuillez remplir la référence, le nom, l'année et le thème.");
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
            
            // On sauvegarde la boîte ET ses listes d'inventaire
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