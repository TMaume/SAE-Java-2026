package ui.console;

import java.util.Scanner;

import App.AuthentificationService;
import App.CatalogueRepository;
import App.CatalogueService;
import App.CollectionService;
import App.ConsoleConfirmation;
import App.DbCatalogueRepository;
import App.GestionUtilisateurs;
import App.RoleUtilisateur;
import App.Utilisateur;
import BD.ConnexionMySQL;

public class MenuConsole {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUi ui = new ConsoleUi(scanner);
        ConsoleConfirmation confirmation = new ConsoleConfirmation(ui);

        ui.afficherTitre("Bienvenue dans Briqu'IUTO - Gestionnaire de Lego");

        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(GestionUtilisateurs.cheminParDefaut());
        AuthentificationService authentification = new AuthentificationService(ui, gestionUtilisateurs);
        Utilisateur utilisateur = authentification.demarrer();
        if (utilisateur == null) {
            ui.afficherLigne("Au revoir !");
            scanner.close();
            return;
        }

        CatalogueRepository depot = creerDepotBase(ui);
        if (depot == null) {
            scanner.close();
            return;
        }
        CatalogueService catalogue = new CatalogueService(depot, confirmation);
        CollectionService collection = new CollectionService();

        boolean estAdmin = utilisateur.getRole() == RoleUtilisateur.ADMIN;

        boolean continuer = true;
        while (continuer) {
            ui.afficherTitre("Menu Principal");
            ui.afficherLigne("Connecte : " + utilisateur.getIdentifiant() + " (" + utilisateur.getRole() + ")");
            ui.afficherLigne("--- Utilisateur (Collectionneur) ---");
            ui.afficherLigne("1. Rechercher une boîte (numéro ou nom)");
            ui.afficherLigne("2. Consulter le détail d'une boîte");
            ui.afficherLigne("3. Explorer les boîtes par thème");
            ui.afficherLigne("4. Afficher les statistiques d'une boîte");
            ui.afficherLigne("5. Rechercher les boîtes contenant une pièce précise");
            ui.afficherLigne("6. Gérer ma collection personnelle (Ajout, État, Pièces manquantes)");
            ui.afficherLigne("7. Composer une boîte personnalisée");
            if (estAdmin) {
                ui.afficherLigne("--- Administrateur ---");
                ui.afficherLigne("8. Ajouter une boîte dans la base");
                ui.afficherLigne("9. Ajouter une pièce");
                ui.afficherLigne("10. Créer un thème");
                ui.afficherLigne("11. Mettre à jour le contenu d'une boîte (Ajout pièce/figurine)");
            }
            ui.afficherLigne("0. Quitter");

            int maxChoix = estAdmin ? 11 : 7;
            int choix = ui.lireChoix("Votre choix : ", 0, maxChoix);

            try {
                switch (choix) {
                    case 0:
                        continuer = false;
                        ui.afficherLigne("Au revoir !");
                        break;
                    case 1: MenuUser.rechercherBoite(ui, catalogue); break;
                    case 2: MenuUser.consulterDetailBoite(ui, catalogue); break;
                    case 3: MenuUser.explorerParTheme(ui, catalogue); break;
                    case 4: MenuUser.afficherStatsBoite(ui, catalogue); break;
                    case 5: MenuUser.rechercherParPiece(ui, catalogue); break;
                    case 6: MenuUser.gererCollection(ui, catalogue, collection); break;
                    case 7: MenuUser.composerBoitePerso(ui, catalogue); break;
                    case 8: MenuAdmin.ajouterBoite(ui, catalogue); break;
                    case 9: MenuAdmin.ajouterPiece(ui, catalogue); break;
                    case 10: MenuAdmin.creerTheme(ui, catalogue); break;
                    case 11: MenuAdmin.majContenuBoite(ui, catalogue); break;
                }
            } catch (Exception e) {
                ui.afficherLigne("Erreur inattendue : " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static CatalogueRepository creerDepotBase(ConsoleUi ui) {
        try {
            ConnexionMySQL connexion = new ConnexionMySQL();
            connexion.connecter(null, null, null, null);
            ui.afficherLigne("Connexion a la base etablie.");
            return new DbCatalogueRepository(connexion);
        } catch (Exception e) {
            ui.afficherLigne("Connexion impossible a la base: " + e.getMessage());
            return null;
        }
    }
}
