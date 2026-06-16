package UI.vue;
import App.Boite;
import App.BoiteService;
import App.BoiteStats;
import App.CollectionItem;
import App.CollectionService;
import App.Couleur;
import App.EtatBoite;
import App.FigurineQuantite;
import App.PieceQuantite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.Map;
/**
 * Vue affichant les détails complets, les statistiques, les pièces et les figurines d'une boîte LEGO.
 */
public class DetailBoiteVue extends BorderPane {
private final BoiteService boiteService;
private final CollectionService collectionService;
private final Boite boiteComplete;
private int pageCourantePieces = 1;
private final int taillePagePieces = 20;
private FlowPane conteneurPiecesGrille;
private Label lblPaginationPieces;
private Button btnPrecedentPieces;
private Button btnSuivantPieces;
private Button btnAjouterCollection;
private Label lblMessageCollection;
    /**
     * Construit la vue détaillée d'une boîte (sans gestion de la collection personnelle).
     *
     * @param boiteLegere la boîte allégée provenant du catalogue
     * @param boiteService le service permettant de récupérer les statistiques et l'inventaire
     * @param actionRetour l'action déclenchée pour revenir au catalogue
     */
public DetailBoiteVue(Boite boiteLegere, BoiteService boiteService, Runnable actionRetour) {
this(boiteLegere, boiteService, null, actionRetour);
    }
 
