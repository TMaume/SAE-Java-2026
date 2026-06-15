package App;

import BD.PieceBD;
import BD.CategorieBD;
import BD.CouleurBD;
import java.util.List;

/**
 * Gère l'accès aux pièces, catégories et couleurs.
 * <p>
 * Fournit des services pour consulter et manipuler les pièces et leurs propriétés.
 * </p>
 */
public class PieceService {
    private final PieceBD pieceBD;
    private final CategorieBD categorieBD;
    private final CouleurBD couleurBD;

    /**
     * Crée un service de gestion des pièces.
     *
     * @param pieceBD l'accès aux données des pièces
     * @param categorieBD l'accès aux données des catégories
     * @param couleurBD l'accès aux données des couleurs
     */
    public PieceService(PieceBD pieceBD, CategorieBD categorieBD, CouleurBD couleurBD) {
        this.pieceBD = pieceBD;
        this.categorieBD = categorieBD;
        this.couleurBD = couleurBD;
    }

    /**
     * Liste toutes les pièces.
     *
     * @return la liste des pièces
     */
    public List<Piece> listerPieces() {
        return pieceBD.listeDesPieces();
    }

    /**
     * Recherche une pièce par son numéro.
     *
     * @param numero le numéro de la pièce
     * @return la pièce ou null si non trouvée
     */
    public Piece rechercherPiece(String numero) {
        return pieceBD.rechercherPiece(numero);
    }

    /**
     * Ajoute une pièce.
     *
     * @param piece la pièce à ajouter
     * @return true si succès, false sinon
     */
    public boolean ajouterPiece(Piece piece) {
        return pieceBD.insererPiece(piece) > 0;
    }

    /**
     * Liste toutes les catégories de pièces.
     *
     * @return la liste des catégories
     */
    public List<Categorie> listerCategories() {
        return categorieBD.listeDesCategories();
    }

    /**
     * Recherche une catégorie par son identifiant.
     *
     * @param id l'identifiant de la catégorie
     * @return la catégorie ou null si non trouvée
     */
    public Categorie rechercherCategorie(int id) {
        return categorieBD.rechercherCategorie(id);
    }

    /**
     * Liste toutes les couleurs de pièces.
     *
     * @return la liste des couleurs
     */
    public List<Couleur> listerCouleurs() {
        return couleurBD.listeDesCouleurs();
    }

    /**
     * Recherche une couleur par son identifiant.
     *
     * @param id l'identifiant de la couleur
     * @return la couleur ou null si non trouvée
     */
    public Couleur rechercherCouleur(int id) {
        return couleurBD.rechercherCouleur(id);
    }
}