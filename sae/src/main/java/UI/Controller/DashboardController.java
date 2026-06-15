package UI.Controller;

import App.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardController {
    private final BoiteService boiteService;
    private final PieceService pieceService;
    private final ThemeService themeService;
    private final CollectionService collectionService;
    private final Utilisateur utilisateurConnecte;

    public DashboardController(Utilisateur utilisateur, BoiteService boiteService, 
                               PieceService pieceService, ThemeService themeService, 
                               CollectionService collectionService) {
                                
        this.utilisateurConnecte = utilisateur;
        this.boiteService = boiteService;
        this.pieceService = pieceService;
        this.themeService = themeService;
        this.collectionService = collectionService;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    /**
     * Change le contenu affiché dans la zone centrale droite.
     */
    public void chargerContenu(StackPane conteneurCentral, Node nouvelleVue) {
        conteneurCentral.getChildren().clear();
        conteneurCentral.getChildren().add(nouvelleVue);
    }
}