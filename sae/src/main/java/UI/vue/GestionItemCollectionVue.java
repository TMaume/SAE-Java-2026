package UI.vue;

import App.CollectionItem;
import App.CollectionService;
import App.EtatBoite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.Optional;

public class GestionItemCollectionVue extends BorderPane {
    private final CollectionItem item;
    private final CollectionService collectionService;
    private final Runnable actionRetour;
    
    private CheckBox chkConstruite;
    private CheckBox chkComplete;
    private TextField txtImagePerso;
    private ImageView imageViewApercu;

    public GestionItemCollectionVue(CollectionItem item, CollectionService collectionService, Runnable actionRetour, Runnable actionVoirDetails, Runnable actionModifierPerso) {
        this.item = item;
        this.collectionService = collectionService;
        this.actionRetour = actionRetour;

        setPadding(new Insets(30));
        setStyle("-fx-background-color: transparent;");

        // --- EN-TÊTE ---
        HBox entete = new HBox(20);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(0, 0, 30, 0));

        Button btnRetour = new Button("◄ Retour à ma collection");
        btnRetour.getStyleClass().add("button");
        btnRetour.setOnAction(e -> actionRetour.run());

        Label lblTitre = new Label("Suivi de ma boîte N° " + item.getBoite().getNumero());
        lblTitre.getStyleClass().add("title-label");
        entete.getChildren().addAll(btnRetour, lblTitre);
        setTop(entete);

        // --- CONTENU PRINCIPAL ---
        HBox contenu = new HBox(30);
        contenu.setAlignment(Pos.TOP_LEFT);

        // Zone Gauche : Image de la boîte ou photo personnalisée
        VBox zoneImage = new VBox(15);
        zoneImage.setAlignment(Pos.CENTER);
        zoneImage.setPadding(new Insets(15));
        zoneImage.getStyleClass().add("card");
        zoneImage.setPrefWidth(350);
        zoneImage.setPrefHeight(350);

        imageViewApercu = new ImageView();
        imageViewApercu.setFitWidth(310);
        imageViewApercu.setFitHeight(270);
        imageViewApercu.setPreserveRatio(true);
        rafraichirApercuImage();
        
        Button btnDetails = new Button("🔍 Voir le contenu");
        btnDetails.getStyleClass().add("button");
        btnDetails.setMaxWidth(Double.MAX_VALUE);
        
        btnDetails.setOnAction(e -> {
            if (item.isBoitePersonnalisee()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Inventaire Personnalisé");
                alert.setHeaderText("Contenu de : " + item.getBoite().getNom());
                
                StringBuilder sb = new StringBuilder();
                sb.append("--- PIÈCES ---\n");
                for (App.PieceQuantite pq : item.getPiecesPerso()) {
                    sb.append(pq.getQuantite()).append("x ").append(pq.getPiece().getNom()).append(" (").append(pq.getPiece().getNumero()).append(")\n");
                }
                sb.append("\n--- FIGURINES ---\n");
                for (App.FigurineQuantite fq : item.getFigurinesPerso()) {
                    sb.append(fq.getQuantite()).append("x ").append(fq.getFigurine().getNom()).append(" (").append(fq.getFigurine().getIdFigurine()).append(")\n");
                }
                
                if (item.getPiecesPerso().isEmpty() && item.getFigurinesPerso().isEmpty()) {
                    sb.append("Aucun inventaire défini pour cette boîte.");
                }
                
                TextArea textArea = new TextArea(sb.toString());
                textArea.setEditable(false);
                alert.getDialogPane().setContent(textArea);
                alert.showAndWait();
            } else {
                actionVoirDetails.run();
            }
        });

        if (item.isBoitePersonnalisee()) {
            Button btnModifier = new Button("✏️ Modifier ma création");
            btnModifier.getStyleClass().add("button");
            btnModifier.setMaxWidth(Double.MAX_VALUE);
            btnModifier.setOnAction(e -> actionModifierPerso.run());
            
            zoneImage.getChildren().addAll(imageViewApercu, btnDetails, btnModifier);
        } else {
            zoneImage.getChildren().addAll(imageViewApercu, btnDetails);
        }

        // Zone Droite : Paramètres de suivi
        VBox zoneFormulaire = new VBox(25);
        zoneFormulaire.setPadding(new Insets(20));
        zoneFormulaire.getStyleClass().add("card");
        HBox.setHgrow(zoneFormulaire, Priority.ALWAYS);

        Label lblNomBoite = new Label(item.getBoite().getNom());
        lblNomBoite.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 1. Statut de Construction
        VBox blocConstruction = new VBox(8);
        Label lblConstruite = new Label("Statut de montage :");
        lblConstruite.getStyleClass().add("subtitle-label");
        chkConstruite = new CheckBox("Construction terminée");
        chkConstruite.setSelected(item.isConstruite());
        blocConstruction.getChildren().addAll(lblConstruite, chkConstruite);

        // 2. État de l'inventaire (Complet / Incomplet)
        VBox blocInventaire = new VBox(8);
        Label lblInventaire = new Label("État de l'inventaire :");
        lblInventaire.getStyleClass().add("subtitle-label");
        chkComplete = new CheckBox("La boîte est complète (aucune pièce manquante)");
        chkComplete.setSelected(item.getEtat() == EtatBoite.COMPLETE);
        blocInventaire.getChildren().addAll(lblInventaire, chkComplete);

        // 3. Photo du modèle réel
        VBox blocPhoto = new VBox(8);
        Label lblPhoto = new Label("Associer une photo de votre modèle :");
        lblPhoto.getStyleClass().add("subtitle-label");
        
