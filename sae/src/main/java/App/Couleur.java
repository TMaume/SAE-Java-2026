package App;

public class Couleur {
    private final int idCouleur;
    private String nom;
    private String rgb;
    private boolean transparent;

    public Couleur(int idCouleur, String nom, String rgb, boolean transparent) {
        this.idCouleur = idCouleur;
        this.nom = nom == null ? "" : nom;
        this.rgb = rgb == null ? "" : rgb;
        this.transparent = transparent;
    }

    public int getIdCouleur() {
        return idCouleur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb == null ? "" : rgb;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    @Override
    public String toString() {
        return idCouleur + " - " + nom + " (" + rgb + ")";
    }
}
