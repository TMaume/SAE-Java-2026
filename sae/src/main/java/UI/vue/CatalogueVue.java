package UI.vue;

import App.Boite;
import App.BoiteService;
import App.Theme;
import App.ThemeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Vue affichant le catalogue des boîtes LEGO avec pagination et filtres.
 */
public class CatalogueVue {
    private final BoiteService boiteService;
    private final ThemeService themeService;
    private final Consumer<Boite> actionClicBoite;
    
    private int pageCourante = 1;
    private final int taillePage = 20;
    private boolean estVueGrille = true;

    private BorderPane root;
    private ScrollPane scrollPane;
    private FlowPane conteneurGrille;
    private VBox conteneurListe;

    private TextField txtRecherche;
    private ComboBox<Theme> comboTheme;
    private TextField txtPageExacte;
    private Label lblPagination;
    private Button btnPrecedent;
    private Button btnSuivant;
    private Label lblInfosTotal;
    private Button btnAjouterBoiteDansCollection;

    /**
     * Construit la vue du catalogue.
     *
     * @param boiteService le service de gestion des boîtes
     * @param themeService le service de gestion des thèmes
     * @param actionClicBoite l'action déclenchée lors du clic sur une boîte
     */
    public CatalogueVue(BoiteService boiteService, ThemeService themeService, Consumer<Boite> actionClicBoite) {
        this.boiteService = boiteService;
        this.themeService = themeService;
        this.actionClicBoite = actionClicBoite;
        initialiserInterface();
        chargerPage();
    }

    /**
     * Retourne le composant racine de la vue.
     *
     * @return le BorderPane principal
     */
    public Node getVue() {
        return root;
    }

    /**
     * Initialise l'interface globale en assemblant les différentes zones.
     */
    private void initialiserInterface() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        VBox enteteGlobal = new VBox(15);
        enteteGlobal.setPadding(new Insets(0, 0, 20, 0));
        enteteGlobal.getChildren().addAll(creerEnTete(), creerBarreFiltres());
        
