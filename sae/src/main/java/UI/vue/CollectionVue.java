package UI.vue;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Vue pure (Interface Graphique) pour la collection.
 * Ne contient aucune logique métier.
 */
public class CollectionVue {
    private BorderPane root;
    public FlowPane conteneurGrille; // Accessible pour le Controller
    public ComboBox<String> comboFiltreEtat; // Accessible pour le Controller
    public Label lblInfosTotal; // Accessible pour le Controller

    public CollectionVue() {
        initialiserInterface();
    }

    public Node getVue() {
        return root;
    }

    private void initialiserInterface() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        // EN-TÊTE
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label lblTitre = new Label("Ma Collection Personnelle");
        lblTitre.getStyleClass().add("titre-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblInfosTotal = new Label();
        lblInfosTotal.getStyleClass().add("soustitre-label");

        comboFiltreEtat = new ComboBox<>(FXCollections.observableArrayList("Toutes", "Complètes", "Incomplètes"));
        comboFiltreEtat.setValue("Toutes");

        header.getChildren().addAll(lblTitre, spacer, lblInfosTotal, comboFiltreEtat);
        
        VBox enteteGlobal = new VBox(15);
        enteteGlobal.setPadding(new Insets(0, 0, 20, 0));
        enteteGlobal.getChildren().add(header);
        
        root.setTop(enteteGlobal);

        // ZONE D'AFFICHAGE
        conteneurGrille = new FlowPane(20, 20);
        conteneurGrille.setPadding(new Insets(10));
        conteneurGrille.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(conteneurGrille);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        root.setCenter(scrollPane);
    }
}