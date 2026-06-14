package App;

/**
 * Énumération des rôles utilisateur disponibles.
 */
public enum RoleUtilisateur {
    ADMIN,
    UTILISATEUR;

    /**
     * Convertit une chaîne en énumération RoleUtilisateur.
     *
     * @param texte la chaîne à convertir
     * @return le rôle correspondant (UTILISATEUR par défaut)
     */
    public static RoleUtilisateur depuisTexte(String texte) {
        if (texte == null) {
            return UTILISATEUR;
        }
        String valeur = texte.trim().toUpperCase();
        if ("ADMIN".equals(valeur)) {
            return ADMIN;
        }
        if ("UTILISATEUR".equals(valeur) || "USER".equals(valeur)) {
            return UTILISATEUR;
        }
        return UTILISATEUR;
    }
}
