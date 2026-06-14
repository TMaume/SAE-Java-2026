package App;

/**
 * Représente une pièce avec une quantité associée.
 */
public class PieceQuantite {
    private final Piece piece;
    private final int quantite;
    private final boolean enSupplement;

    /**
     * Crée une association pièce-quantité.
     *
     * @param piece la pièce (non null)
     * @param quantite la quantité (>= 0)
     * @param enSupplement true si la pièce est en supplément
     * @throws IllegalArgumentException si piece est null ou quantite &lt; 0
     */
    public PieceQuantite(Piece piece, int quantite, boolean enSupplement) {
        if (piece == null) {
            throw new IllegalArgumentException("piece");
        }
        if (quantite < 0) {
            throw new IllegalArgumentException("quantite");
        }
        this.piece = piece;
        this.quantite = quantite;
        this.enSupplement = enSupplement;
    }

    /**
     * Retourne la pièce.
     *
     * @return la pièce
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Retourne la quantité.
     *
     * @return la quantité
     */
    public int getQuantite() {
        return quantite;
    }

    /**
     * Indique si la pièce est en supplément.
     *
     * @return true si en supplément, false sinon
     */
    public boolean isEnSupplement() {
        return enSupplement;
    }
}