    /**
     * Construit la vue détaillée d'une boîte.
     *
     * @param boiteLegere la boîte allégée provenant du catalogue
     * @param boiteService le service permettant de récupérer les statistiques et l'inventaire
     * @param collectionService le service permettant d'ajouter la boîte à la collection personnelle (peut être null)
     * @param actionRetour l'action déclenchée pour revenir au catalogue
     */
public DetailBoiteVue(Boite boiteLegere, BoiteService boiteService, CollectionService collectionService, Runnable actionRetour) {
this.boiteService = boiteService;
this.collectionService = collectionService;
Boite boiteChargee = boiteService.chargerBoiteComplete(boiteLegere.getNumero());
this.boiteComplete = (boiteChargee != null) ? boiteChargee : boiteLegere;
setPadding(new Insets(30));
setTop(creerEnTete(actionRetour, this.boiteComplete));
setCenter(creerOnglets(this.boiteComplete));
    }
    /**
     * Formate le code RGB pour garantir l'utilisation dans JavaFX.
     *
     * @param rgb le code RGB brut
     * @return le code RGB formaté avec le préfixe #
     */
private String formaterRgb(String rgb) {
if (rgb == null || rgb.isBlank()) {
return "#ecf0f1";
        }
if (!rgb.startsWith("#")) {
return "#" + rgb;
        }
return rgb;
    }
    /**
     * Calcule la couleur de texte idéale (noir ou blanc) pour contraster avec le fond.
     *
     * @param rgbHex la couleur de fond en format hexadécimal
     * @return la couleur du texte (black ou white)
     */
private String obtenirCouleurTexte(String rgbHex) {
if (rgbHex == null || rgbHex.length() != 7) {
return "black";
        }
try {
int r = Integer.parseInt(rgbHex.substring(1, 3), 16);
int g = Integer.parseInt(rgbHex.substring(3, 5), 16);
int b = Integer.parseInt(rgbHex.substring(5, 7), 16);
double luminance = (0.299 * r + 0.587 * g + 0.114 * b);
return luminance > 150 ? "black" : "white";
        } catch (NumberFormatException e) {
return "black";
        }
    }
    /**
     * Crée l'en-tête contenant le bouton de retour et le titre.
     *
     * @param actionRetour l'action du bouton retour
     * @param boite la boîte à afficher
     * @return un HBox configuré
     */
private HBox creerEnTete(Runnable actionRetour, Boite boite) {
HBox entete = new HBox(20);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setPadding(new Insets(0, 0, 30, 0));
Button btnRetour = new Button("◄ Retour au catalogue");
        btnRetour.getStyleClass().add("button");
        btnRetour.setOnAction(e -> actionRetour.run());
String nomTheme = (boite.getTheme() != null) ? boite.getTheme().getNom() : "Inconnu";
Label lblTitre = new Label("LEGO " + nomTheme + " " + boite.getNumero() + " - " + boite.getNom());
        lblTitre.getStyleClass().add("title-label");
        entete.getChildren().addAll(btnRetour, lblTitre);
return entete;
    }
    /**
     * Crée le système d'onglets pour séparer les statistiques, les pièces et les figurines.
     *
     * @param boite la boîte à afficher
     * @return le composant TabPane contenant les vues
     */
private TabPane creerOnglets(Boite boite) {
TabPane tabPane = new TabPane();
Tab tabInfos = new Tab("Informations & Statistiques");
        tabInfos.setClosable(false);
        tabInfos.setContent(creerContenu(boite));
Tab tabPieces = new Tab("Pièces incluses");
        tabPieces.setClosable(false);
        tabPieces.setContent(creerContenuPieces(boite));
Tab tabFigurines = new Tab("Figurines incluses");
        tabFigurines.setClosable(false);
        tabFigurines.setContent(creerContenuFigurines(boite));
        tabPane.getTabs().addAll(tabInfos, tabPieces, tabFigurines);
return tabPane;
    }
    /**
     * Crée la zone contenant les informations générales et les statistiques.
     *
     * @param boite la boîte à afficher
     * @return un HBox structurant l'affichage
     */
private HBox creerContenu(Boite boite) {
HBox contenu = new HBox(30);
        contenu.setAlignment(Pos.TOP_LEFT);
        contenu.setPadding(new Insets(20, 0, 0, 0));
VBox zoneInformations = new VBox(20);
        zoneInformations.getChildren().addAll(
creerSectionInformations(boite),
creerSectionStatistiques(boite)
        );
        HBox.setHgrow(zoneInformations, Priority.ALWAYS);
        contenu.getChildren().addAll(
creerSectionImage(boite),
            zoneInformations
        );
return contenu;
    }
    /**
     * Crée le conteneur paginé affichant la liste des pièces.
     *
     * @param boite la boîte complète contenant les pièces
     * @return un BorderPane contenant la grille et la pagination
     */
private BorderPane creerContenuPieces(Boite boite) {
BorderPane conteneurGlobal = new BorderPane();
        conteneurGlobal.setPadding(new Insets(20));
        conteneurPiecesGrille = new FlowPane(20, 20);
        conteneurPiecesGrille.setAlignment(Pos.TOP_LEFT);
if (boite.getPieces() == null || boite.getPieces().isEmpty()) {
Label lblVide = new Label("Aucun inventaire de pièces répertorié pour cette boîte.");
            lblVide.getStyleClass().add("subtitle-label");
            lblVide.setStyle("-fx-font-style: italic;");
            conteneurPiecesGrille.getChildren().add(lblVide);
            conteneurGlobal.setCenter(conteneurPiecesGrille);
return conteneurGlobal;
        }
ScrollPane scroll = new ScrollPane(conteneurPiecesGrille);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        conteneurGlobal.setCenter(scroll);
HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));
        btnPrecedentPieces = new Button("◄ Précédent");
        btnPrecedentPieces.getStyleClass().add("btn-primary");
        btnPrecedentPieces.setOnAction(e -> {
if (pageCourantePieces > 1) {
                pageCourantePieces--;
chargerPagePieces(boite);
            }
        });
        lblPaginationPieces = new Label();
        lblPaginationPieces.getStyleClass().add("label");
        lblPaginationPieces.setStyle("-fx-font-weight: bold;");
        btnSuivantPieces = new Button("Suivant ►");
        btnSuivantPieces.getStyleClass().add("btn-primary");
        btnSuivantPieces.setOnAction(e -> {
            pageCourantePieces++;
chargerPagePieces(boite);
        });
        footer.getChildren().addAll(btnPrecedentPieces, lblPaginationPieces, btnSuivantPieces);
        conteneurGlobal.setBottom(footer);
