package App;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'authentification et la création des utilisateurs.
 * <p>
 * Stocke les utilisateurs dans un fichier CSV et fournit des méthodes
 * pour l'authentification et la gestion des comptes.
 * </p>
 */
public class GestionUtilisateurs {
    private final Path cheminFichier;

    /**
     * Crée un gestionnaire d'utilisateurs.
     *
     * @param cheminFichier le chemin du fichier CSV (non null)
     * @throws IllegalArgumentException si cheminFichier est null
     */
    public GestionUtilisateurs(Path cheminFichier) {
        if (cheminFichier == null) {
            throw new IllegalArgumentException("cheminFichier");
        }
        this.cheminFichier = cheminFichier;
        initialiserFichier();
    }

    /**
     * Retourne le chemin par défaut du fichier utilisateurs.
     *
     * @return le chemin du fichier
     */
    public static Path cheminParDefaut() {
        Path direct = Paths.get("utilisateurs.csv");
        if (Files.exists(direct)) {
            return direct;
        }
        Path dansSae = Paths.get("sae", "utilisateurs.csv");
        if (Files.exists(dansSae)) {
            return dansSae;
        }
        return direct;
    }

    /**
     * Retourne le chemin du fichier utilisateurs.
     *
     * @return le chemin
     */
    public Path getCheminFichier() {
        return cheminFichier;
    }

    /**
     * Authentifie un utilisateur.
     *
     * @param identifiant l'identifiant de l'utilisateur
     * @param motDePasse le mot de passe
     * @return l'utilisateur authentifié ou null
     */
    public Utilisateur authentifier(String identifiant, String motDePasse) {
        if (identifiant == null || motDePasse == null) {
            return null;
        }
        for (Utilisateur utilisateur : listerUtilisateurs()) {
            if (utilisateur.getIdentifiant().equalsIgnoreCase(identifiant)
                && utilisateur.verifierMotDePasse(motDePasse)) {
                return utilisateur;
            }
        }
        return null;
    }

    /**
     * Vérifie si un identifiant existe.
     *
     * @param identifiant l'identifiant à vérifier
     * @return true si l'identifiant existe
     */
    public boolean identifiantExiste(String identifiant) {
        if (identifiant == null || identifiant.isBlank()) {
            return false;
        }
        for (Utilisateur utilisateur : listerUtilisateurs()) {
            if (utilisateur.getIdentifiant().equalsIgnoreCase(identifiant)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crée un nouvel utilisateur avec le rôle UTILISATEUR.
     *
     * @param identifiant l'identifiant de l'utilisateur
     * @param motDePasse le mot de passe
     * @return l'utilisateur créé ou null si erreur
     */
    public Utilisateur creerUtilisateur(String identifiant, String motDePasse) {
        return creerUtilisateur(identifiant, motDePasse, RoleUtilisateur.UTILISATEUR);
    }

    /**
     * Crée un nouvel utilisateur avec un rôle spécifique.
     *
     * @param identifiant l'identifiant de l'utilisateur
     * @param motDePasse le mot de passe
     * @param role le rôle de l'utilisateur
     * @return l'utilisateur créé ou null si erreur
     */
    public Utilisateur creerUtilisateur(String identifiant, String motDePasse, RoleUtilisateur role) {
        if (identifiant == null || identifiant.isBlank()) {
            return null;
        }
        if (motDePasse == null || motDePasse.isBlank()) {
            return null;
        }
        if (identifiantExiste(identifiant)) {
            return null;
        }
        if (ajouterLigne(identifiant, motDePasse, role)) {
            return new Utilisateur(identifiant, motDePasse, role);
        }
        return null;
    }

    /**
     * Liste tous les utilisateurs.
     *
     * @return la liste des utilisateurs
     */
    public List<Utilisateur> listerUtilisateurs() {
        List<Utilisateur> res = new ArrayList<>();
        List<String> lignes = lireLignes();
        for (String ligne : lignes) {
            if (ligne == null || ligne.trim().isEmpty()) {
                continue;
            }
            String ligneNet = ligne.trim();
            if (ligneNet.toLowerCase().startsWith("login;")) {
                continue;
            }
            String[] morceaux = ligneNet.split(";", -1);
            if (morceaux.length < 3) {
                continue;
            }
            String identifiant = morceaux[0].trim();
            String motDePasse = morceaux[1];
            RoleUtilisateur role = RoleUtilisateur.depuisTexte(morceaux[2]);
            if (!identifiant.isEmpty()) {
                res.add(new Utilisateur(identifiant, motDePasse, role));
            }
        }
        return res;
    }

    private void initialiserFichier() {
        if (Files.exists(cheminFichier)) {
            return;
        }
        try {
            Path parent = cheminFichier.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lignes = new ArrayList<>();
            lignes.add("login;motDePasse;role");
            lignes.add("admin;admin;ADMIN");
            lignes.add("user;user;UTILISATEUR");
            Files.write(cheminFichier, lignes, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            System.err.println("Impossible d'initialiser le fichier utilisateurs: " + e.getMessage());
        }
    }

    private List<String> lireLignes() {
        try {
            if (!Files.exists(cheminFichier)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(cheminFichier, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erreur lecture utilisateurs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean ajouterLigne(String identifiant, String motDePasse, RoleUtilisateur role) {
        String ligne = identifiant + ";" + motDePasse + ";" + role;
        try {
            Files.write(cheminFichier, List.of(ligne), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur ecriture utilisateurs: " + e.getMessage());
            return false;
        }
    }
}
