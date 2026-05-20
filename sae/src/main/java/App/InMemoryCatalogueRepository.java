package App;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCatalogueRepository implements CatalogueRepository {
    private final Map<String, Boite> boites = new LinkedHashMap<>();
    private final Map<String, Piece> pieces = new LinkedHashMap<>();
    private final Map<String, Figurine> figurines = new LinkedHashMap<>();
    private final Map<Integer, Theme> themes = new LinkedHashMap<>();
    private final Map<Integer, Categorie> categories = new LinkedHashMap<>();
    private final Map<Integer, Couleur> couleurs = new LinkedHashMap<>();

    public void ajouterCategorie(Categorie categorie) {
        if (categorie != null) {
            categories.put(categorie.getIdCategorie(), categorie);
        }
    }

    public void ajouterCouleur(Couleur couleur) {
        if (couleur != null) {
            couleurs.put(couleur.getIdCouleur(), couleur);
        }
    }

    public void ajouterFigurine(Figurine figurine) {
        if (figurine != null) {
            figurines.put(figurine.getIdFigurine(), figurine);
        }
    }

    @Override
    public List<Boite> listerBoites() {
        return new ArrayList<>(boites.values());
    }

    @Override
    public Boite rechercherBoiteParNumero(String numero) {
        return boites.get(numero);
    }

    @Override
    public List<Boite> rechercherBoitesParNom(String nomPartiel) {
        List<Boite> res = new ArrayList<>();
        String recherche = nomPartiel == null ? "" : nomPartiel.toLowerCase();
        for (Boite boite : boites.values()) {
            if (boite.getNom().toLowerCase().contains(recherche)) {
                res.add(boite);
            }
        }
        return res;
    }

    @Override
    public List<Theme> listerThemes() {
        return new ArrayList<>(themes.values());
    }

    @Override
    public Theme rechercherTheme(int idTheme) {
        return themes.get(idTheme);
    }

    @Override
    public Theme rechercherThemeParNom(String nom) {
        if (nom == null) {
            return null;
        }
        for (Theme theme : themes.values()) {
            if (theme.getNom().equalsIgnoreCase(nom)) {
                return theme;
            }
        }
        return null;
    }

    @Override
    public List<Theme> listerSousThemes(int idTheme) {
        List<Theme> res = new ArrayList<>();
        for (Theme theme : themes.values()) {
            if (theme.getIdThemePere() != null && theme.getIdThemePere() == idTheme) {
                res.add(theme);
            }
        }
        return res;
    }

    @Override
    public List<Piece> listerPieces() {
        return new ArrayList<>(pieces.values());
    }

    @Override
    public Piece rechercherPiece(String numPiece) {
        return pieces.get(numPiece);
    }

    @Override
    public List<Piece> rechercherPiecesParNom(String nomPartiel) {
        List<Piece> res = new ArrayList<>();
        String recherche = nomPartiel == null ? "" : nomPartiel.toLowerCase();
        for (Piece piece : pieces.values()) {
            if (piece.getNom().toLowerCase().contains(recherche)) {
                res.add(piece);
            }
        }
        return res;
    }

    @Override
    public List<Categorie> listerCategories() {
        return new ArrayList<>(categories.values());
    }

    @Override
    public Categorie rechercherCategorie(int idCategorie) {
        return categories.get(idCategorie);
    }

    @Override
    public List<Couleur> listerCouleurs() {
        return new ArrayList<>(couleurs.values());
    }

    @Override
    public Couleur rechercherCouleur(int idCouleur) {
        return couleurs.get(idCouleur);
    }

    @Override
    public List<Figurine> listerFigurines() {
        return new ArrayList<>(figurines.values());
    }

    @Override
    public Figurine rechercherFigurine(String idFigurine) {
        return figurines.get(idFigurine);
    }

    @Override
    public Boite chargerContenuBoite(String numeroBoite) {
        return boites.get(numeroBoite);
    }

    @Override
    public List<Boite> listerBoitesParTheme(int idTheme) {
        List<Boite> res = new ArrayList<>();
        for (Boite boite : boites.values()) {
            if (boite.getTheme() != null && boite.getTheme().getIdTheme() == idTheme) {
                res.add(boite);
            }
        }
        return res;
    }

    @Override
    public boolean ajouterBoite(Boite boite) {
        if (boite == null || boites.containsKey(boite.getNumero())) {
            return false;
        }
        boites.put(boite.getNumero(), boite);
        return true;
    }

    @Override
    public boolean ajouterPiece(Piece piece) {
        if (piece == null || pieces.containsKey(piece.getNumero())) {
            return false;
        }
        pieces.put(piece.getNumero(), piece);
        return true;
    }

    @Override
    public boolean ajouterTheme(Theme theme) {
        if (theme == null || themes.containsKey(theme.getIdTheme())) {
            return false;
        }
        themes.put(theme.getIdTheme(), theme);
        return true;
    }

    @Override
    public boolean ajouterContenuPiece(String numBoite, PieceQuantite piece) {
        Boite boite = boites.get(numBoite);
        if (boite == null) {
            return false;
        }
        boite.ajouterPiece(piece);
        return true;
    }

    @Override
    public boolean ajouterContenuFigurine(String numBoite, FigurineQuantite figurine) {
        Boite boite = boites.get(numBoite);
        if (boite == null) {
            return false;
        }
        boite.ajouterFigurine(figurine);
        return true;
    }

    @Override
    public boolean ajouterContenuBoite(String numBoite, BoiteQuantite boiteIncluse) {
        Boite boite = boites.get(numBoite);
        if (boite == null) {
            return false;
        }
        boite.ajouterBoiteIncluse(boiteIncluse);
        return true;
    }

    @Override
    public boolean boiteIdentiqueExiste(String nom, List<Piece> piecesRecherchees) {
        if (nom == null) {
            return false;
        }
        Map<String, Integer> cible = indexPieces(piecesRecherchees);
        for (Boite boite : boites.values()) {
            if (!boite.isPersonnalisee()) {
                continue;
            }
            if (!boite.getNom().equalsIgnoreCase(nom)) {
                continue;
            }
            Map<String, Integer> existant = indexPieces(boite.getPieces());
            if (existant.equals(cible)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String genererNumeroBoitePersonnalisee(String nom) {
        int max = 0;
        for (String numero : boites.keySet()) {
            if (numero.startsWith("PERS-")) {
                try {
                    int valeur = Integer.parseInt(numero.substring(5));
                    if (valeur > max) {
                        max = valeur;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        int suivant = max + 1;
        String numero = formaterNumeroPersonnalise(suivant);
        while (boites.containsKey(numero)) {
            suivant++;
            numero = formaterNumeroPersonnalise(suivant);
        }
        return numero;
    }

    @Override
    public boolean enregistrerBoitePersonnalisee(Boite boite) {
        return ajouterBoite(boite);
    }

    private Map<String, Integer> indexPieces(List<Piece> piecesListe) {
        Map<String, Integer> res = new LinkedHashMap<>();
        if (piecesListe == null) {
            return res;
        }
        for (Piece piece : piecesListe) {
            if (piece == null) {
                continue;
            }
            String cle = clePiece(piece);
            res.put(cle, res.getOrDefault(cle, 0) + 1);
        }
        return res;
    }

    private Map<String, Integer> indexPieces(List<PieceQuantite> piecesListe) {
        Map<String, Integer> res = new LinkedHashMap<>();
        if (piecesListe == null) {
            return res;
        }
        for (PieceQuantite piece : piecesListe) {
            if (piece == null || piece.getPiece() == null) {
                continue;
            }
            String cle = clePiece(piece.getPiece());
            res.put(cle, res.getOrDefault(cle, 0) + piece.getQuantite());
        }
        return res;
    }

    private String clePiece(Piece piece) {
        int idCoul = piece.getCouleur() == null ? 0 : piece.getCouleur().getIdCouleur();
        return piece.getNumero() + "#" + idCoul;
    }

    private String formaterNumeroPersonnalise(int valeur) {
        return String.format("PERS-%04d", valeur);
    }
}
