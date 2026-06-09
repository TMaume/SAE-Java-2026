package ui.console;

import java.util.Scanner;

public class ConsoleUi {
    private final Scanner scanner;

    public ConsoleUi(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("scanner");
        }
        this.scanner = scanner;
    }

    public void afficherLigne(String texte) {
        System.out.println(texte == null ? "" : texte);
    }

    public void afficherTitre(String texte) {
        System.out.println();
        System.out.println("=== " + (texte == null ? "" : texte) + " ===");
    }

    public String lireTexte(String invite) {
        System.out.print(invite == null ? "" : invite);
        String ligne = scanner.nextLine();
        return ligne == null ? "" : ligne.trim();
    }

    public int lireEntier(String invite) {
        while (true) {
            String texte = lireTexte(invite);
            try {
                return Integer.parseInt(texte.trim());
            } catch (NumberFormatException e) {
                afficherLigne("Entree invalide.");
            }
        }
    }

    public int lireChoix(String invite, int min, int max) {
        while (true) {
            int valeur = lireEntier(invite);
            if (valeur >= min && valeur <= max) {
                return valeur;
            }
            afficherLigne("Choix invalide.");
        }
    }

    public boolean lireOuiNon(String invite) {
        while (true) {
            String texte = lireTexte(invite);
            if (texte.equalsIgnoreCase("o") || texte.equalsIgnoreCase("oui")) {
                return true;
            }
            if (texte.equalsIgnoreCase("n") || texte.equalsIgnoreCase("non")) {
                return false;
            }
            afficherLigne("Reponse invalide.");
        }
    }
}
