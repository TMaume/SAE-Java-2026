package App;

public enum RoleUtilisateur {
    ADMIN,
    UTILISATEUR;

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
