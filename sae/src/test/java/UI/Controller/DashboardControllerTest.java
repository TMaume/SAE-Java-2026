package UI.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

import App.BoiteService;
import App.CollectionService;
import App.PieceService;
import App.RoleUtilisateur;
import App.ThemeService;
import App.Utilisateur;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import testsupport.JavaFxTestSupport;

public class DashboardControllerTest {

    /**
     * Verification remplacement contenu central
     */
    @Test
    void chargerContenu_remplaceLeContenuDuPanneauCentral() {
        JavaFxTestSupport.ensureJavaFx();
        DashboardController controller = new DashboardController(
                new Utilisateur("alice", "secret", RoleUtilisateur.UTILISATEUR),
                (BoiteService) null,
                (PieceService) null,
                (ThemeService) null,
                (CollectionService) null);

        StackPane panneau = new StackPane();
        Node contenuInitial = new Label("ancien");
        Node nouveauContenu = new Label("nouveau");
        panneau.getChildren().add(contenuInitial);

        controller.chargerContenu(panneau, nouveauContenu);

        assertEquals(1, panneau.getChildren().size());
        assertSame(nouveauContenu, panneau.getChildren().get(0));
    }
}
