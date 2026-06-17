package UI.vue;

import App.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ModifierBoitePersoVue extends HBox {
    private final CollectionItem item;
    private final CollectionService collectionService;
    private final ThemeService themeService;
    private final PieceService pieceService;
    private final Runnable actionRetour;

    // Champs Formulaire Boîte
    private TextField numeroField;
    private TextField nomField;
    private TextField anneeField;
    private ComboBox<Theme> themeComboBox;
    private Label messageLabel;

    // Champs Inventaire
    private ComboBox<String> comboTypeItem;
    private TextField txtRefAjout;
    private TextField txtQteAjout;
    private ListView<String> listeInventaireVisuel;

    // Listes temporaires pour pouvoir annuler si on quitte sans sauvegarder
    private final List<PieceQuantite> piecesTemporaires = new ArrayList<>();
    private final List<FigurineQuantite> figurinesTemporaires = new ArrayList<>();

    public ModifierBoitePersoVue(CollectionItem item, CollectionService collectionService, ThemeService themeService, PieceService pieceService, Runnable actionRetour) {
        this.item = item;
        this.collectionService = collectionService;
        this.themeService = themeService;
        this.pieceService = pieceService;
        this.actionRetour = actionRetour;

        // Copie de l'inventaire actuel
        piecesTemporaires.addAll(item.getPiecesPerso());
        figurinesTemporaires.addAll(item.getFigurinesPerso());

        this.setSpacing(30);
        this.setPadding(new Insets(25));
        this.setAlignment(Pos.TOP_CENTER);

        // ================= ZONE GAUCHE : FORMULAIRE DE LA BOÎTE =================
        VBox zoneFormulaire = new VBox(20);
        zoneFormulaire.setMaxWidth(400);

        Button btnRetour = new Button("◄ Retour");
        btnRetour.getStyleClass().add("button");
        btnRetour.setOnAction(e -> actionRetour.run());

        Label titreLabel = new Label("1. Modifier les informations");
        titreLabel.getStyleClass().add("title-label");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(14);

        grid.add(new Label("Référence :"), 0, 0);
        numeroField = new TextField(item.getBoite().getNumero());
        numeroField.setDisable(true); // La référence ne peut pas être changée (clé primaire)
        grid.add(numeroField, 1, 0);

        grid.add(new Label("Nom :"), 0, 1);
        nomField = new TextField(item.getBoite().getNom());
        grid.add(nomField, 1, 1);

        grid.add(new Label("Année :"), 0, 2);
        anneeField = new TextField(item.getBoite().getAnnee() != null ? String.valueOf(item.getBoite().getAnnee()) : "");
        grid.add(anneeField, 1, 2);

        grid.add(new Label("Thème :"), 0, 3);
        themeComboBox = new ComboBox<>();
        grid.add(themeComboBox, 1, 3);

        zoneFormulaire.getChildren().addAll(btnRetour, titreLabel, grid);

        // ================= ZONE DROITE : INVENTAIRE =================
        VBox zoneInventaire = new VBox(15);
        zoneInventaire.getStyleClass().add("card");
        zoneInventaire.setPadding(new Insets(20));
        zoneInventaire.setPrefWidth(450);
        HBox.setHgrow(zoneInventaire, Priority.ALWAYS);

        Label titreInventaire = new Label("2. Gérer l'inventaire");
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
        rafraichirListe(); // Charge les éléments visuellement

        Button btnSupprimer = new Button("Supprimer l'élément sélectionné");
        btnSupprimer.getStyleClass().add("button");
        btnSupprimer.setOnAction(e -> supprimerItemInventaire());

        VBox actionBox = new VBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button btnSauvegarder = new Button("Enregistrer les modifications");
        btnSauvegarder.getStyleClass().add("btn-primary");
        btnSauvegarder.setMaxWidth(Double.MAX_VALUE);
        btnSauvegarder.setOnAction(e -> handleSauvegarder());
        
        messageLabel = new Label();
        actionBox.getChildren().addAll(btnSauvegarder, messageLabel);

        zoneInventaire.getChildren().addAll(titreInventaire, barreAjout, listeInventaireVisuel, btnSupprimer, actionBox);

        this.getChildren().addAll(zoneFormulaire, zoneInventaire);

        chargerThemes();
    }

    private void chargerThemes() {
        if (themeService != null) {
            List<Theme> themes = themeService.listerThemes();
            themeComboBox.setItems(FXCollections.observableArrayList(themes));
            
            // Pré-sélection du thème actuel
            if (item.getBoite().getTheme() != null) {
                for (Theme t : themes) {
                    if (t.getIdTheme() == item.getBoite().getTheme().getIdTheme()) {
                        themeComboBox.setValue(t);
                        break;
                    }
                }
            }
        }
    }

    private void rafraichirListe() {
        listeInventaireVisuel.getItems().clear();
        for (PieceQuantite pq : piecesTemporaires) {
            listeInventaireVisuel.getItems().add(pq.getQuantite() + "x Pièce : " + pq.getPiece().getNom() + " (" + pq.getPiece().getNumero() + ")");
        }
        for (FigurineQuantite fq : figurinesTemporaires) {
            listeInventaireVisuel.getItems().add(fq.getQuantite() + "x Figurine : " + fq.getFigurine().getNom() + " (" + fq.getFigurine().getIdFigurine() + ")");
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
                if (p == null) p = new Piece(ref, "Pièce personnalisée", null, null);
                piecesTemporaires.add(new PieceQuantite(p, qte, false, null));
            } else {
                Figurine f = new Figurine(ref, "Figurine personnalisée", 0, "");
                figurinesTemporaires.add(new FigurineQuantite(f, qte));
            }
            
            rafraichirListe();
            txtRefAjout.clear();
            txtQteAjout.setText("1");
        } catch (NumberFormatException ex) {
            messageLabel.setText("La quantité doit être un nombre.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void supprimerItemInventaire() {
        int index = listeInventaireVisuel.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            if (index < piecesTemporaires.size()) {
                piecesTemporaires.remove(index);
            } else {
                figurinesTemporaires.remove(index - piecesTemporaires.size());
            }
            rafraichirListe();
        }
    }

    private void handleSauvegarder() {
        String nom = nomField.getText().trim();
        String anneeStr = anneeField.getText().trim();
        Theme themeSelectionne = themeComboBox.getValue();

        if (nom.isEmpty() || anneeStr.isEmpty() || themeSelectionne == null) {
            messageLabel.setText("Veuillez remplir tous les champs obligatoires.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            
            // Appliquer les modifications au modèle (en mémoire)
            item.getBoite().setNom(nom);
            item.getBoite().setAnnee(annee);
            item.getBoite().setTheme(themeSelectionne);

            item.getPiecesPerso().clear();
            item.getPiecesPerso().addAll(piecesTemporaires);
            
            item.getFigurinesPerso().clear();
            item.getFigurinesPerso().addAll(figurinesTemporaires);

            // Appel de la méthode qui sauvegarde TOUTE la collection dans le JSON
            collectionService.mettreAJourItem(item);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Les modifications de votre boîte ont été enregistrées !");
            alert.showAndWait();

            if (actionRetour != null) actionRetour.run();
        } catch (NumberFormatException e) {
            messageLabel.setText("Erreur : L'année doit être un nombre entier.");
            messageLabel.setTextFill(Color.RED);
        }
    }
}