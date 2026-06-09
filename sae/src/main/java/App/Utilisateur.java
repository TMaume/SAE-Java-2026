package App;

public class Utilisateur {
    private final String identifiant;
    private final String motDePasse;
    private final RoleUtilisateur role;

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

    public String getIdentifiant() {
        return identifiant;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public boolean verifierMotDePasse(String saisie) {
        return motDePasse.equals(saisie);
    }
}
