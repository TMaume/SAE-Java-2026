package App;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CatalogueService {
    private final CatalogueInterface depot;
    private final GestionConfirmation confirmation;

    public CatalogueService(CatalogueInterface depot, GestionConfirmation confirmation) {
        if (depot == null) {
            throw new IllegalArgumentException("depot");
        }
        this.depot = depot;
        this.confirmation = confirmation;
    }

    public List<Boite> listerBoites() {
        return depot.listerBoites();
    }

    public List<Theme> listerThemes() {
        return depot.listerThemes();
    }

    public List<Categorie> listerCategories() {
        return depot.listerCategories();
    }

    public List<Couleur> listerCouleurs() {
        return depot.listerCouleurs();
    }

    public List<Figurine> listerFigurines() {
        return depot.listerFigurines();
    }

    public Boite rechercherBoiteParNumero(String numero) {
        return depot.rechercherBoiteParNumero(numero);
    }

    public List<Boite> rechercherBoitesParNom(String nom) {
        return depot.rechercherBoitesParNom(nom);
    }

    public Boite consulterDetailBoite(String numero) {
        return depot.chargerContenuBoite(numero);
    }

    public Theme rechercherTheme(int id) {
        return depot.rechercherTheme(id);
    }

    public Categorie rechercherCategorie(int id) {
        return depot.rechercherCategorie(id);
    }

    public Couleur rechercherCouleur(int id) {
        return depot.rechercherCouleur(id);
    }

    public Piece rechercherPiece(String numero) {
        return depot.rechercherPiece(numero);
    }

    public Figurine rechercherFigurine(String idFigurine) {
        return depot.rechercherFigurine(idFigurine);
    }

    /**
     * Recherche les boites associees a un theme en incluant ses sous-themes.
     */
    public List<Boite> rechercherBoitesParTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("theme");
        }
        Set<Integer> idsThemes = new LinkedHashSet<>();
        Deque<Integer> aExplorer = new ArrayDeque<>();
        aExplorer.add(theme.getIdTheme());
        while (!aExplorer.isEmpty()) {
            int id = aExplorer.removeFirst();
            if (idsThemes.add(id)) {
                for (Theme sousTheme : depot.listerSousThemes(id)) {
                    aExplorer.addLast(sousTheme.getIdTheme());
                }
            }
        }

        List<Boite> res = new ArrayList<>();
        Set<String> vus = new LinkedHashSet<>();
        for (int idTheme : idsThemes) {
            for (Boite boite : depot.listerBoitesParTheme(idTheme)) {
                if (boite != null && vus.add(boite.getNumero())) {
                    res.add(boite);
                }
            }
        }
        return res;
    }

    /**
     * Compose une boite personnalisee a partir d'une selection de pieces existantes.
     */
    public Boite composerBoitePersonnalisee(String nom, List<Piece> pieces) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("nom");
        }
        if (pieces == null || pieces.isEmpty()) {
            throw new IllegalArgumentException("pieces");
        }

        List<Piece> piecesValides = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece == null) {
                throw new IllegalArgumentException("piece");
            }
            Piece base = depot.rechercherPiece(piece.getNumero());
            if (base == null) {
                throw new IllegalArgumentException("Piece inconnue: " + piece.getNumero());
            }
            Couleur couleur = piece.getCouleur();
            if (couleur == null) {
                couleur = couleurParDefaut();
            }
            piecesValides.add(new Piece(base.getNumero(), base.getNom(), base.getCategorie(), couleur));
        }

        if (depot.boiteIdentiqueExiste(nom, piecesValides)) {
            if (confirmation == null || !confirmation.confirmer("Une boite identique existe. Continuer ?")) {
                return null;
            }
        }

        Theme theme = obtenirThemePersonnalisee();
        String numero = depot.genererNumeroBoitePersonnalisee(nom);
        Boite boite = new Boite(numero, nom, null, theme);
        boite.setPersonnalisee(true);

        Map<String, Integer> quantites = new LinkedHashMap<>();
        Map<String, Piece> piecesParCle = new LinkedHashMap<>();
        for (Piece piece : piecesValides) {
            String cle = clePiece(piece);
            quantites.put(cle, quantites.getOrDefault(cle, 0) + 1);
            piecesParCle.putIfAbsent(cle, piece);
        }

        int total = 0;
        for (Map.Entry<String, Integer> entry : quantites.entrySet()) {
            Piece piece = piecesParCle.get(entry.getKey());
            int qte = entry.getValue();
            total += qte;
            boite.ajouterPiece(new PieceQuantite(piece, qte, false));
        }
        boite.setNbPieces(total);

        boolean ok = depot.enregistrerBoitePersonnalisee(boite);
        return ok ? boite : null;
    }

    public BoiteStats calculerStatsBoite(Boite boite) {
        if (boite == null) {
            throw new IllegalArgumentException("boite");
        }
        int total = 0;
        int supplement = 0;
        Map<Couleur, Integer> repartition = new LinkedHashMap<>();
        for (PieceQuantite piece : boite.getPieces()) {
            int qte = piece.getQuantite();
            total += qte;
            if (piece.isEnSupplement()) {
                supplement += qte;
            }
            Couleur couleur = piece.getPiece().getCouleur();
            if (couleur == null) {
                couleur = couleurParDefaut();
            }
            repartition.put(couleur, repartition.getOrDefault(couleur, 0) + qte);
        }
        return new BoiteStats(total, supplement, repartition);
    }

    public List<Boite> rechercherBoitesContenantPiece(String numPiece) {
        List<Boite> res = new ArrayList<>();
        if (numPiece == null || numPiece.isBlank()) {
            return res;
        }
        for (Boite boite : depot.listerBoites()) {
            Boite detail = depot.chargerContenuBoite(boite.getNumero());
            if (detail != null && contientPiece(detail, numPiece)) {
                res.add(detail);
            }
        }
        return res;
    }

    public boolean ajouterBoite(Boite boite) {
        return depot.ajouterBoite(boite);
    }

    public boolean ajouterPiece(Piece piece) {
        return depot.ajouterPiece(piece);
    }

    public boolean ajouterTheme(Theme theme) {
        return depot.ajouterTheme(theme);
    }

    public boolean ajouterContenuPiece(String numBoite, PieceQuantite piece) {
        return depot.ajouterContenuPiece(numBoite, piece);
    }

    public boolean ajouterContenuFigurine(String numBoite, FigurineQuantite figurine) {
        return depot.ajouterContenuFigurine(numBoite, figurine);
    }

    public boolean ajouterContenuBoite(String numBoite, BoiteQuantite boite) {
        return depot.ajouterContenuBoite(numBoite, boite);
    }

    private boolean contientPiece(Boite boite, String numPiece) {
        for (PieceQuantite piece : boite.getPieces()) {
            if (piece.getPiece().getNumero().equalsIgnoreCase(numPiece)) {
                return true;
            }
        }
        return false;
    }

    private Theme obtenirThemePersonnalisee() {
        Theme theme = depot.rechercherThemeParNom("Personnalisee");
        if (theme != null) {
            return theme;
        }
        int nouvelId = prochainIdTheme();
        Theme nouveau = new Theme(nouvelId, "Personnalisee", null);
        depot.ajouterTheme(nouveau);
        return nouveau;
    }

    private int prochainIdTheme() {
        int max = 0;
        for (Theme theme : depot.listerThemes()) {
            if (theme.getIdTheme() > max) {
                max = theme.getIdTheme();
            }
        }
        return max + 1;
    }

    private Couleur couleurParDefaut() {
        List<Couleur> couleurs = depot.listerCouleurs();
        if (!couleurs.isEmpty()) {
            return couleurs.get(0);
        }
        return new Couleur(0, "Inconnue", "000000", false);
    }

    private String clePiece(Piece piece) {
        int idCoul = piece.getCouleur() == null ? 0 : piece.getCouleur().getIdCouleur();
        return piece.getNumero() + "#" + idCoul;
    }
}
