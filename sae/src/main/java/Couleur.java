

public class Couleur {

    private int idCoul;
    private String nomCoul;
    private String rgb;
    private boolean transparent;

    public Couleur() {
    }

    public void setIdCoul(int newVar) {
        idCoul = newVar;
    }

    public int getIdCoul() {
        return idCoul;
    }

    public void setNomCoul(String newVar) {
        nomCoul = newVar;
    }

    public String getNomCoul() {
        return nomCoul;
    }

    public void setRgb(String newVar) {
        rgb = newVar;
    }

    public String getRgb() {
        return rgb;
    }

    public void setTransparent(boolean newVar) {
        transparent = newVar;
    }

    public boolean getTransparent() {
        return transparent;
    }

    public String obtenirNom() {
    }

    public boolean estTransparente() {
    }

    public String afficher() {
    }
}
