public class Figurine {

    private String idFig;
    private String nomFig;
    private int nbParties;

    public Figurine() {
    }

    public void setIdFig(String newVar) {
        idFig = newVar;
    }

    public String getIdFig() {
        return idFig;
    }

    public void setNomFig(String newVar) {
        nomFig = newVar;
    }

    public String getNomFig() {
        return nomFig;
    }

    public void setNbParties(int newVar) {
        nbParties = newVar;
    }

    public int getNbParties() {
        return nbParties;
    }

    @Override
    public String toString() {
        return "Figurine{" +
                "idFig='" + idFig + '\'' +
                ", nomFig='" + nomFig + '\'' +
                ", nbParties=" + nbParties +
                '}';
    }
}
