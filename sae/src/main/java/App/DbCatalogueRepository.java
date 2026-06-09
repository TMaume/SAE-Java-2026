package App;

import BD.BoiteBD;
import BD.BoiteBD.BoiteRow;
import BD.CategorieBD;
import BD.CategorieBD.CategorieRow;
import BD.ConnexionMySQL;
import BD.ContenirbBD;
import BD.ContenirbBD.ContenirbRow;
import BD.ContenirfBD;
import BD.ContenirfBD.ContenirfRow;
import BD.ContenirpBD;
import BD.ContenirpBD.ContenirpRow;
import BD.Contenu;
import BD.Contenu.ContenuRow;
import BD.CouleurBD;
import BD.CouleurBD.CouleurRow;
import BD.PieceBD;
import BD.PieceBD.PieceRow;
import BD.ThemeBD;
import BD.ThemeBD.ThemeRow;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DbCatalogueRepository implements CatalogueRepository {
    private final BoiteBD boiteBD;
    private final ThemeBD themeBD;
    private final CategorieBD categorieBD;
    private final CouleurBD couleurBD;
    private final PieceBD pieceBD;
    private final BD.Figurine figurineBD;
    private final Contenu contenuBD;
    private final ContenirpBD contenirpBD;
    private final ContenirfBD contenirfBD;
    private final ContenirbBD contenirbBD;

    public DbCatalogueRepository(ConnexionMySQL connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("connexion");
        }
        this.boiteBD = new BoiteBD(connexion);
        this.themeBD = new ThemeBD(connexion);
        this.categorieBD = new CategorieBD(connexion);
        this.couleurBD = new CouleurBD(connexion);
        this.pieceBD = new PieceBD(connexion);
        this.figurineBD = new BD.Figurine(connexion);
        this.contenuBD = new Contenu(connexion);
        this.contenirpBD = new ContenirpBD(connexion);
        this.contenirfBD = new ContenirfBD(connexion);
        this.contenirbBD = new ContenirbBD(connexion);
    }

    @Override
    public List<Boite> listerBoites() {
        List<Boite> liste = new ArrayList<>();
        for (BoiteRow row : boiteBD.listeDesBoites()) {
            liste.add(convertir(row));
        }
        return liste;
    }

    @Override
    public Boite rechercherBoiteParNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            return null;
        }
        BoiteRow row = boiteBD.rechercherBoite(numero);
        if (row != null) {
            return chargerContenuBoite(numero);
        }
        return null;
    }

    @Override
    public List<Boite> rechercherBoitesParNom(String nomPartiel) {
        List<Boite> liste = new ArrayList<>();
        if (nomPartiel == null) {
            return liste;
        }
        String recherche = nomPartiel.toLowerCase();
        for (Boite b : listerBoites()) {
            if (b.getNom().toLowerCase().contains(recherche)) {
                liste.add(b);
            }
        }
        return liste;
    }

    @Override
    public List<Boite> listerBoitesParTheme(int idTheme) {
        List<Boite> liste = new ArrayList<>();
        for (BoiteRow row : boiteBD.listeBoitesParTheme(idTheme)) {
            liste.add(convertir(row));
        }
        return liste;
    }

    @Override
    public boolean ajouterBoite(Boite boite) {
        if (boite == null) {
            return false;
        }
        int idTheme = boite.getTheme() != null ? boite.getTheme().getIdTheme() : idThemeParDefaut();
        if (idTheme <= 0) {
            return false;
        }
        BoiteRow row = new BoiteRow(boite.getNumero(), boite.getNom(), boite.getAnnee(), boite.getNbPieces(), idTheme);
        return boiteBD.insererBoite(row) > 0;
    }

    @Override
    public Boite chargerContenuBoite(String numeroBoite) {
        if (numeroBoite == null || numeroBoite.isBlank()) {
            return null;
        }
        BoiteRow row = boiteBD.rechercherBoite(numeroBoite);
        if (row == null) {
            return null;
        }
        Boite b = convertir(row);
        for (ContenuRow contenu : contenuBD.listeContenusParBoite(numeroBoite)) {
            int idCont = contenu.getIdCont();
            for (ContenirpRow pieceRow : contenirpBD.listeContenirpParContenu(idCont)) {
                Piece piece = convertirPiece(pieceRow.getNumPiece(), pieceRow.getIdCoul());
                int qte = pieceRow.getQuantite() == null ? 0 : pieceRow.getQuantite();
                b.ajouterPiece(new PieceQuantite(piece, qte, pieceRow.isEnSupplement()));
            }
            for (ContenirfRow figRow : contenirfBD.listeContenirfParContenu(idCont)) {
                Figurine figurine = convertirFigurine(figRow.getIdFig());
                int qte = figRow.getQuantite() == null ? 0 : figRow.getQuantite();
                b.ajouterFigurine(new FigurineQuantite(figurine, qte));
            }
            for (ContenirbRow boiteRow : contenirbBD.listeContenirbParContenu(idCont)) {
                Boite incluse = convertirBoite(boiteRow.getNumBoite());
                int qte = boiteRow.getQuantite() == null ? 1 : boiteRow.getQuantite();
                b.ajouterBoiteIncluse(new BoiteQuantite(incluse, qte));
            }
        }
        return b;
    }

    @Override
    public List<Theme> listerThemes() {
        List<Theme> res = new ArrayList<>();
        for (ThemeRow row : themeBD.listeDesThemes()) {
            res.add(convertir(row));
        }
        return res;
    }

    @Override
    public Theme rechercherTheme(int idTheme) {
        ThemeRow row = themeBD.rechercherTheme(idTheme);
        return row == null ? null : convertir(row);
    }

    @Override
    public Theme rechercherThemeParNom(String nom) {
        if (nom == null) {
            return null;
        }
        for (Theme theme : listerThemes()) {
            if (theme.getNom().equalsIgnoreCase(nom)) {
                return theme;
            }
        }
        return null;
    }

    @Override
    public List<Theme> listerSousThemes(int idTheme) {
        List<Theme> res = new ArrayList<>();
        for (ThemeRow row : themeBD.listeSousThemes(idTheme)) {
            res.add(convertir(row));
        }
        return res;
    }

    @Override
    public List<Piece> listerPieces() {
        List<Piece> res = new ArrayList<>();
        for (PieceRow row : pieceBD.listeDesPieces()) {
            res.add(convertir(row));
        }
        return res;
    }

    @Override
    public Piece rechercherPiece(String numPiece) {
        if (numPiece == null || numPiece.isBlank()) {
            return null;
        }
        PieceRow row = pieceBD.rechercherPiece(numPiece);
        return row == null ? null : convertir(row);
    }

    @Override
    public List<Piece> rechercherPiecesParNom(String nomPartiel) {
        List<Piece> res = new ArrayList<>();
        if (nomPartiel == null) {
            return res;
        }
        String recherche = nomPartiel.toLowerCase();
        for (Piece piece : listerPieces()) {
            if (piece.getNom().toLowerCase().contains(recherche)) {
                res.add(piece);
            }
        }
        return res;
    }

    @Override
    public List<Categorie> listerCategories() {
        List<Categorie> res = new ArrayList<>();
        for (CategorieRow row : categorieBD.listeDesCategories()) {
            res.add(convertir(row));
        }
        return res;
    }

    @Override
    public Categorie rechercherCategorie(int idCategorie) {
        CategorieRow row = categorieBD.rechercherCategorie(idCategorie);
        return row == null ? null : convertir(row);
    }

    @Override
    public List<Couleur> listerCouleurs() {
        List<Couleur> res = new ArrayList<>();
        for (CouleurRow row : couleurBD.listeDesCouleurs()) {
            res.add(convertir(row));
        }
        return res;
    }

    @Override
    public Couleur rechercherCouleur(int idCouleur) {
        CouleurRow row = couleurBD.rechercherCouleur(idCouleur);
        return row == null ? null : convertir(row);
    }

    @Override
    public List<Figurine> listerFigurines() {
        List<Figurine> res = new ArrayList<>();
        for (BD.Figurine.FigurineRow row : figurineBD.listeDesFigurines()) {
            res.add(convertir(row));
        }
        return res;
    }

    @Override
    public Figurine rechercherFigurine(String idFigurine) {
        if (idFigurine == null || idFigurine.isBlank()) {
            return null;
        }
        BD.Figurine.FigurineRow row = figurineBD.rechercherFigurine(idFigurine);
        return row == null ? null : convertir(row);
    }

    @Override
    public boolean ajouterPiece(Piece piece) {
        if (piece == null) {
            return false;
        }
        int idCat = piece.getCategorie() != null ? piece.getCategorie().getIdCategorie() : idCategorieParDefaut();
        if (idCat <= 0) {
            return false;
        }
        PieceRow row = new PieceRow(piece.getNumero(), piece.getNom(), idCat);
        return pieceBD.insererPiece(row) > 0;
    }

    @Override
    public boolean ajouterTheme(Theme theme) {
        if (theme == null) {
            return false;
        }
        ThemeRow row = new ThemeRow(theme.getIdTheme(), theme.getNom(), theme.getIdThemePere());
        return themeBD.insererTheme(row) > 0;
    }

    @Override
    public boolean ajouterContenuPiece(String numBoite, PieceQuantite piece) {
        if (numBoite == null || numBoite.isBlank() || piece == null || piece.getPiece() == null) {
            return false;
        }
        Integer idCont = obtenirOuCreerContenu(numBoite);
        if (idCont == null) {
            return false;
        }
        int idCoul = piece.getPiece().getCouleur() != null
            ? piece.getPiece().getCouleur().getIdCouleur()
            : idCouleurParDefaut();
        if (idCoul <= 0) {
            return false;
        }
        ContenirpRow row = new ContenirpRow(idCont, piece.getPiece().getNumero(), idCoul,
            piece.isEnSupplement(), piece.getQuantite());
        return contenirpBD.insererContenirp(row) > 0;
    }

    @Override
    public boolean ajouterContenuFigurine(String numBoite, FigurineQuantite figurine) {
        if (numBoite == null || numBoite.isBlank() || figurine == null || figurine.getFigurine() == null) {
            return false;
        }
        Integer idCont = obtenirOuCreerContenu(numBoite);
        if (idCont == null) {
            return false;
        }
        ContenirfRow row = new ContenirfRow(idCont, figurine.getFigurine().getIdFigurine(), figurine.getQuantite());
        return contenirfBD.insererContenirf(row) > 0;
    }

    @Override
    public boolean ajouterContenuBoite(String numBoite, BoiteQuantite boite) {
        if (numBoite == null || numBoite.isBlank() || boite == null || boite.getBoite() == null) {
            return false;
        }
        Integer idCont = obtenirOuCreerContenu(numBoite);
        if (idCont == null) {
            return false;
        }
        ContenirbRow row = new ContenirbRow(idCont, boite.getBoite().getNumero(), boite.getQuantite());
        return contenirbBD.insererContenirb(row) > 0;
    }

    @Override
    public boolean boiteIdentiqueExiste(String nom, List<Piece> piecesRecherchees) {
        if (nom == null) {
            return false;
        }
        Map<String, Integer> cible = indexPieces(piecesRecherchees);
        for (Boite boite : listerBoites()) {
            if (!boite.getNom().equalsIgnoreCase(nom)) {
                continue;
            }
            Boite detail = chargerContenuBoite(boite.getNumero());
            if (detail == null) {
                continue;
            }
            Map<String, Integer> existant = indexPiecesQuantite(detail.getPieces());
            if (existant.equals(cible)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String genererNumeroBoitePersonnalisee(String nom) {
        int max = 0;
        for (Boite boite : listerBoites()) {
            String numero = boite.getNumero();
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
        while (rechercherBoiteParNumero(numero) != null) {
            suivant++;
            numero = formaterNumeroPersonnalise(suivant);
        }
        return numero;
    }

    @Override
    public boolean enregistrerBoitePersonnalisee(Boite boite) {
        if (boite == null) {
            return false;
        }
        if (!ajouterBoite(boite)) {
            return false;
        }
        boolean ok = true;
        for (PieceQuantite piece : boite.getPieces()) {
            if (!ajouterContenuPiece(boite.getNumero(), piece)) {
                ok = false;
            }
        }
        return ok;
    }

    private Boite convertir(BoiteRow row) {
        Theme theme = rechercherTheme(row.getIdTheme());
        Boite b = new Boite(row.getNumBoite(), row.getNomBoite(), row.getAnnee(), theme);
        b.setNbPieces(row.getNbPieces());
        return b;
    }

    private Theme convertir(ThemeRow row) {
        return new Theme(row.getIdTheme(), row.getNomTheme(), row.getIdThemePere());
    }

    private Categorie convertir(CategorieRow row) {
        return new Categorie(row.getIdCat(), row.getNomCat());
    }

    private Couleur convertir(CouleurRow row) {
        return new Couleur(row.getIdCoul(), row.getNomCoul(), row.getRgb(), row.isTransparent());
    }

    private Piece convertir(PieceRow row) {
        Categorie categorie = rechercherCategorie(row.getIdCat());
        return new Piece(row.getNumPiece(), row.getNomPiece(), categorie, null);
    }

    private Figurine convertir(BD.Figurine.FigurineRow row) {
        return new Figurine(row.getIdFig(), row.getNomFig(), row.getNbParties());
    }

    private Figurine convertirFigurine(String idFig) {
        BD.Figurine.FigurineRow row = figurineBD.rechercherFigurine(idFig);
        if (row == null) {
            return new Figurine(idFig, "Inconnue", null);
        }
        return convertir(row);
    }

    private Piece convertirPiece(String numPiece, int idCoul) {
        Piece piece = rechercherPiece(numPiece);
        if (piece == null) {
            piece = new Piece(numPiece, "Inconnue", null, null);
        }
        Couleur couleur = rechercherCouleur(idCoul);
        if (couleur != null) {
            piece = piece.avecCouleur(couleur);
        }
        return piece;
    }

    private Boite convertirBoite(String numBoite) {
        BoiteRow row = boiteBD.rechercherBoite(numBoite);
        if (row == null) {
            return new Boite(numBoite, "Inconnue", null, null);
        }
        return convertir(row);
    }

    private Integer obtenirOuCreerContenu(String numBoite) {
        List<ContenuRow> contenus = contenuBD.listeContenusParBoite(numBoite);
        if (!contenus.isEmpty()) {
            return contenus.get(0).getIdCont();
        }
        int nouvelId = prochainIdContenu();
        ContenuRow row = new ContenuRow(nouvelId, 1, numBoite, null);
        if (contenuBD.insererContenu(row) > 0) {
            return nouvelId;
        }
        return null;
    }

    private int prochainIdContenu() {
        int max = 0;
        for (ContenuRow contenu : contenuBD.listeDesContenus()) {
            if (contenu.getIdCont() > max) {
                max = contenu.getIdCont();
            }
        }
        return max + 1;
    }

    private int idThemeParDefaut() {
        List<Theme> themes = listerThemes();
        if (!themes.isEmpty()) {
            return themes.get(0).getIdTheme();
        }
        return -1;
    }

    private int idCategorieParDefaut() {
        List<Categorie> categories = listerCategories();
        if (!categories.isEmpty()) {
            return categories.get(0).getIdCategorie();
        }
        return -1;
    }

    private int idCouleurParDefaut() {
        List<Couleur> couleurs = listerCouleurs();
        if (!couleurs.isEmpty()) {
            return couleurs.get(0).getIdCouleur();
        }
        return -1;
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

    private Map<String, Integer> indexPiecesQuantite(List<PieceQuantite> piecesListe) {
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