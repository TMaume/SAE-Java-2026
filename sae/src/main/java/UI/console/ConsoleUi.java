package UI.console;

import java.util.Scanner;

public class ConsoleUi {
    private final Scanner scanner;

    /**
     * Crée une interface console avec le scanner fourni.
     *
     * @param scanner le scanner pour lire les entrées utilisateur (non null)
     * @throws IllegalArgumentException si scanner est null
     */
    public ConsoleUi(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("scanner");
        }
        this.scanner = scanner;
    }

    /**
     * Affiche une ligne de texte dans la console.
     *
     * @param texte le texte à afficher (null affiche une ligne vide)
     */
    public void afficherLigne(String texte) {
        System.out.println(texte == null ? "" : texte);
    }

    /**
     * Affiche un titre encadré de séparateurs dans la console.
     *
     * @param texte le titre à afficher (null affiche un titre vide)
     */
    public void afficherTitre(String texte) {
        System.out.println();
        System.out.println("=== " + (texte == null ? "" : texte) + " ===");
    }

    /**
     * Affiche une invite et retourne le texte saisi par l'utilisateur.
     *
     * @param invite le message affiché avant la saisie
     * @return le texte saisi, jamais null
     */
    public String lireTexte(String invite) {
        System.out.print(invite == null ? "" : invite);
        String ligne = scanner.nextLine();
        return ligne == null ? "" : ligne.trim();
    }

    /**
     * Affiche une invite et retourne l'entier saisi par l'utilisateur.
     * Redemande tant que la saisie n'est pas un entier valide.
     *
     * @param invite le message affiché avant la saisie
     * @return l'entier saisi
     */
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

    /**
     * Affiche une invite et retourne un entier compris entre min et max.
     * Redemande tant que la valeur saisie est hors de la plage autorisée.
     *
     * @param invite le message affiché avant la saisie
     * @param min la valeur minimale acceptée (incluse)
     * @param max la valeur maximale acceptée (incluse)
     * @return l'entier saisi dans la plage [min, max]
     */
    public int lireChoix(String invite, int min, int max) {
        while (true) {
            int valeur = lireEntier(invite);
            if (valeur >= min && valeur <= max) {
                return valeur;
            }
            afficherLigne("Choix invalide.");
        }
    }

    /**
     * Affiche une invite et retourne true si l'utilisateur répond "o"/"oui", false si "n"/"non".
     * Redemande tant que la réponse n'est pas reconnue.
     *
     * @param invite le message affiché avant la saisie
     * @return true pour oui, false pour non
     */
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