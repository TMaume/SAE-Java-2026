package App;

public class AuthentificationService {
    private final ConsoleUi ui;
    private final GestionUtilisateurs gestion;

    public AuthentificationService(ConsoleUi ui, GestionUtilisateurs gestion) {
        if (ui == null) {
            throw new IllegalArgumentException("ui");
        }
        if (gestion == null) {
            throw new IllegalArgumentException("gestion");
        }
        this.ui = ui;
        this.gestion = gestion;
    }

    public Utilisateur demarrer() {
        while (true) {
            ui.afficherTitre("Authentification");
            ui.afficherLigne("1. Se connecter");
            ui.afficherLigne("2. Creer un compte");
            ui.afficherLigne("0. Quitter");
            int choix = ui.lireChoix("Choix : ", 0, 2);
            if (choix == 0) {
                return null;
            }
            if (choix == 1) {
                Utilisateur utilisateur = connecter();
                if (utilisateur != null) {
                    return utilisateur;
                }
                ui.afficherLigne("Identifiants invalides.");
            } else {
                Utilisateur utilisateur = creerCompte();
                if (utilisateur != null) {
                    ui.afficherLigne("Compte cree avec succes.");
                    return utilisateur;
                }
            }
        }
    }

    private Utilisateur connecter() {
        String identifiant = ui.lireTexte("Identifiant : ");
        String motDePasse = ui.lireTexte("Mot de passe : ");
        return gestion.authentifier(identifiant, motDePasse);
    }

    private Utilisateur creerCompte() {
        String identifiant = ui.lireTexte("Choisissez un identifiant : ");
        if (identifiant == null || identifiant.isBlank()) {
            ui.afficherLigne("Identifiant invalide.");
            return null;
        }
        if (gestion.identifiantExiste(identifiant)) {
            ui.afficherLigne("Identifiant deja utilise.");
            return null;
        }
        String motDePasse = ui.lireTexte("Choisissez un mot de passe : ");
        String confirmation = ui.lireTexte("Confirmez le mot de passe : ");
        if (!motDePasse.equals(confirmation)) {
            ui.afficherLigne("Les mots de passe ne correspondent pas.");
            return null;
        }
        return gestion.creerUtilisateur(identifiant, motDePasse);
    }
}
