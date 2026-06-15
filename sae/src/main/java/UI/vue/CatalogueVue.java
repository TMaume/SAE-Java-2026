package UI.vue;

import App.Boite;
import App.BoiteService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.List;

public class CatalogueVue {
    private final BoiteService boiteService;
    private int pageCourante = 1;
    private final int taillePage = 20;
    private boolean estVueGrille = true; 

    private BorderPane root;
    private ScrollPane scrollPane;
    private FlowPane conteneurGrille;
    private VBox conteneurListe;
    
    private Label lblPagination;
    private Button btnPrecedent;
    private Button btnSuivant;
    private Label lblInfosTotal;

    public CatalogueVue(BoiteService boiteService) {
        this.boiteService = boiteService;
        initialiserInterface();
        chargerPage();
    }

    public Node getVue() {
        return root;
    }

    private void initialiserInterface() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label lblTitre = new Label("Catalogue des Boîtes LEGO");
        lblTitre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblInfosTotal = new Label();
        lblInfosTotal.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        Button btnChangerVue = new Button("Affichage : Grille");
        btnChangerVue.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 4; -fx-cursor: hand;");
        btnChangerVue.setOnAction(e -> {
            estVueGrille = !estVueGrille;
            btnChangerVue.setText(estVueGrille ? "Affichage : Grille" : "Affichage : Liste");
            chargerPage(); 
        });

        header.getChildren().addAll(lblTitre, spacer, lblInfosTotal, btnChangerVue);
        root.setTop(header);

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        conteneurGrille = new FlowPane(15, 15);
        conteneurGrille.setPadding(new Insets(10));
        conteneurGrille.setAlignment(Pos.TOP_LEFT);

        conteneurListe = new VBox(10);
        conteneurListe.setPadding(new Insets(10));

        root.setCenter(scrollPane);

        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));

        btnPrecedent = new Button("◄ Précédent");
        btnPrecedent.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnPrecedent.setOnAction(e -> {
            if (pageCourante > 1) {
                pageCourante--;
                chargerPage();
            }
        });

        lblPagination = new Label("Page 1");
        lblPagination.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        btnSuivant = new Button("Suivant ►");
        btnSuivant.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnSuivant.setOnAction(e -> {
            pageCourante++;
            chargerPage();
        });

        footer.getChildren().addAll(btnPrecedent, lblPagination, btnSuivant);
        root.setBottom(footer);
    }

    private void chargerPage() {
        if (boiteService == null) return; 

        // 1. Calculs globaux
        int totalBoites = boiteService.obtenirNombreTotalBoites();
        int totalPages = (int) Math.ceil((double) totalBoites / taillePage);
        if (totalPages == 0) totalPages = 1;

        // Sécurité si on dépasse
        if (pageCourante > totalPages) pageCourante = totalPages;

        // 2. Mise à jour de l'UI (Textes et Boutons)
        lblInfosTotal.setText(totalBoites + " boîtes trouvées");
        lblPagination.setText("Page " + pageCourante + " sur " + totalPages);
        btnPrecedent.setDisable(pageCourante <= 1);
        btnSuivant.setDisable(pageCourante >= totalPages);

        // 3. Récupération des données paginées en Base de Données
        List<Boite> boites = boiteService.listerBoitesPaginees(pageCourante, taillePage);

        // 4. Affichage selon le mode choisi
        if (estVueGrille) {
            conteneurGrille.getChildren().clear();
            for (Boite b : boites) {
                conteneurGrille.getChildren().add(creerCarteBoite(b));
            }
            scrollPane.setContent(conteneurGrille);
        } else {
            conteneurListe.getChildren().clear();
            for (Boite b : boites) {
                conteneurListe.getChildren().add(creerLigneBoite(b));
            }
            scrollPane.setContent(conteneurListe);
        }
    }

    /**
     * Crée un affichage de type "Carte" (Vue Grille)
     */
    private VBox creerCarteBoite(Boite b) {
        VBox carte = new VBox(8);
        carte.setPadding(new Insets(15));
        carte.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        carte.setPrefWidth(220);
        carte.setMinHeight(160);

        Label lblNumero = new Label("#" + b.getNumero());
        lblNumero.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label lblNom = new Label(b.getNom());
        lblNom.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblNom.setWrapText(true);

        String nomTheme = (b.getTheme() != null) ? b.getTheme().getNom() : "Inconnu";
        Label lblTheme = new Label("Thème : " + nomTheme);
        lblTheme.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        String strAnnee = (b.getAnnee() != null) ? String.valueOf(b.getAnnee()) : "N/A";
        String strPieces = (b.getNbPieces() != null) ? String.valueOf(b.getNbPieces()) : "?";
        Label lblDetails = new Label(strAnnee + " • " + strPieces + " pièces");
        lblDetails.setStyle("-fx-text-fill: #34495e; -fx-font-size: 13px;");

        carte.getChildren().addAll(lblNumero, lblNom, lblTheme, lblDetails);
        return carte;
    }

    /**
     * Crée un affichage de type "Ligne" (Vue Liste)
     */
    private HBox creerLigneBoite(Boite b) {
        HBox ligne = new HBox(20);
        ligne.setPadding(new Insets(15));
        ligne.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 5; -fx-background-radius: 5;");
        ligne.setAlignment(Pos.CENTER_LEFT);

        Label lblNumero = new Label("#" + b.getNumero());
        lblNumero.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-pref-width: 80px;");

        Label lblNom = new Label(b.getNom());
        lblNom.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-pref-width: 300px;");

        String nomTheme = (b.getTheme() != null) ? b.getTheme().getNom() : "Inconnu";
        Label lblTheme = new Label(nomTheme);
        lblTheme.setStyle("-fx-text-fill: #7f8c8d; -fx-pref-width: 200px;");

        String strAnnee = (b.getAnnee() != null) ? String.valueOf(b.getAnnee()) : "N/A";
        String strPieces = (b.getNbPieces() != null) ? String.valueOf(b.getNbPieces()) : "?";
        Label lblDetails = new Label(strAnnee + "  |  " + strPieces + " pcs");
        lblDetails.setStyle("-fx-text-fill: #34495e; -fx-alignment: center-right;");

        ligne.getChildren().addAll(lblNumero, lblNom, lblTheme, lblDetails);
        return ligne;
    }
}