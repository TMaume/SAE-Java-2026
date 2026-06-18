package UI.Controller;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import App.GestionUtilisateurs;
import App.RoleUtilisateur;
import App.Utilisateur;
import javafx.scene.control.Label;
import testsupport.JavaFxTestSupport;

public class AuthControllerTest {

    @TempDir
    Path tempDir;

    /**
     * Verification champs vides
     */
    @Test
    void connecter_afficheUnMessageSiChampsVides() {
        JavaFxTestSupport.ensureJavaFx();
        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(tempDir.resolve("utilisateurs.csv"));
        AuthController controller = new AuthController(gestionUtilisateurs);
        Label erreur = new Label();

        Utilisateur utilisateur = controller.connecter("", "", erreur);

        assertNull(utilisateur);
        assertEquals("Veuillez remplir tous les champs.", erreur.getText());
    }

    /**
     * Verification connexion admin
     */
    @Test
    void connecter_reussitAvecCompteExistant() {
        JavaFxTestSupport.ensureJavaFx();
        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(tempDir.resolve("utilisateurs.csv"));
        AuthController controller = new AuthController(gestionUtilisateurs);
        Label erreur = new Label();

        Utilisateur utilisateur = controller.connecter("admin", "admin", erreur);

        assertNotNull(utilisateur);
        assertEquals("admin", utilisateur.getIdentifiant());
        assertEquals(RoleUtilisateur.ADMIN, utilisateur.getRole());
        assertEquals("", erreur.getText());
    }
}