chargerPagePieces(boite);
return conteneurGlobal;
    }
    /**
     * Charge et affiche la page actuelle des pièces.
     *
     * @param boite la boîte contenant la liste globale des pièces
     */
private void chargerPagePieces(Boite boite) {
int totalItems = boite.getPieces().size();
int totalPages = (int) Math.ceil((double) totalItems / taillePagePieces);
if (totalPages == 0) {
            totalPages = 1;
        }
if (pageCourantePieces > totalPages) {
            pageCourantePieces = totalPages;
        }
if (pageCourantePieces < 1) {
            pageCourantePieces = 1;
        }
        lblPaginationPieces.setText("Page " + pageCourantePieces + " sur " + totalPages);
        btnPrecedentPieces.setDisable(pageCourantePieces <= 1);
        btnSuivantPieces.setDisable(pageCourantePieces >= totalPages);
        conteneurPiecesGrille.getChildren().clear();
int indexDebut = (pageCourantePieces - 1) * taillePagePieces;
int indexFin = Math.min(indexDebut + taillePagePieces, totalItems);
for (int i = indexDebut; i < indexFin; i++) {
            conteneurPiecesGrille.getChildren().add(creerCartePiece(boite.getPieces().get(i)));
        }
    }
    /**
     * Crée une carte graphique pour une pièce individuelle.
     *
     * @param pq l'objet contenant la pièce et sa quantité
     * @return un VBox stylisé
     */
private VBox creerCartePiece(PieceQuantite pq) {
VBox carte = new VBox(10);
        carte.setPadding(new Insets(15));
        carte.getStyleClass().add("card");
        carte.setPrefWidth(220);
        carte.setAlignment(Pos.CENTER);
ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
String url = pq.getImageP();
if (url != null && !url.isBlank() && !url.equalsIgnoreCase("null")) {
try {
Image image = new Image(url, true);
                imageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Impossible de charger l'image de la pièce : " + url);
            }
        }
VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefHeight(130);
Label lblId = new Label("#" + pq.getPiece().getNumero());
        lblId.getStyleClass().add("subtitle-label");
        lblId.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); // On garde l'accent orange pour l'ID
Label lblNom = new Label(pq.getPiece().getNom());
        lblNom.getStyleClass().add("label");
        lblNom.setStyle("-fx-font-weight: bold;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(40);
String nomCouleur = (pq.getPiece().getCouleur() != null) ? pq.getPiece().getCouleur().getNom() : "Couleur inconnue";
Label lblCouleur = new Label(nomCouleur);
        lblCouleur.getStyleClass().add("subtitle-label");
Label lblQte = new Label("Quantité incluse : " + pq.getQuantite());
        lblQte.getStyleClass().add("label");
        lblQte.setStyle("-fx-font-weight: bold;");
        carte.getChildren().addAll(conteneurImage, lblId, lblNom, lblCouleur, lblQte);
if (pq.isEnSupplement()) {
Label lblSupp = new Label("(Pièce de supplément)");
            lblSupp.getStyleClass().add("subtitle-label");
            lblSupp.setStyle("-fx-font-style: italic; -fx-text-fill: #e74c3c;"); // Rouge signalétique conservé
            carte.getChildren().add(lblSupp);
        }
return carte;
    }
    /**
     * Crée le conteneur affichant la liste des figurines sous forme de cartes.
     *
     * @param boite la boîte complète contenant les figurines
     * @return un ScrollPane contenant les cartes
     */
