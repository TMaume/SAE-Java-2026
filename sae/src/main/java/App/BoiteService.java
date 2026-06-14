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
    private final ThemeService themeService;

    public BoiteService(BoiteBD boiteBD, Contenu contenuBD, ContenirpBD contenirpBD, ContenirfBD contenirfBD, ContenirbBD contenirbBD, ThemeService themeService) {
        this.boiteBD = boiteBD;
        this.contenuBD = contenuBD;
        this.contenirpBD = contenirpBD;
        this.contenirfBD = contenirfBD;
        this.contenirbBD = contenirbBD;
        this.themeService = themeService;
    }

    public List<Boite> listerBoites() {
        return boiteBD.listeDesBoites();
    }

    public Boite rechercherBoiteParNumero(String numero) {
        return boiteBD.rechercherBoite(numero);
    }

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

    private boolean boiteIdentiqueExiste(List<PieceQuantite> pieces) {
        return false; 
    }

    public List<Boite> rechercherBoitesParNom(String nom) {
        return boiteBD.rechercherBoitesParNom(nom);
    }

    public List<Boite> rechercherBoitesParPiece(String numPiece) {
        return boiteBD.rechercherBoitesParPiece(numPiece);
    }

    public boolean ajouterPieceABoite(String numBoite, PieceQuantite pq) {
        List<Contenu.ContenuDetail> contenus = contenuBD.listeContenusParBoite(numBoite);
        
        if (contenus.isEmpty()) {
            return false;
        }
        
        int idCont = contenus.get(0).getIdCont();

        return contenirpBD.insererContenirp(idCont, pq) > 0;
    }
}