        HBox lignePhoto = new HBox(10);
        txtImagePerso = new TextField(item.getImagePersonnelle() != null ? item.getImagePersonnelle() : "");
        txtImagePerso.setPromptText("Chemin du fichier ou URL de l'image...");
        HBox.setHgrow(txtImagePerso, Priority.ALWAYS);
        
        Button btnParcourir = new Button("Parcourir...");
        btnParcourir.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner votre photo");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            Stage stageActuel = (Stage) this.getScene().getWindow();
            File fichierSelectionne = fileChooser.showOpenDialog(stageActuel);
            
            if (fichierSelectionne != null) {
                String uriFichier = fichierSelectionne.toURI().toString();
                txtImagePerso.setText(uriFichier);
                try {
                    imageViewApercu.setImage(new Image(uriFichier));
                } catch (Exception ex) {
                    System.err.println("Erreur lors de l'aperçu rapide : " + ex.getMessage());
                }
            }
        });
        lignePhoto.getChildren().addAll(txtImagePerso, btnParcourir);
        blocPhoto.getChildren().addAll(lblPhoto, lignePhoto);

        // Boutons d'actions finales (Enregistrer et Supprimer)
        VBox zoneBoutonsAction = new VBox(10);

        Button btnEnregistrer = new Button("Enregistrer les modifications de suivi");
        btnEnregistrer.getStyleClass().add("btn-primary");
        btnEnregistrer.setMaxWidth(Double.MAX_VALUE);
        btnEnregistrer.setOnAction(e -> gererSauvegarde());

        Button btnSupprimer = new Button("🗑️ Supprimer de ma collection");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSupprimer.setMaxWidth(Double.MAX_VALUE);
        btnSupprimer.setOnAction(e -> gererSuppression());

        zoneBoutonsAction.getChildren().addAll(btnEnregistrer, btnSupprimer);

        zoneFormulaire.getChildren().addAll(lblNomBoite, blocConstruction, blocInventaire, blocPhoto, zoneBoutonsAction);
        
        contenu.getChildren().addAll(zoneImage, zoneFormulaire);
        setCenter(contenu);
    }

    private void rafraichirApercuImage() {
        String urlACharger = (item.getImagePersonnelle() != null && !item.getImagePersonnelle().isBlank())
                ? item.getImagePersonnelle()
                : item.getBoite().getImageBoite();

        if (urlACharger != null && !urlACharger.isBlank()) {
            try {
                imageViewApercu.setImage(new Image(urlACharger, true));
            } catch (Exception e) {
                System.err.println("Impossible de charger l'image : " + urlACharger);
            }
        }
    }

    private void gererSauvegarde() {
        item.setConstruite(chkConstruite.isSelected());
        item.setEtat(chkComplete.isSelected() ? EtatBoite.COMPLETE : EtatBoite.INCOMPLETE);
        
        String cheminPhoto = txtImagePerso.getText().trim();
        item.setImagePersonnelle(cheminPhoto.isEmpty() ? null : cheminPhoto);

        try {
            collectionService.mettreAJourItem(item);
            
            Alert alerte = new Alert(Alert.AlertType.INFORMATION);
            alerte.setTitle("Mise à jour réussie");
            alerte.setHeaderText(null);
            alerte.setContentText("L'état de votre boîte a été mis à jour avec succès !");
            alerte.showAndWait();
            
            rafraichirApercuImage();
        } catch (Exception ex) {
            Alert alerte = new Alert(Alert.AlertType.ERROR);
            alerte.setTitle("Erreur");
            alerte.setHeaderText("Échec de la sauvegarde");
            alerte.setContentText("Une erreur est survenue : " + ex.getMessage());
            alerte.showAndWait();
        }
    }

    // --- NOUVELLE MÉTHODE : Gestion de la suppression avec double sécurité ---
    private void gererSuppression() {
        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Confirmation de suppression");
        alert1.setHeaderText("Retirer cette boîte ?");
        alert1.setContentText("Êtes-vous sûr de vouloir supprimer la boîte \"" + item.getBoite().getNom() + "\" de votre collection ?");

        Optional<ButtonType> result1 = alert1.showAndWait();
        if (result1.isPresent() && result1.get() == ButtonType.OK) {
            
            // Si c'est une boîte personnalisée, on affiche un DEUXIÈME avertissement sévère
            if (item.isBoitePersonnalisee()) {
                Alert alert2 = new Alert(Alert.AlertType.WARNING);
                alert2.setTitle("Attention : Boîte personnalisée");
                alert2.setHeaderText("Suppression DÉFINITIVE");
                alert2.setContentText("Ceci est une boîte que vous avez créée vous-même.\nLa supprimer l'effacera DÉFINITIVEMENT de vos données (modèle et inventaire).\n\nVoulez-vous vraiment continuer ?");
                
                ButtonType btnOuiDefinitif = new ButtonType("Oui, supprimer définitivement", ButtonBar.ButtonData.OK_DONE);
                ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert2.getButtonTypes().setAll(btnOuiDefinitif, btnAnnuler);

                Optional<ButtonType> result2 = alert2.showAndWait();
                if (result2.isPresent() && result2.get() == btnOuiDefinitif) {
                    executerSuppression();
                }
            } else {
                // Boîte normale du catalogue
                executerSuppression();
            }
        }
    }

    private void executerSuppression() {
        collectionService.supprimerItem(item.getBoite().getNumero());
        
        Alert alerteInfo = new Alert(Alert.AlertType.INFORMATION);
        alerteInfo.setTitle("Suppression");
        alerteInfo.setHeaderText(null);
        alerteInfo.setContentText("La boîte a été retirée de votre collection.");
        alerteInfo.showAndWait();
        
        actionRetour.run(); // Retourne automatiquement sur l'onglet Collection
    }
}