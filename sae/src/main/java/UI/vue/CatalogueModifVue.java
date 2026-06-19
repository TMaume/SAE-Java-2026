package UI.vue;

import App.Boite;
import App.BoiteService;
import App.CollectionService;
import App.Theme;
import App.ThemeService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Vue affichant le catalogue des boîtes LEGO (sans bouton collection) pour la modification admin.
 */
public class CatalogueModifVue {
    
    private final BoiteService boiteService;
    private final ThemeService themeService;
    private final Consumer<Boite> actionClicBoite;

    private int pageCourante = 1;
    private final int taillePage = 20;
    private boolean estVueGrille = true;

    // Composants principaux
    private BorderPane root = new BorderPane();
    private ScrollPane scrollPane = new ScrollPane();
    private FlowPane fpGrille = new FlowPane(20, 20);
    private VBox vbListe = new VBox(15);

    // En-tête
    private Label lblTitre = new Label("Catalogue des boites modifiables");
    private Label lblTotal = new Label();
    private Button btnVue = new Button("Affichage : Grille");

    // Barre de filtres et autocomplétion
    private TextField tfRecherche = new TextField();
    private ListView<String> listSuggest = new ListView<>();
    private PopupControl popupSuggest = new PopupControl();
    private ComboBox<Theme> comboTheme = new ComboBox<>();
    private Button btnFiltrer = new Button("Filtrer");
    private Button btnReset = new Button("Réinitialiser");

    // Pagination (Pied de page)
    private Button btnPrec = new Button("◄ Précédent");
    private TextField tfPage = new TextField();
    private Label lblPage = new Label("Page ");
    private Label lblSurTotal = new Label();
    private Button btnSuiv = new Button("Suivant ►");