private ScrollPane creerContenuFigurines(Boite boite) {
FlowPane flowFigurines = new FlowPane(20, 20);
        flowFigurines.setPadding(new Insets(20));
        flowFigurines.setAlignment(Pos.TOP_LEFT);
if (boite.getFigurines() == null || boite.getFigurines().isEmpty()) {
Label lblVide = new Label("Aucune figurine répertoriée pour cette boîte.");
            lblVide.getStyleClass().add("subtitle-label");
            lblVide.setStyle("-fx-font-style: italic;");
            flowFigurines.getChildren().add(lblVide);
        } else {
for (FigurineQuantite fq : boite.getFigurines()) {
                flowFigurines.getChildren().add(creerCarteFigurine(fq));
            }
        }
ScrollPane scroll = new ScrollPane(flowFigurines);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
return scroll;
    }
    /**
     * Crée une carte graphique pour une figurine individuelle.
     *
     * @param fq l'objet contenant la figurine et sa quantité
     * @return un VBox stylisé
     */
private VBox creerCarteFigurine(FigurineQuantite fq) {
VBox carte = new VBox(10);
        carte.setPadding(new Insets(15));
        carte.getStyleClass().add("card");
        carte.setPrefWidth(220);
        carte.setAlignment(Pos.CENTER);
ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
String url = fq.getFigurine().getImageF();
if (url != null && !url.isBlank() && !url.equalsIgnoreCase("null")) {
try {
Image image = new Image(url, true);
                imageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Impossible de charger l'image de la figurine (URL invalide) : " + url);
            }
        }
VBox conteneurImage = new VBox(imageView);
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPrefHeight(130);
Label lblId = new Label("#" + fq.getFigurine().getIdFigurine());
        lblId.getStyleClass().add("subtitle-label");
        lblId.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); // Accent orange
Label lblNom = new Label(fq.getFigurine().getNom());
        lblNom.getStyleClass().add("label");
        lblNom.setStyle("-fx-font-weight: bold;");
        lblNom.setWrapText(true);
        lblNom.setAlignment(Pos.CENTER);
        lblNom.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblNom.setMinHeight(40);
Label lblQte = new Label("Quantité incluse : " + fq.getQuantite());
        lblQte.getStyleClass().add("label");
        lblQte.setStyle("-fx-font-weight: bold;");
        carte.getChildren().addAll(conteneurImage, lblId, lblNom, lblQte);
return carte;
    }
    /**
     * Crée le conteneur affichant l'image principale de la boîte ainsi que le bouton
     * permettant d'ajouter la boîte à la collection personnelle de l'utilisateur.
     *
     * @param boite la boîte contenant l'URL de l'image
     * @return un VBox stylisé contenant l'image et le bouton d'ajout
     */
private VBox creerSectionImage(Boite boite) {
VBox conteneurImage = new VBox();
        conteneurImage.setAlignment(Pos.CENTER);
        conteneurImage.setPadding(new Insets(10));
        conteneurImage.getStyleClass().add("stats-box");
        conteneurImage.setPrefWidth(350);
        conteneurImage.setMaxHeight(350);
ImageView imageView = new ImageView();
        imageView.setFitWidth(330);
        imageView.setFitHeight(330);
        imageView.setPreserveRatio(true);
String url = boite.getImageBoite();
if (url != null && !url.isBlank() && !url.equalsIgnoreCase("null")) {
try {
Image image = new Image(url, true);
                imageView.setImage(image);
            } catch (IllegalArgumentException e) {
                System.err.println("Impossible de charger l'image de la boîte (URL invalide) : " + url);
            }
        }
        conteneurImage.getChildren().add(imageView);
 
        // --- Bouton "Ajouter à ma collection" ---
        VBox conteneurAction = new VBox(8);
        conteneurAction.setAlignment(Pos.CENTER);
        conteneurAction.setPadding(new Insets(15, 0, 0, 0));
 
        btnAjouterCollection = new Button("Ajouter à ma collection");
        btnAjouterCollection.getStyleClass().add("btn-primary");
        btnAjouterCollection.setMaxWidth(Double.MAX_VALUE);
 
        lblMessageCollection = new Label();
        lblMessageCollection.setStyle("-fx-font-size: 11px;");
        lblMessageCollection.setWrapText(true);
 
        if (collectionService == null) {
            btnAjouterCollection.setDisable(true);
        } else {
            mettreAJourBoutonCollection(boite);
            btnAjouterCollection.setOnAction(e -> ajouterALaCollection(boite));
        }
 
        conteneurAction.getChildren().addAll(btnAjouterCollection, lblMessageCollection);
        conteneurImage.getChildren().add(conteneurAction);
        // -----------------------------------------
 
return conteneurImage;
    }
 
    /**
     * Ajoute la boîte courante à la collection personnelle via le CollectionService,
     * puis met à jour l'état du bouton et le message de retour.
     *
     * @param boite la boîte à ajouter
     */
