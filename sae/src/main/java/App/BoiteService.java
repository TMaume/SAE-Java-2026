package App;

import BD.BoiteBD;
import BD.ContenirbBD;
import BD.ContenirfBD;
import BD.ContenirpBD;
import BD.Contenu;
import java.util.List;
import java.util.UUID;

public class BoiteService {
    private final BoiteBD boiteBD;
    private final Contenu contenuBD;
    private final ContenirpBD contenirpBD;
    private final ContenirfBD contenirfBD;
    private final ContenirbBD contenirbBD;

    public BoiteService(BoiteBD boiteBD, Contenu contenuBD, ContenirpBD contenirpBD, ContenirfBD contenirfBD, ContenirbBD contenirbBD) {
        this.boiteBD = boiteBD;
        this.contenuBD = contenuBD;
        this.contenirpBD = contenirpBD;
        this.contenirfBD = contenirfBD;
        this.contenirbBD = contenirbBD;
    }

    public List<Boite> listerBoites() {
        return boiteBD.listeDesBoites();
    }

    public Boite rechercherBoiteParNumero(String numero) {
        return boiteBD.rechercherBoite(numero);
    }

    public List<Boite> rechercherBoitesParTheme(int idTheme) {
        return boiteBD.listeBoitesParTheme(idTheme);
    }

    // Méthode de création de boîte personnalisée respectant les règles métier
    public Boite composerBoitePersonnalisee(String nom, Theme themePersonnalise, List<PieceQuantite> pieces, boolean forcerCreation) throws BoiteIdentiqueException {
        
        // 1. Vérification si une boîte identique existe déjà
        if (!forcerCreation && boiteIdentiqueExiste(pieces)) {
            // Au lieu d'utiliser ConsoleConfirmation, on lève une erreur métier
            // Le menu UI attrapera cette erreur, demandera confirmation à l'utilisateur, 
            // et relancera cette méthode avec forcerCreation = true si l'utilisateur dit oui.
            throw new BoiteIdentiqueException("Une boîte contenant exactement ces pièces existe déjà.");
        }

        // 2. Génération d'un identifiant unique (règle métier)
        String numeroUnique = "PERSO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3. Création de l'objet
        Boite nouvelleBoite = new Boite(numeroUnique, nom, 2026, themePersonnalise);
        nouvelleBoite.setPersonnalisee(true);
        for (PieceQuantite pq : pieces) {
            nouvelleBoite.ajouterPiece(pq);
        }
        
        // 4. Sauvegarde en BD (il faudra d'abord insérer la boite, puis son contenu dans les tables d'association)
        boiteBD.insererBoite(nouvelleBoite);
        // Note: Ici, tu ajouteras tes appels à insererContenu, insererContenirp, etc.
        
        return nouvelleBoite;
    }

    // Logique de vérification (à compléter selon tes besoins)
    private boolean boiteIdentiqueExiste(List<PieceQuantite> pieces) {
        // Ta logique pour vérifier si une boîte possède déjà ces pièces
        // Tu peux rapatrier ton code de l'ancien DbCatalogueInterface ici.
        return false; 
    }
}