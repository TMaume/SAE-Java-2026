package App;

import BD.PieceBD;
import BD.CategorieBD;
import BD.CouleurBD;
import java.util.List;

public class PieceService {
    private final PieceBD pieceBD;
    private final CategorieBD categorieBD;
    private final CouleurBD couleurBD;

    public PieceService(PieceBD pieceBD, CategorieBD categorieBD, CouleurBD couleurBD) {
        this.pieceBD = pieceBD;
        this.categorieBD = categorieBD;
        this.couleurBD = couleurBD;
    }

    public List<Piece> listerPieces() {
        return pieceBD.listeDesPieces();
    }

    public Piece rechercherPiece(String numero) {
        return pieceBD.rechercherPiece(numero);
    }

    public boolean ajouterPiece(Piece piece) {
        return pieceBD.insererPiece(piece) > 0;
    }

    public List<Categorie> listerCategories() {
        return categorieBD.listeDesCategories();
    }

    public Categorie rechercherCategorie(int id) {
        return categorieBD.rechercherCategorie(id);
    }

    public List<Couleur> listerCouleurs() {
        return couleurBD.listeDesCouleurs();
    }

    public Couleur rechercherCouleur(int id) {
        return couleurBD.rechercherCouleur(id);
    }
}