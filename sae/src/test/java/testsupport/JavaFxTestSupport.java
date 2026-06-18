package testsupport;

import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;

/**
 * Support JavaFX pour les tests UI
 */
public final class JavaFxTestSupport {

    private static final AtomicBoolean INITIALISE = new AtomicBoolean(false);

    private JavaFxTestSupport() {
    }

    /**
     * Initialise JavaFX une seule fois
     */
    public static void ensureJavaFx() {
        if (INITIALISE.compareAndSet(false, true)) {
            try {
                Platform.startup(() -> {
                });
            } catch (IllegalStateException ignored) {
            }
        }
    }
}
