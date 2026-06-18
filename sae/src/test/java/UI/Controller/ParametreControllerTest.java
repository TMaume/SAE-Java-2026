package UI.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import javafx.scene.Group;
import javafx.scene.Scene;
import testsupport.JavaFxTestSupport;

public class ParametreControllerTest {

    /**
     * Verification theme sombre
     */
    @Test
    void appliquerTheme_chargeLeBonStylesheetPourLeThemeSombre() {
        JavaFxTestSupport.ensureJavaFx();
        ParametreController.setThemeActuel("Sombre");
        Scene scene = new Scene(new Group());

        ParametreController.appliquerTheme(scene);

        assertEquals(1, scene.getStylesheets().size());
        assertTrue(scene.getStylesheets().get(0).contains("sombre-theme.css"));
    }
}
