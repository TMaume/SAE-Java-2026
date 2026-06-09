package App;

import ui.console.ConsoleUi;

public class ConsoleConfirmation implements GestionConfirmation {
    private final ConsoleUi ui;

    public ConsoleConfirmation(ConsoleUi ui) {
        if (ui == null) {
            throw new IllegalArgumentException("ui");
        }
        this.ui = ui;
    }

    @Override
    public boolean confirmer(String message) {
        return ui.lireOuiNon(message + " (o/n): ");
    }
}
