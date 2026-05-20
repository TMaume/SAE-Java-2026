package App;

import BD.ConnexionMySQL;

public final class DataStoreFactory {
    private DataStoreFactory() {
    }

    public static CatalogueRepository creerDepot(ConsoleUi ui) {
        if (ui == null) {
            throw new IllegalArgumentException("ui");
        }
        ui.afficherLigne("Choisir la source de donnees:");
        ui.afficherLigne("1. Memoire (demo)");
        ui.afficherLigne("2. Base de donnees");
        int choix = ui.lireChoix("Choix: ", 1, 2);
        if (choix == 2) {
            try {
                String serveur = ui.lireTexte("Serveur (vide pour defaut): ");
                String base = ui.lireTexte("Base (vide pour defaut): ");
                String login = ui.lireTexte("Login (vide pour defaut): ");
                String motDePasse = ui.lireTexte("Mot de passe (vide pour defaut): ");
                ConnexionMySQL connexion = new ConnexionMySQL();
                connexion.connecter(serveur, base, login, motDePasse);
                ui.afficherLigne("Connexion etablie.");
                return new DbCatalogueRepository(connexion);
            } catch (Exception e) {
                ui.afficherLigne("Connexion impossible, passage en mode memoire.");
            }
        }

        InMemoryCatalogueRepository repo = new InMemoryCatalogueRepository();
        DataStore.chargerDemo(repo);
        return repo;
    }
}
