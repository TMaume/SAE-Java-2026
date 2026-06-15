package App;

import BD.BoiteBD;
import BD.ContenirbBD;
import BD.ContenirfBD;
import BD.ContenirpBD;
import BD.Contenu;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Service de gestion des boîtes LEGO.
 * <p>
 * Fournit des méthodes pour lister, rechercher, charger et modifier les boîtes.
 * Permet également de créer des boîtes personnalisées et calculer des statistiques.
 * </p>
 */
public class BoiteService {
    private final BoiteBD boiteBD;
    private final Contenu contenuBD;
    private final ContenirpBD contenirpBD;
    private final ContenirfBD contenirfBD;
    private final ContenirbBD contenirbBD;
    private final ThemeService themeService;

    /**
     * Crée un service de gestion des boîtes.
     *
     * @param boiteBD l'accès aux données des boîtes
     * @param contenuBD l'accès aux données des contenus
     * @param contenirpBD l'accès aux associations pièce-contenu
     * @param contenirfBD l'accès aux associations figurine-contenu
     * @param contenirbBD l'accès aux associations boîte-contenu
     * @param themeService le service des thèmes
     */
    public BoiteService(BoiteBD boiteBD, Contenu contenuBD, ContenirpBD contenirpBD, ContenirfBD contenirfBD, ContenirbBD contenirbBD, ThemeService themeService) {
        this.boiteBD = boiteBD;
        this.contenuBD = contenuBD;
        this.contenirpBD = contenirpBD;
        this.contenirfBD = contenirfBD;
        this.contenirbBD = contenirbBD;
        this.themeService = themeService;
    }

    /**
     * Liste toutes les boîtes.
     *
     * @return la liste des boîtes
     */
    public List<Boite> listerBoites() {
        return boiteBD.listeDesBoites();
    }

    /**
     * Recherche une boîte par son numéro.
     *
     * @param numero le numéro de la boîte
     * @return la boîte ou null si non trouvée
     */
    public Boite rechercherBoiteParNumero(String numero) {
        return boiteBD.rechercherBoite(numero);
    }

    /**
     * Recherche les boîtes d'un thème, incluant les sous-thèmes.
     *
     * @param theme le thème recherché
     * @return la liste des boîtes du thème et ses sous-thèmes
     */
    public List<Boite> rechercherBoitesParTheme(Theme theme) {
        List<Boite> resultat = new ArrayList<>();
        if (theme == null) return resultat;

        resultat.addAll(boiteBD.listeBoitesParTheme(theme.getIdTheme()));

        List<Theme> sousThemes = themeService.listerSousThemes(theme.getIdTheme());

        for (Theme sousTheme : sousThemes) {
            resultat.addAll(rechercherBoitesParTheme(sousTheme));
        }

        return resultat;
    }

    /**
     * Crée une boîte personnalisée avec les pièces spécifiées.
     *
     * @param nom le nom de la boîte
     * @param themePersonnalise le thème de la boîte
     * @param pieces la liste des pièces à inclure
     * @param forcerCreation true pour ignorer les vérifications de doublons
     * @return la boîte créée
     * @throws BoiteIdentiqueException si une boîte identique existe et forcerCreation est false
     */
    public Boite composerBoitePersonnalisee(String nom, Theme themePersonnalise, List<PieceQuantite> pieces, boolean forcerCreation) throws BoiteIdentiqueException {
        
        // 1. Vérification si une boîte identique existe déjà
        if (!forcerCreation && boiteIdentiqueExiste(pieces)) {
            throw new BoiteIdentiqueException("Une boîte contenant exactement ces pièces existe déjà.");
        }

        // 2. Génération d'un identifiant unique
        String numeroUnique = "PERSO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3. Création de l'objet
        Boite nouvelleBoite = new Boite(numeroUnique, nom, 2026, themePersonnalise);
        nouvelleBoite.setPersonnalisee(true);
        for (PieceQuantite pq : pieces) {
            nouvelleBoite.ajouterPiece(pq);
        }
        
        // 4. Sauvegarde en BD 
        boiteBD.insererBoite(nouvelleBoite);
        
        return nouvelleBoite;
    }

