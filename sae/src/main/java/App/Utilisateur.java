package App;

/**
 * Représente un utilisateur du système.
 */
public class Utilisateur {
    private final String identifiant;
    private final String motDePasse;
    private final RoleUtilisateur role;

    /**
     * Crée un utilisateur.
     *
     * @param identifiant l'identifiant (non null, non vide)
     * @param motDePasse le mot de passe (non null)
     * @param role le rôle de l'utilisateur (null = UTILISATEUR)
     * @throws IllegalArgumentException si identifiant ou motDePasse est invalide
     */
    public Utilisateur(String identifiant, String motDePasse, RoleUtilisateur role) {
        if (identifiant == null || identifiant.isBlank()) {
            throw new IllegalArgumentException("identifiant");
        }
        if (motDePasse == null) {
            throw new IllegalArgumentException("motDePasse");
        }
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.role = role == null ? RoleUtilisateur.UTILISATEUR : role;
    }

    /**
     * Retourne l'identifiant de l'utilisateur.
     *
     * @return l'identifiant
     */
    public String getIdentifiant() {
        return identifiant;
    }

    /**
     * Retourne le rôle de l'utilisateur.
     *
     * @return le rôle
     */
    public RoleUtilisateur getRole() {
        return role;
    }

    /**
     * Vérifie si le mot de passe saisi correspond.
     *
     * @param saisie le mot de passe à vérifier
     * @return true si le mot de passe est correct
     */
    public boolean verifierMotDePasse(String saisie) {
        return motDePasse.equals(saisie);
    }
}
