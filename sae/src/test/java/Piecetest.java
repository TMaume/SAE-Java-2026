import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {

    @Test
    public void testSetEtgetNumero() {
        Piece piece = new Piece();
        piece.setNumPiece("S100rr");

        assertEquals("S100rr", piece.getNumPiece());
    }

    @Test
    public void testSetEtgetCategorie() {
        Piece piece = new Piece();
        Categorie categorie = new Categorie();
        piece.setCategorie(categorie);
        assertEquals(categorie, piece.getCategorie());
    }

    @Test
    public void testAfficher() {
        Piece piece = new Piece("s1000rr", "motos", "auto/motos");  // pas d'inspiration
        assertEquals("Pièce n°s1000rr : motos", piece.toString());
        
   }
}