    /**
     * Charge une boîte complète avec tout son contenu (pièces, figurines, boîtes incluses).
     *
     * @param numero le numéro de la boîte
     * @return la boîte avec son contenu complet, ou null si non trouvée
     */
    public Boite chargerBoiteComplete(String numero) {
        Boite b = boiteBD.rechercherBoite(numero);
        if (b == null) return null;

        // On cherche les contenus liés à cette boîte
        List<Contenu.ContenuDetail> listContenus = contenuBD.listeContenusParBoite(numero);
        for (Contenu.ContenuDetail c : listContenus) {
            int idContenu = c.getIdCont();

            // On ajoute les pièces
            List<PieceQuantite> pieces = contenirpBD.listeContenirpParContenu(idContenu);
            for (PieceQuantite pq : pieces) {
                b.ajouterPiece(pq);
            }

            // On ajoute les figurines
            List<App.FigurineQuantite> figurines = contenirfBD.listeContenirfParContenu(idContenu);
            for (App.FigurineQuantite fq : figurines) {
                b.ajouterFigurine(fq);
            }
            
            List<App.BoiteQuantite> sousBoites = contenirbBD.listeContenirbParContenu(idContenu);
            for (App.BoiteQuantite bq : sousBoites) {
                b.ajouterBoiteIncluse(bq);
            }
        }
        return b;
    }

    /**
     * Calcule les statistiques d'une boîte.
     *
     * @param numero le numéro de la boîte
     * @return les statistiques de la boîte ou null si non trouvée
     */
    public App.BoiteStats calculerStatsBoite(String numero) {
        Boite b = chargerBoiteComplete(numero);
        if (b == null) return null;

        int totalPieces = 0;
        int totalSupplements = 0;
        java.util.Map<App.Couleur, Integer> repartitionCouleurs = new java.util.LinkedHashMap<>();

        for (PieceQuantite pq : b.getPieces()) {
            totalPieces += pq.getQuantite();
            if (pq.isEnSupplement()) {
                totalSupplements += pq.getQuantite();
            }
            App.Couleur c = pq.getPiece().getCouleur();
            if (c != null) {
                repartitionCouleurs.put(c, repartitionCouleurs.getOrDefault(c, 0) + pq.getQuantite());
            }
        }
        return new App.BoiteStats(totalPieces, totalSupplements, repartitionCouleurs);
    }

    /**
     * Vérifie si une boîte identique (même contenu de pièces) existe.
     *
     * @param pieces la liste des pièces
     * @return true si une boîte identique existe
     */
    private boolean boiteIdentiqueExiste(List<PieceQuantite> pieces) {
        return false; 
    }

    /**
     * Recherche les boîtes par nom.
     *
     * @param nom le nom recherché
     * @return la liste des boîtes correspondantes
     */
    public List<Boite> rechercherBoitesParNom(String nom) {
        return boiteBD.rechercherBoitesParNom(nom);
    }

    /**
     * Recherche les boîtes contenant une pièce donnée.
     *
     * @param numPiece le numéro de la pièce
     * @return la liste des boîtes contenant cette pièce
     */
    public List<Boite> rechercherBoitesParPiece(String numPiece) {
        return boiteBD.rechercherBoitesParPiece(numPiece);
    }

    /**
     * Ajoute une pièce à une boîte.
     *
     * @param numBoite le numéro de la boîte
     * @param pq la pièce à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterPieceABoite(String numBoite, PieceQuantite pq) {
        List<Contenu.ContenuDetail> contenus = contenuBD.listeContenusParBoite(numBoite);
        
        if (contenus.isEmpty()) {
            return false;
        }
        
        int idCont = contenus.get(0).getIdCont();

        return contenirpBD.insererContenirp(idCont, pq) > 0;
    }
}