    // Scheduler pour l'autocomplétion
    private static final int DELAI_SUGGESTION_MS = 250;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "suggestion-scheduler-modif");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> tacheSuggestion;

    public CatalogueModifVue(BoiteService boiteService, ThemeService themeService, CollectionService collectionService, Consumer<Boite> actionClicBoite) {
        this.boiteService = boiteService;
        this.themeService = themeService;
        this.actionClicBoite = actionClicBoite;
        
        initialiserInterface();
        chargerPage();
    }

    public Node getVue() {
        return root;
    }

    // -----------------------------------------------------------------------
    // CONSTRUCTION DE L'INTERFACE
    // -----------------------------------------------------------------------

    private void initialiserInterface() {
        root.getStyleClass().add("root");

        VBox enteteGlobal = new VBox(15);
        enteteGlobal.setPadding(new Insets(0, 0, 20, 0));
        enteteGlobal.getChildren().addAll(creerEnTete(), creerBarreFiltres());

        root.setTop(enteteGlobal);
        root.setCenter(creerZoneAffichage());
        root.setBottom(creerPiedDePage());
    }

    private HBox creerEnTete() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        lblTitre.getStyleClass().add("titre-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblTotal.getStyleClass().add("soustitre-label");

        btnVue.getStyleClass().add("button");
        btnVue.setOnAction(e -> basculerVue(btnVue));

        header.getChildren().addAll(lblTitre, spacer, lblTotal, btnVue);
        return header;
    }

    private void basculerVue(Button bouton) {
        estVueGrille = !estVueGrille;
        bouton.setText(estVueGrille ? "Affichage : Grille" : "Affichage : Liste");
        chargerPage();
    }

    private HBox creerBarreFiltres() {
        HBox barreFiltres = new HBox(10);
        barreFiltres.setAlignment(Pos.CENTER_LEFT);

        StackPane conteneurRecherche = creerChampRechercheAvecSuggestions();

        comboTheme.setPromptText("Tous les thèmes");
        comboTheme.setPrefWidth(200);
        comboTheme.getItems().add(null);
        if (themeService != null) {
            comboTheme.getItems().addAll(themeService.listerThemes());
        }

        btnFiltrer.getStyleClass().add("btn-primary");
        btnFiltrer.setOnAction(e -> appliquerFiltres());

        btnReset.getStyleClass().add("btn-danger");
        btnReset.setOnAction(e -> reinitialiserFiltres());

        barreFiltres.getChildren().addAll(conteneurRecherche, comboTheme, btnFiltrer, btnReset);
        return barreFiltres;
    }

    // -----------------------------------------------------------------------
    // AUTOCOMPLETION
    // -----------------------------------------------------------------------

    private StackPane creerChampRechercheAvecSuggestions() {
        tfRecherche.setPromptText("Rechercher par nom...");
        tfRecherche.setPrefWidth(250);

        listSuggest.setPrefWidth(250);
        listSuggest.setMaxHeight(180);
        listSuggest.getStyleClass().add("suggestions-list");

        popupSuggest.setAutoHide(true);
        popupSuggest.getScene().setRoot(listSuggest);

        listSuggest.setOnMouseClicked(e -> {
            String valeur = listSuggest.getSelectionModel().getSelectedItem();
            if (valeur != null) {
                tfRecherche.setText(valeur);
                popupSuggest.hide();
                appliquerFiltres();
            }
        });

        tfRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            if (tacheSuggestion != null) tacheSuggestion.cancel(false);

            if (nouveau == null || nouveau.trim().length() < 2) {
                popupSuggest.hide();
                return;
            }

            tacheSuggestion = scheduler.schedule(
                () -> Platform.runLater(() -> afficherSuggestions(nouveau.trim())),
                DELAI_SUGGESTION_MS, TimeUnit.MILLISECONDS
            );
        });

        tfRecherche.setOnAction(e -> {
            popupSuggest.hide();
            appliquerFiltres();
        });

        tfRecherche.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> popupSuggest.hide();
                case DOWN -> {
                    if (popupSuggest.isShowing()) {
                        listSuggest.requestFocus();
                        listSuggest.getSelectionModel().selectFirst();
                    }
                }
                default -> {}
            }
        });

        listSuggest.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> {
                    String valeur = listSuggest.getSelectionModel().getSelectedItem();
                    if (valeur != null) {
                        tfRecherche.setText(valeur);
                        popupSuggest.hide();
                        appliquerFiltres();
                    }
                }
                case ESCAPE -> popupSuggest.hide();
                default -> {}
            }
        });

        return new StackPane(tfRecherche);
    }

    private void afficherSuggestions(String motCle) {
        if (boiteService == null) return;

        List<Boite> resultats = boiteService.rechercherBoitesParNom(motCle);
        if (resultats.isEmpty()) {
            popupSuggest.hide();
            return;
        }

        listSuggest.getItems().clear();
        int max = Math.min(resultats.size(), 8);
        for (int i = 0; i < max; i++) {
            listSuggest.getItems().add(resultats.get(i).getNom());
        }

        if (!popupSuggest.isShowing() && tfRecherche.getScene() != null) {
            javafx.geometry.Bounds bounds = tfRecherche.localToScreen(tfRecherche.getBoundsInLocal());
            if (bounds != null) {
                popupSuggest.show(tfRecherche, bounds.getMinX(), bounds.getMaxY() + 2);
            }
        }
    }

    // -----------------------------------------------------------------------
    // FILTRES ET NAVIGATION
    // -----------------------------------------------------------------------

    private void appliquerFiltres() {
        pageCourante = 1;
        chargerPage();
    }

    private void reinitialiserFiltres() {
        tfRecherche.clear();
        comboTheme.setValue(null);
        popupSuggest.hide();
        pageCourante = 1;
        chargerPage();
    }

    private ScrollPane creerZoneAffichage() {
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        fpGrille.setPadding(new Insets(10));
        fpGrille.setAlignment(Pos.TOP_LEFT);

        vbListe.setPadding(new Insets(10));

        return scrollPane;
    }

    private HBox creerPiedDePage() {
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));

        btnPrec.getStyleClass().add("button");
        btnPrec.setOnAction(e -> pagePrecedente());

        lblPage.getStyleClass().add("label");

        tfPage.setPrefWidth(50);
        tfPage.setAlignment(Pos.CENTER);
        tfPage.setOnAction(e -> allerAPageExacte());

        lblSurTotal.getStyleClass().add("label");

        btnSuiv.getStyleClass().add("button");
        btnSuiv.setOnAction(e -> pageSuivante());

        footer.getChildren().addAll(btnPrec, lblPage, tfPage, lblSurTotal, btnSuiv);
        return footer;
    }

    private void pagePrecedente() {
        if (pageCourante > 1) {
            pageCourante--;
            chargerPage();
        }
    }

    private void pageSuivante() {
        pageCourante++;
        chargerPage();
    }

    private void allerAPageExacte() {
        try {
            pageCourante = Integer.parseInt(tfPage.getText());
        } catch (NumberFormatException ignored) {}
        chargerPage();
    }

    // -----------------------------------------------------------------------
    // CHARGEMENT
    // -----------------------------------------------------------------------

    private void chargerPage() {
        if (boiteService == null) return;

        String recherche = tfRecherche.getText();
        Theme themeSelectionne = comboTheme.getValue();
        Integer idTheme = (themeSelectionne != null) ? themeSelectionne.getIdTheme() : null;

        int totalBoites = boiteService.obtenirNombreTotalBoitesFiltrees(recherche, idTheme);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalBoites / taillePage));

        pageCourante = Math.max(1, Math.min(pageCourante, totalPages));

        mettreAJourTextesPagination(totalBoites, totalPages);

        List<Boite> boites = boiteService.listerBoitesFiltreesPaginees(recherche, idTheme, pageCourante, taillePage);

        if (estVueGrille) {
            afficherVueGrille(boites);
        } else {
            afficherVueListe(boites);
        }

        Platform.runLater(() -> scrollPane.setVvalue(0));
    }

    private void mettreAJourTextesPagination(int totalBoites, int totalPages) {
        lblTotal.setText(totalBoites + " boîtes trouvées");
        tfPage.setText(String.valueOf(pageCourante));
        lblSurTotal.setText(" sur " + totalPages);
        btnPrec.setDisable(pageCourante <= 1);
        btnSuiv.setDisable(pageCourante >= totalPages);
    }

    // -----------------------------------------------------------------------
    // AFFICHAGE
    // -----------------------------------------------------------------------

    private void afficherVueGrille(List<Boite> boites) {
        fpGrille.getChildren().clear();
        for (Boite b : boites) {
            fpGrille.getChildren().add(creerCarteBoite(b));
        }
        scrollPane.setContent(fpGrille);
    }

    private void afficherVueListe(List<Boite> boites) {
        vbListe.getChildren().clear();
        for (Boite b : boites) {
            vbListe.getChildren().add(creerLigneBoite(b));
        }
        scrollPane.setContent(vbListe);
    }

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

        String nomTheme = (b.getTheme() != null) ? b.getTheme().getNom() : "Inconnu";
        Label lblTheme = new Label("Thème : " + nomTheme);
        lblTheme.getStyleClass().add("label");

        String strAnnee  = (b.getAnnee()    != null) ? String.valueOf(b.getAnnee())    : "N/A";
        String strPieces = (b.getNbPieces() != null) ? String.valueOf(b.getNbPieces()) : "?";
        Label lblDetails = new Label(strAnnee + " • " + strPieces + " pièces");
        lblDetails.getStyleClass().add("label");

        carte.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblTheme, lblDetails);
        return carte;
    }

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
            imageView.setImage(new Image(url, true));
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

        String strAnnee  = (b.getAnnee()    != null) ? String.valueOf(b.getAnnee())    : "N/A";
        String strPieces = (b.getNbPieces() != null) ? String.valueOf(b.getNbPieces()) : "?";
        Label lblDetails = new Label(strAnnee + "  |  " + strPieces + " pcs");
        lblDetails.getStyleClass().add("label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ligne.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblTheme, spacer, lblDetails);
        return ligne;
    }

    // -----------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------

    public TextField getTfRecherche() { return tfRecherche; }
    public ComboBox<Theme> getComboTheme() { return comboTheme; }
    public Button getBtnFiltrer() { return btnFiltrer; }
    public Button getBtnReset() { return btnReset; }
    public Button getBtnVue() { return btnVue; }
    public Button getBtnPrec() { return btnPrec; }
    public Button getBtnSuiv() { return btnSuiv; }
    public TextField getTfPage() { return tfPage; }
    public Label getLblTotal() { return lblTotal; }
    public FlowPane getFpGrille() { return fpGrille; }
    public VBox getVbListe() { return vbListe; }
}