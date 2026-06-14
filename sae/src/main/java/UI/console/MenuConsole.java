package UI.console;

import java.util.Scanner;
import App.BoiteService;
import App.PieceService;
import App.ThemeService;
import App.CollectionService;
import App.GestionUtilisateurs;
import App.RoleUtilisateur;
import App.Utilisateur;
import BD.*;

public class MenuConsole {

    /**
     * Point d'entrée principal de l'application.
     * Gère l'authentification, la connexion à la base de données et le menu principal.
     *
     * @param args les arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUi ui = new ConsoleUi(scanner);

        ui.afficherTitre("Bienvenue dans Briqu'IUTO - Gestionnaire de Lego");

        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(GestionUtilisateurs.cheminParDefaut());
        AuthentificationService authentification = new AuthentificationService(ui, gestionUtilisateurs);
        Utilisateur utilisateur = authentification.demarrer();
        
        if (utilisateur == null) {
            ui.afficherLigne("Au revoir !");
            scanner.close();
            return;
        }

        try {
            ConnexionMySQL connexion = new ConnexionMySQL();
            connexion.connecter(null, null, null, null);
            ui.afficherLigne("Connexion à la base établie.");

            BoiteBD boiteBD = new BoiteBD(connexion);
            ThemeBD themeBD = new ThemeBD(connexion);
            ThemeParentBD themeParentBD = new ThemeParentBD(connexion);
            PieceBD pieceBD = new PieceBD(connexion);
            CategorieBD categorieBD = new CategorieBD(connexion);
            CouleurBD couleurBD = new CouleurBD(connexion);
            Contenu contenuBD = new Contenu(connexion);
            ContenirpBD contenirpBD = new ContenirpBD(connexion);
            ContenirfBD contenirfBD = new ContenirfBD(connexion);
            ContenirbBD contenirbBD = new ContenirbBD(connexion);

            ThemeService themeService = new ThemeService(themeBD, themeParentBD);
            PieceService pieceService = new PieceService(pieceBD, categorieBD, couleurBD);
            BoiteService boiteService = new BoiteService(boiteBD, contenuBD, contenirpBD, contenirfBD, contenirbBD, themeService);
            
            CollectionService collection = new CollectionService();

            boolean estAdmin = utilisateur.getRole() == RoleUtilisateur.ADMIN;
            boolean continuer = true;

            while (continuer) {
                ui.afficherTitre("Menu Principal");
                
                // Options pour tout le monde
                ui.afficherLigne("1. Rechercher une boîte");
                ui.afficherLigne("2. Consulter les détails d'une boîte");
                ui.afficherLigne("3. Explorer par thème");
                ui.afficherLigne("4. Afficher les statistiques d'une boîte");
                ui.afficherLigne("5. Rechercher par pièce");
                ui.afficherLigne("6. Gérer ma collection");
                ui.afficherLigne("7. Composer une boîte personnalisée");
                
                // Options Admin uniquement
                if (estAdmin) {
                    ui.afficherLigne("8. [Admin] Ajouter une boîte");
                    ui.afficherLigne("9. [Admin] Ajouter une pièce");
                    ui.afficherLigne("10. [Admin] Créer un thème");
                    ui.afficherLigne("11. [Admin] Modifier contenu boîte");
                }
                
                ui.afficherLigne("0. Quitter");

                int choix = ui.lireChoix("Choix : ", 0, 11);
                switch (choix) {
                    case 0: continuer = false; break;
                    case 1: MenuUser.rechercherBoite(ui, boiteService); break;
                    case 2: MenuUser.consulterDetailBoite(ui, boiteService); break;
                    case 3: MenuUser.explorerParTheme(ui, themeService, boiteService); break;
                    case 4: MenuUser.afficherStatsBoite(ui, boiteService); break;
                    case 5: MenuUser.rechercherParPiece(ui, pieceService, boiteService); break;
                    case 6: MenuUser.gererCollection(ui, collection, boiteService); break;
                    case 7: MenuUser.composerBoitePerso(ui, boiteService, pieceService); break;
                    case 8: if(estAdmin) MenuAdmin.ajouterBoite(ui, boiteService, themeService); break;
                    case 9: if(estAdmin) MenuAdmin.ajouterPiece(ui, pieceService); break;
                    case 10: if(estAdmin) MenuAdmin.creerTheme(ui, themeService); break;
                    case 11: if(estAdmin) MenuAdmin.majContenuBoite(ui, boiteService, pieceService); break;
                }
            }
            connexion.close();
        } catch (Exception e) {
            ui.afficherLigne("Erreur inattendue : " + e.getMessage());
        }
        scanner.close();
    }
}