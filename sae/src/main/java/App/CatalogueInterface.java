package App;

import java.util.List;

public interface CatalogueInterface {
    List<Boite> listerBoites();

    Boite rechercherBoiteParNumero(String numero);

    List<Boite> rechercherBoitesParNom(String nomPartiel);

    List<Theme> listerThemes();

    Theme rechercherTheme(int idTheme);

    Theme rechercherThemeParNom(String nom);

    List<Theme> listerSousThemes(int idTheme);

    List<Piece> listerPieces();

    Piece rechercherPiece(String numPiece);

    List<Piece> rechercherPiecesParNom(String nomPartiel);

    List<Categorie> listerCategories();

    Categorie rechercherCategorie(int idCategorie);

    List<Couleur> listerCouleurs();

    Couleur rechercherCouleur(int idCouleur);

    List<Figurine> listerFigurines();

    Figurine rechercherFigurine(String idFigurine);

    Boite chargerContenuBoite(String numeroBoite);

    List<Boite> listerBoitesParTheme(int idTheme);

    boolean ajouterBoite(Boite boite);

    boolean ajouterPiece(Piece piece);

    boolean ajouterTheme(Theme theme);

    boolean ajouterContenuPiece(String numBoite, PieceQuantite piece);

    boolean ajouterContenuFigurine(String numBoite, FigurineQuantite figurine);

    boolean ajouterContenuBoite(String numBoite, BoiteQuantite boite);

    boolean boiteIdentiqueExiste(String nom, List<Piece> pieces);

    String genererNumeroBoitePersonnalisee(String nom);

    boolean enregistrerBoitePersonnalisee(Boite boite);
}