        root.setTop(enteteGlobal);
        root.setCenter(creerZoneAffichage());
        root.setBottom(creerPiedDePage());
    }

    /**
     * Crée la partie supérieure contenant le titre et le bouton de changement de vue.
     *
     * @return un HBox contenant l'en-tête
     */
    private HBox creerEnTete() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitre = new Label("Catalogue des Boîtes LEGO");
        lblTitre.getStyleClass().add("titre-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblInfosTotal = new Label();
        lblInfosTotal.getStyleClass().add("soustitre-label");

        Button btnChangerVue = new Button("Affichage : Grille");
        btnChangerVue.getStyleClass().add("button");
        btnChangerVue.setOnAction(e -> basculerVue(btnChangerVue));

        header.getChildren().addAll(lblTitre, spacer, lblInfosTotal, btnChangerVue);
        return header;
    }

    /**
     * Bascule entre l'affichage en grille et en liste.
     *
     * @param bouton le bouton ayant déclenché l'action
     */
    private void basculerVue(Button bouton) {
        estVueGrille = !estVueGrille;
        bouton.setText(estVueGrille ? "Affichage : Grille" : "Affichage : Liste");
        chargerPage();
    }

    /**
     * Crée la barre de recherche et les filtres.
     *
     * @return un HBox contenant les filtres
     */
    private HBox creerBarreFiltres() {
        HBox barreFiltres = new HBox(10);
        barreFiltres.setAlignment(Pos.CENTER_LEFT);

        txtRecherche = new TextField();
        txtRecherche.setPromptText("Rechercher par nom...");
        txtRecherche.setPrefWidth(250);
        txtRecherche.setOnAction(e -> appliquerFiltres());

        comboTheme = new ComboBox<>();
        comboTheme.setPromptText("Tous les thèmes");
        comboTheme.setPrefWidth(200);
        comboTheme.getItems().add(null);
        
        if (themeService != null) {
            comboTheme.getItems().addAll(themeService.listerThemes());
        }

        Button btnFiltrer = new Button("Filtrer");
        btnFiltrer.getStyleClass().add("btn-primary");
        btnFiltrer.setOnAction(e -> appliquerFiltres());

        Button btnReinitialiser = new Button("Réinitialiser");
        btnReinitialiser.getStyleClass().add("btn-danger");
        btnReinitialiser.setOnAction(e -> reinitialiserFiltres());

        barreFiltres.getChildren().addAll(txtRecherche, comboTheme, btnFiltrer, btnReinitialiser);
        return barreFiltres;
    }

    /**
     * Applique les filtres et retourne à la première page.
     */
    private void appliquerFiltres() {
        pageCourante = 1;
        chargerPage();
    }

    /**
     * Efface les filtres et retourne à la première page.
     */
    private void reinitialiserFiltres() {
        txtRecherche.clear();
        comboTheme.setValue(null);
        pageCourante = 1;
        chargerPage();
    }

    /**
     * Crée la zone centrale avec barre de défilement contenant la grille ou la liste.
     *
     * @return un ScrollPane configuré
     */
    private ScrollPane creerZoneAffichage() {
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        conteneurGrille = new FlowPane(20, 20);
        conteneurGrille.setPadding(new Insets(10));
        conteneurGrille.setAlignment(Pos.TOP_LEFT);

        conteneurListe = new VBox(15);
        conteneurListe.setPadding(new Insets(10));

        return scrollPane;
    }

    /**
     * Crée le pied de page avec les contrôles de pagination.
     *
     * @return un HBox contenant la pagination
     */
    private HBox creerPiedDePage() {
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));

        btnPrecedent = new Button("◄ Précédent");
        btnPrecedent.getStyleClass().add("button");
        btnPrecedent.setOnAction(e -> pagePrecedente());

        lblPagination = new Label("Page ");
        lblPagination.getStyleClass().add("label");

        txtPageExacte = new TextField();
        txtPageExacte.setPrefWidth(50);
        txtPageExacte.setAlignment(Pos.CENTER);
        txtPageExacte.setOnAction(e -> allerAPageExacte());

        Label lblSurTotal = new Label();
        lblSurTotal.getStyleClass().add("label");

        btnSuivant = new Button("Suivant ►");
        btnSuivant.getStyleClass().add("button");
        btnSuivant.setOnAction(e -> pageSuivante());

        footer.getChildren().addAll(btnPrecedent, lblPagination, txtPageExacte, lblSurTotal, btnSuivant);
        return footer;
    }

    /**
     * Recule d'une page si possible.
     */
    private void pagePrecedente() {
        if (pageCourante > 1) {
            pageCourante--;
            chargerPage();
        }
    }

    /**
     * Avance d'une page.
     */
    private void pageSuivante() {
        pageCourante++;
        chargerPage();
    }

    /**
     * Tente de naviguer vers la page saisie dans le champ texte.
     */
    private void allerAPageExacte() {
        try {
            int pageDemandee = Integer.parseInt(txtPageExacte.getText());
            pageCourante = pageDemandee;
            chargerPage();
        } catch (NumberFormatException ex) {
            chargerPage();
        }
    }

    /**
     * Récupère les données depuis le service et met à jour l'affichage.
     */
    private void chargerPage() {
        if (boiteService == null) {
            return;
        }

        String recherche = txtRecherche.getText();
        Theme themeSelectionne = comboTheme.getValue();
        Integer idTheme = (themeSelectionne != null) ? themeSelectionne.getIdTheme() : null;

        int totalBoites = boiteService.obtenirNombreTotalBoitesFiltrees(recherche, idTheme);
        int totalPages = (int) Math.ceil((double) totalBoites / taillePage);
        
        if (totalPages == 0) {
            totalPages = 1;
        }

        if (pageCourante > totalPages) {
            pageCourante = totalPages;
        }
        
        if (pageCourante < 1) {
            pageCourante = 1;
        }

        mettreAJourTextesPagination(totalBoites, totalPages);

        List<Boite> boites = boiteService.listerBoitesFiltreesPaginees(recherche, idTheme, pageCourante, taillePage);

        if (estVueGrille) {
            afficherVueGrille(boites);
        } else {
            afficherVueListe(boites);
        }
    }

    /**
     * Met à jour les étiquettes et boutons de la pagination.
     *
     * @param totalBoites le nombre total de boîtes trouvées
     * @param totalPages le nombre total de pages
     */
    private void mettreAJourTextesPagination(int totalBoites, int totalPages) {
        lblInfosTotal.setText(totalBoites + " boîtes trouvées");
        txtPageExacte.setText(String.valueOf(pageCourante));

        HBox footer = (HBox) root.getBottom();
        Label lblSurTotal = (Label) footer.getChildren().get(3);
        lblSurTotal.setText(" sur " + totalPages);

        btnPrecedent.setDisable(pageCourante <= 1);
        btnSuivant.setDisable(pageCourante >= totalPages);
    }

    /**
     * Affiche la liste des boîtes sous forme de cartes.
     *
     * @param boites la liste des boîtes à afficher
     */
    private void afficherVueGrille(List<Boite> boites) {
        conteneurGrille.getChildren().clear();
        for (Boite b : boites) {
            conteneurGrille.getChildren().add(creerCarteBoite(b));
        }
        scrollPane.setContent(conteneurGrille);
    }

    /**
     * Affiche la liste des boîtes sous forme de liste détaillée.
     *
     * @param boites la liste des boîtes à afficher
     */
    private void afficherVueListe(List<Boite> boites) {
        conteneurListe.getChildren().clear();
        for (Boite b : boites) {
            conteneurListe.getChildren().add(creerLigneBoite(b));
        }
        scrollPane.setContent(conteneurListe);
    }

    /**
     * Construit l'interface graphique d'une carte représentant une boîte.
     *
     * @param b la boîte à représenter
     * @return un VBox contenant la carte
     */
    private VBox creerCarteBoite(Boite b) {
        VBox carte = new VBox(10);
        carte.getStyleClass().add("carte");
        carte.setPadding(new Insets(15));
        carte.getStyleClass().add("carte-boite");
        carte.setPrefWidth(220);
        carte.setMinHeight(250);
        carte.setOnMouseClicked(e -> actionClicBoite.accept(b));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        String url = b.getImageBoite();
        if (url != null && !url.isBlank()) {
            Image image = new Image(url, true);
            imageView.setImage(image);
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

        String nomTheme = (b.getTheme() != null) ? b.getTheme().getNom() : "Inconnu";
        Label lblTheme = new Label("Thème : " + nomTheme);
        lblTheme.getStyleClass().add("label");

        String strAnnee = (b.getAnnee() != null) ? String.valueOf(b.getAnnee()) : "N/A";
        String strPieces = (b.getNbPieces() != null) ? String.valueOf(b.getNbPieces()) : "?";
        Label lblDetails = new Label(strAnnee + " • " + strPieces + " pièces");
        lblDetails.getStyleClass().add("label");

        btnAjouterBoiteDansCollection = new Button("Ajouter à ma collection");
        btnAjouterBoiteDansCollection.setOnAction(e -> {
            if (boiteService != null) {
                boiteService.ajouterBoiteDansCollection(b);
            }
        });

        carte.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblTheme, lblDetails, btnAjouterBoiteDansCollection);
        return carte;
    }

    /**
     * Construit l'interface graphique d'une ligne représentant une boîte.
     *
     * @param b la boîte à représenter
     * @return un HBox contenant la ligne
     */
    private HBox creerLigneBoite(Boite b) {
        HBox ligne = new HBox(20);
        ligne.setPadding(new Insets(10, 15, 10, 15));
        ligne.getStyleClass().add("ligne-boite");
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setOnMouseClicked(e -> actionClicBoite.accept(b));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(60);
        imageView.setFitHeight(45);
        imageView.setPreserveRatio(true);

        String url = b.getImageBoite();
        if (url != null && !url.isBlank()) {
            Image image = new Image(url, true);
            imageView.setImage(image);
        }
        
        VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefWidth(70);

        Label lblNumero = new Label("#" + b.getNumero());
        lblNumero.getStyleClass().add("label");

        Label lblNom = new Label(b.getNom());
        lblNom.getStyleClass().add("label");

        String nomTheme = (b.getTheme() != null) ? b.getTheme().getNom() : "Inconnu";
        Label lblTheme = new Label(nomTheme);
        lblTheme.getStyleClass().add("label");

        String strAnnee = (b.getAnnee() != null) ? String.valueOf(b.getAnnee()) : "N/A";
        String strPieces = (b.getNbPieces() != null) ? String.valueOf(b.getNbPieces()) : "?";
        Label lblDetails = new Label(strAnnee + "  |  " + strPieces + " pcs");
        lblDetails.getStyleClass().add("label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnAjouterBoiteDansCollection = new Button("Ajouter à ma collection");
        btnAjouterBoiteDansCollection.setOnAction(e -> {
            if (boiteService != null) {
                boiteService.ajouterBoiteDansCollection(b);
            }
        });

        ligne.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblTheme, spacer, lblDetails, btnAjouterBoiteDansCollection);
        return ligne;
    }
}