private void ajouterALaCollection(Boite boite) {
try {
            collectionService.ajouterBoite(boite, EtatBoite.COMPLETE);
            lblMessageCollection.setText("Boîte ajoutée à votre collection !");
            lblMessageCollection.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            mettreAJourBoutonCollection(boite);
        } catch (Exception ex) {
            lblMessageCollection.setText("Erreur lors de l'ajout : " + ex.getMessage());
            lblMessageCollection.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");
        }
    }
 
    /**
     * Met à jour le texte et l'état (activé/désactivé) du bouton d'ajout
     * selon que la boîte est déjà présente dans la collection.
     *
     * @param boite la boîte concernée
     */
private void mettreAJourBoutonCollection(Boite boite) {
CollectionItem item = collectionService.obtenerItem(boite.getNumero());
if (item != null) {
            btnAjouterCollection.setText("Déjà dans ma collection");
            btnAjouterCollection.setDisable(true);
        } else {
            btnAjouterCollection.setText("Ajouter à ma collection");
            btnAjouterCollection.setDisable(false);
        }
    }
 
    /**
     * Crée la grille détaillant les métadonnées de base de la boîte.
     *
     * @param boite la boîte contenant les informations
     * @return un GridPane contenant les labels
     */
private GridPane creerSectionInformations(Boite boite) {
GridPane grille = new GridPane();
        grille.setVgap(15);
        grille.setHgap(50);
        grille.setPadding(new Insets(20));
        grille.getStyleClass().add("stats-box");
String nomTheme = (boite.getTheme() != null) ? boite.getTheme().getNom() : "Inconnu";
String annee = (boite.getAnnee() != null) ? String.valueOf(boite.getAnnee()) : "Non renseignée";
String pieces = (boite.getNbPieces() != null) ? String.valueOf(boite.getNbPieces()) : "Inconnu";
ajouterLigneInfo(grille, 0, "Numéro de référence :", boite.getNumero());
ajouterLigneInfo(grille, 1, "Thème :", nomTheme);
ajouterLigneInfo(grille, 2, "Année de sortie :", annee);
ajouterLigneInfo(grille, 3, "Pièces annoncées :", pieces);
return grille;
    }
    /**
     * Crée la section affichant les statistiques réelles du contenu de la boîte.
     *
     * @param boite la boîte à analyser
     * @return un VBox contenant les statistiques calculées
     */
