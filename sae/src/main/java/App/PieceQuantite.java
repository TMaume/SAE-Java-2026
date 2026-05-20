package App;

public class PieceQuantite {
    private final Piece piece;
    private final int quantite;
    private final boolean enSupplement;

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

    public Piece getPiece() {
        return piece;
    }

    public int getQuantite() {
        return quantite;
    }

    public boolean isEnSupplement() {
        return enSupplement;
    }
}
