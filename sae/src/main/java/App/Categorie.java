package App;

public class Categorie {
    private final int idCategorie;
    private String nom;

    public Categorie(int idCategorie, String nom) {
        this.idCategorie = idCategorie;
        this.nom = nom == null ? "" : nom;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    @Override
    public String toString() {
        return idCategorie + " - " + nom;
    }
}