private VBox creerSectionStatistiques(Boite boite) {
VBox sectionComplete = new VBox(15);
        sectionComplete.setPadding(new Insets(20));
        sectionComplete.getStyleClass().add("stats-box");
        VBox.setVgrow(sectionComplete, Priority.ALWAYS);
Label lblTitreStats = new Label("Statistiques du contenu réel");
        lblTitreStats.getStyleClass().add("title-label");
Separator separator = new Separator();
BoiteStats stats = boiteService.calculerStatsBoite(boite.getNumero());
if (stats == null || stats.getTotalPieces() == 0) {
Label lblVide = new Label("Aucun inventaire détaillé disponible pour cette boîte en base de données.");
            lblVide.getStyleClass().add("subtitle-label");
            lblVide.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            sectionComplete.getChildren().addAll(lblTitreStats, separator, lblVide);
return sectionComplete;
        }
HBox conteneurDivise = new HBox(30);
        conteneurDivise.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(conteneurDivise, Priority.ALWAYS);
VBox colonneGauche = new VBox(20);
        colonneGauche.setPrefWidth(450);
        colonneGauche.setMaxWidth(450);
GridPane grilleStats = new GridPane();
        grilleStats.setVgap(15);
        grilleStats.setHgap(30);
ajouterLigneInfo(grilleStats, 0, "Total de pièces répertoriées :", String.valueOf(stats.getTotalPieces()));
ajouterLigneInfo(grilleStats, 1, "Dont pièces en supplément :", String.valueOf(stats.getTotalSupplement()));
ajouterLigneInfo(grilleStats, 2, "Nombre de figurines :", String.valueOf(stats.getTotalFigurines()));
ajouterLigneInfo(grilleStats, 3, "Sous-boîtes incluses :", String.valueOf(stats.getTotalSousBoites()));
Label lblCouleurs = new Label("Répartition par couleurs :");
        lblCouleurs.getStyleClass().add("subtitle-label");
        lblCouleurs.setStyle("-fx-font-weight: bold;");
FlowPane flowCouleurs = new FlowPane(10, 10);
        flowCouleurs.setPrefWrapLength(420);
PieChart graphiqueCouleurs = new PieChart();
        graphiqueCouleurs.setPrefSize(600, 600);
        graphiqueCouleurs.setLegendVisible(false);
for (Map.Entry<Couleur, Integer> entree : stats.getRepartitionCouleurs().entrySet()) {
Couleur couleur = entree.getKey();
int quantite = entree.getValue();
String rgbHex = formaterRgb(couleur.getRgb());
String textCouleur = obtenirCouleurTexte(rgbHex);
String borderColor = rgbHex.equalsIgnoreCase("#FFFFFF") ? "#bdc3c7" : rgbHex;
Label tagCouleur = new Label(couleur.getNom() + " (" + quantite + ")");
            tagCouleur.setStyle("-fx-background-color: " + rgbHex + "; -fx-text-fill: " + textCouleur + "; -fx-border-color: " + borderColor + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
            flowCouleurs.getChildren().add(tagCouleur);
PieChart.Data tranche = new PieChart.Data(couleur.getNom() + " (" + quantite + ")", quantite);
            graphiqueCouleurs.getData().add(tranche);
if (tranche.getNode() != null) {
                tranche.getNode().setStyle("-fx-pie-color: " + rgbHex + "; -fx-border-color: transparent;");
            }
            tranche.nodeProperty().addListener((obs, oldNode, newNode) -> {
if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: " + rgbHex + "; -fx-border-color: transparent;");
                }
            });
        }
ScrollPane scrollCouleurs = new ScrollPane(flowCouleurs);
        scrollCouleurs.setFitToWidth(true);
        scrollCouleurs.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollCouleurs, Priority.ALWAYS);
        colonneGauche.getChildren().addAll(grilleStats, lblCouleurs, scrollCouleurs);
        HBox.setHgrow(graphiqueCouleurs, Priority.ALWAYS);
        conteneurDivise.getChildren().addAll(colonneGauche, graphiqueCouleurs);
        sectionComplete.getChildren().addAll(lblTitreStats, separator, conteneurDivise);
return sectionComplete;
    }
    /**
     * Ajoute une ligne de données dans un GridPane.
     *
     * @param grille le conteneur cible
     * @param ligne l'index de la ligne
     * @param libelle le nom de la caractéristique
     * @param valeur la valeur de la caractéristique
     */
private void ajouterLigneInfo(GridPane grille, int ligne, String libelle, String valeur) {
Label lblLibelle = new Label(libelle);
        lblLibelle.getStyleClass().add("subtitle-label");
Label lblValeur = new Label(valeur);
        lblValeur.getStyleClass().add("label");
        lblValeur.setStyle("-fx-font-weight: bold;");
        grille.add(lblLibelle, 0, ligne);
        grille.add(lblValeur, 1, ligne);
    }
}
 
