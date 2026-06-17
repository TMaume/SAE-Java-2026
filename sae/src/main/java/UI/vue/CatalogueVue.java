package UI.vue;

import App.Boite;
import App.BoiteService;
import App.CollectionService;
import App.EtatBoite;
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
 * Vue affichant le catalogue des boîtes LEGO avec pagination, filtres et autocomplétion.
 */
public class CatalogueVue {
    private final BoiteService boiteService;
    private final ThemeService themeService;
    private final CollectionService collectionService;
    private final Consumer<Boite> actionClicBoite;

    private int pageCourante = 1;
    private final int taillePage = 20;
    private boolean estVueGrille = true;

    private BorderPane root;
    private ScrollPane scrollPane;
    private FlowPane conteneurGrille;
    private VBox conteneurListe;

    private TextField txtRecherche;
    private ListView<String> listeSuggestions;
    private PopupControl popupSuggestions;
    private ComboBox<Theme> comboTheme;
    private TextField txtPageExacte;
    private Label lblPagination;
    private Button btnPrecedent;
    private Button btnSuivant;
    private Label lblInfosTotal;
    private Button btnAjouterBoiteDansCollection;

    // Délai avant de déclencher la recherche de suggestions (ms)
    private static final int DELAI_SUGGESTION_MS = 250;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "suggestion-scheduler");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> tacheSuggestion;

    public CatalogueVue(BoiteService boiteService, ThemeService themeService, CollectionService collectionService, Consumer<Boite> actionClicBoite) {
        this.boiteService = boiteService;
        this.themeService = themeService;
        this.collectionService = collectionService;
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
        root = new BorderPane();
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

    private void basculerVue(Button bouton) {
        estVueGrille = !estVueGrille;
        bouton.setText(estVueGrille ? "Affichage : Grille" : "Affichage : Liste");
        chargerPage();
    }

    private HBox creerBarreFiltres() {
        HBox barreFiltres = new HBox(10);
        barreFiltres.setAlignment(Pos.CENTER_LEFT);

        // Champ de recherche + popup autocomplétion
        StackPane conteneurRecherche = creerChampRechercheAvecSuggestions();

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

        barreFiltres.getChildren().addAll(conteneurRecherche, comboTheme, btnFiltrer, btnReinitialiser);
        return barreFiltres;
    }

    // -----------------------------------------------------------------------
    // AUTOCOMPLETION
    // -----------------------------------------------------------------------

    /**
     * Crée le champ de recherche et son popup de suggestions.
     */
    private StackPane creerChampRechercheAvecSuggestions() {
        txtRecherche = new TextField();
        txtRecherche.setPromptText("Rechercher par nom...");
        txtRecherche.setPrefWidth(250);

        // Liste des suggestions dans un popup
        listeSuggestions = new ListView<>();
        listeSuggestions.setPrefWidth(250);
        listeSuggestions.setMaxHeight(180);
        listeSuggestions.getStyleClass().add("suggestions-list");

        popupSuggestions = new PopupControl();
        popupSuggestions.setAutoHide(true);
        popupSuggestions.getScene().setRoot(listeSuggestions);

        // Clic sur une suggestion → rempli le champ + lance la recherche
        listeSuggestions.setOnMouseClicked(e -> {
            String valeur = listeSuggestions.getSelectionModel().getSelectedItem();
            if (valeur != null) {
                txtRecherche.setText(valeur);
                popupSuggestions.hide();
                appliquerFiltres();
            }
        });

        // Saisie → planifier la récupération des suggestions avec un délai
        txtRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            if (tacheSuggestion != null) tacheSuggestion.cancel(false);

            if (nouveau == null || nouveau.trim().length() < 2) {
                popupSuggestions.hide();
                return;
            }

            tacheSuggestion = scheduler.schedule(() ->
                Platform.runLater(() -> afficherSuggestions(nouveau.trim())),
                DELAI_SUGGESTION_MS, TimeUnit.MILLISECONDS
            );
        });

        // Entrée → filtre immédiat, ferme le popup
        txtRecherche.setOnAction(e -> {
            popupSuggestions.hide();
            appliquerFiltres();
        });

        // Echap → ferme le popup
        txtRecherche.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> popupSuggestions.hide();
                case DOWN -> {
                    if (popupSuggestions.isShowing()) {
                        listeSuggestions.requestFocus();
                        listeSuggestions.getSelectionModel().selectFirst();
                    }
                }
                default -> {}
            }
        });

        // Depuis la liste, Entrée sélectionne et ferme
        listeSuggestions.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> {
                    String valeur = listeSuggestions.getSelectionModel().getSelectedItem();
                    if (valeur != null) {
                        txtRecherche.setText(valeur);
                        popupSuggestions.hide();
                        appliquerFiltres();
                    }
                }
                case ESCAPE -> popupSuggestions.hide();
                default -> {}
            }
        });

        return new StackPane(txtRecherche);
    }

    /**
     * Interroge le service et affiche jusqu'à 8 suggestions sous le champ.
     */
    private void afficherSuggestions(String motCle) {
        if (boiteService == null) return;

        List<Boite> resultats = boiteService.rechercherBoitesParNom(motCle);
        if (resultats.isEmpty()) {
            popupSuggestions.hide();
            return;
        }

        listeSuggestions.getItems().clear();
        int max = Math.min(resultats.size(), 8);
        for (int i = 0; i < max; i++) {
            listeSuggestions.getItems().add(resultats.get(i).getNom());
        }

        // Positionner le popup juste sous le champ de saisie
        if (!popupSuggestions.isShowing() && txtRecherche.getScene() != null) {
            javafx.geometry.Bounds bounds = txtRecherche.localToScreen(txtRecherche.getBoundsInLocal());
            if (bounds != null) {
                popupSuggestions.show(txtRecherche, bounds.getMinX(), bounds.getMaxY() + 2);
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
        txtRecherche.clear();
        comboTheme.setValue(null);
        popupSuggestions.hide();
        pageCourante = 1;
        chargerPage();
    }

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
            pageCourante = Integer.parseInt(txtPageExacte.getText());
        } catch (NumberFormatException ignored) {}
        chargerPage();
    }

    // -----------------------------------------------------------------------
    // CHARGEMENT
    // -----------------------------------------------------------------------

    private void chargerPage() {
        if (boiteService == null) return;

        String recherche = txtRecherche.getText();
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

        // Remonter en haut du scroll après chaque chargement de page
        Platform.runLater(() -> scrollPane.setVvalue(0));
    }

    private void mettreAJourTextesPagination(int totalBoites, int totalPages) {
        lblInfosTotal.setText(totalBoites + " boîtes trouvées");
        txtPageExacte.setText(String.valueOf(pageCourante));

        HBox footer = (HBox) root.getBottom();
        Label lblSurTotal = (Label) footer.getChildren().get(3);
        lblSurTotal.setText(" sur " + totalPages);

        btnPrecedent.setDisable(pageCourante <= 1);
        btnSuivant.setDisable(pageCourante >= totalPages);
    }

    // -----------------------------------------------------------------------
    // AFFICHAGE
    // -----------------------------------------------------------------------

    private void afficherVueGrille(List<Boite> boites) {
        conteneurGrille.getChildren().clear();
        for (Boite b : boites) {
            conteneurGrille.getChildren().add(creerCarteBoite(b));
        }
        scrollPane.setContent(conteneurGrille);
    }

    private void afficherVueListe(List<Boite> boites) {
        conteneurListe.getChildren().clear();
        for (Boite b : boites) {
            conteneurListe.getChildren().add(creerLigneBoite(b));
        }
        scrollPane.setContent(conteneurListe);
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

        btnAjouterBoiteDansCollection = new Button("Ajouter à ma collection");
        btnAjouterBoiteDansCollection.setOnAction(e -> {
            if (collectionService != null) collectionService.ajouterBoite(b, EtatBoite.COMPLETE);
        });

        carte.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblTheme, lblDetails, btnAjouterBoiteDansCollection);
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

        btnAjouterBoiteDansCollection = new Button("Ajouter à ma collection");
        btnAjouterBoiteDansCollection.setOnAction(e -> {
            if (collectionService != null) collectionService.ajouterBoite(b, EtatBoite.COMPLETE);
        });

        ligne.getChildren().addAll(conteneurImage, lblNumero, lblNom, lblTheme, spacer, lblDetails, btnAjouterBoiteDansCollection);
        return ligne;
    }
}