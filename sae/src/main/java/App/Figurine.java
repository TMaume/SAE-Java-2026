package App;

public class Figurine {
    private final String idFigurine;
    private String nom;
    private Integer nbParties;

    public Figurine(String idFigurine, String nom, Integer nbParties) {
        if (idFigurine == null || idFigurine.isBlank()) {
            throw new IllegalArgumentException("idFigurine");
        }
        this.idFigurine = idFigurine;
        this.nom = nom == null ? "" : nom;
        this.nbParties = nbParties;
    }

    public String getIdFigurine() {
        return idFigurine;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom == null ? "" : nom;
    }

    public Integer getNbParties() {
        return nbParties;
    }

    public void setNbParties(Integer nbParties) {
        this.nbParties = nbParties;
    }

    @Override
    public String toString() {
        return idFigurine + " - " + nom;
    }
}
