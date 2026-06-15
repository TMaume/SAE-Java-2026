package UI.Controller;

import App.*;
import UI.vue.DashboardVue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardController  implements EventHandler<ActionEvent>{
    private final BoiteService boiteService;
    private final PieceService pieceService;
    private final ThemeService themeService;
    private final CollectionService collectionService;
    private final Utilisateur utilisateurConnecte;
    private DashboardVue dash;


    public DashboardController(Utilisateur utilisateur, BoiteService boiteService, 
                               PieceService pieceService, ThemeService themeService, 
                               CollectionService collectionService,DashboardVue vue) {

                                
        this.utilisateurConnecte = utilisateur;
        this.boiteService = boiteService;
        this.pieceService = pieceService;
        this.themeService = themeService;
        this.collectionService = collectionService;
        this.dash = vue;
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

    @Override
    public void handle(ActionEvent arg0) {      
        dash.changecouleur((Button) arg0.getSource());
    }
}