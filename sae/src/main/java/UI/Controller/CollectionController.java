package UI.Controller;

import App.*;
import UI.vue.CollectionVue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contrôleur gérant la logique de la vue Collection personnelle.
 */
public class CollectionController {
    private final CollectionVue vue;
    private final CollectionService collectionService;
    private final Consumer<CollectionItem> actionClicBoite;

    public CollectionController(CollectionVue vue, CollectionService collectionService, Consumer<CollectionItem> actionClicBoite) {
        this.vue = vue;
        this.collectionService = collectionService;
        this.actionClicBoite = actionClicBoite;

        // On attache l'événement au menu déroulant de la vue
        this.vue.comboFiltreEtat.setOnAction(e -> chargerCollection());

        // On charge la collection au démarrage
        chargerCollection();
    }

    /**
     * Récupère les données depuis le JSON via le service et met à jour l'interface.
     */
    private void chargerCollection() {
        vue.conteneurGrille.getChildren().clear();
        
        List<CollectionItem> items = collectionService.listerCollection();
        String filtre = vue.comboFiltreEtat.getValue();

        // Filtrage
        if ("Complètes".equals(filtre)) {
            items = items.stream().filter(i -> i.getEtat() == EtatBoite.COMPLETE).collect(Collectors.toList());
        } else if ("Incomplètes".equals(filtre)) {
            items = items.stream().filter(i -> i.getEtat() == EtatBoite.INCOMPLETE).collect(Collectors.toList());
        }

        vue.lblInfosTotal.setText(items.size() + " boîtes dans cette vue");

        if (items.isEmpty()) {
            Label lblVide = new Label("Aucune boîte à afficher.");
            lblVide.getStyleClass().add("soustitre-label");
            vue.conteneurGrille.getChildren().add(lblVide);
            return;
        }

        // Création et injection des cartes
        for (CollectionItem item : items) {
            vue.conteneurGrille.getChildren().add(creerCarteCollection(item));
        }
    }

    /**
     * Fabrique la carte cliquable pour une boîte.
     */
    private VBox creerCarteCollection(CollectionItem item) {
        Boite b = item.getBoite();
        
        VBox carte = new VBox(10);
        carte.getStyleClass().addAll("carte", "carte-boite");
        carte.setPadding(new Insets(15));
        carte.setPrefWidth(220);
        carte.setMinHeight(250);
        
        // Au clic, on déclenche l'action passée par le Dashboard
        carte.setOnMouseClicked(e -> actionClicBoite.accept(item));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        String url = b.getImageBoite();
        if (url != null && !url.isBlank()) {
            imageView.setImage(new Image(url, true));
        }

        VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefHeight(140);

        Label lblNumero = new Label("#" + b.getNumero());
        lblNumero.getStyleClass().add("label");

        Label lblNom = new Label(b.getNom());
        lblNom.getStyleClass().add("label");
        lblNom.setWrapText(true);
        lblNom.setMaxHeight(40);

        String texteEtat = item.getEtat() == EtatBoite.COMPLETE ? "✔ Complète" : "❌ Incomplète";
        Label lblEtat = new Label(texteEtat);
        lblEtat.getStyleClass().add("label");
        lblEtat.setStyle(item.getEtat() == EtatBoite.COMPLETE ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : "-fx-text-fill: #e67e22; -fx-font-weight: bold;");

        String dateFormatee = item.getDateAjout() != null ? item.getDateAjout().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
        Label lblDate = new Label("Ajoutée le : " + dateFormatee);
        lblDate.getStyleClass().add("label");

        carte.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblEtat, lblDate);
        return carte;
    }
}