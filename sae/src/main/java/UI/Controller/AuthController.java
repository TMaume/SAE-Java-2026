package UI.Controller;

import App.GestionUtilisateurs;
import App.Utilisateur;
import javafx.scene.control.Label;

public class AuthController {
    private final GestionUtilisateurs gestionUtilisateurs;

    public AuthController(GestionUtilisateurs gestionUtilisateurs) {
        this.gestionUtilisateurs = gestionUtilisateurs;
    }

    /**
     * Tente de connecter l'utilisateur.
     */
    public Utilisateur connecter(String identifiant, String motDePasse, Label lblErreur) {
        if (identifiant.isBlank() || motDePasse.isBlank()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return null;
        }

        Utilisateur u = gestionUtilisateurs.authentifier(identifiant, motDePasse);
        if (u == null) {
            lblErreur.setText("Identifiant ou mot de passe incorrect.");
        }
        return u;
    }

    /**
     * Tente de créer un nouveau compte utilisateur.
     */
    public Utilisateur creerCompte(String identifiant, String mdp, String confirmMdp, Label lblErreur) {
        if (identifiant.isBlank() || mdp.isBlank() || confirmMdp.isBlank()) {
            lblErreur.setText("Veuillez remplir tous les champs.");
            return null;
        }
        if (!mdp.equals(confirmMdp)) {
            lblErreur.setText("Les mots de passe ne correspondent pas.");
            return null;
        }
        if (gestionUtilisateurs.identifiantExiste(identifiant)) {
            lblErreur.setText("Cet identifiant est déjà utilisé.");
            return null;
        }

        Utilisateur u = gestionUtilisateurs.creerUtilisateur(identifiant, mdp);
        if (u == null) {
            lblErreur.setText("Erreur lors de la création du compte.");
        }
        return u;
    }
}