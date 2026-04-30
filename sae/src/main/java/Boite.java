import java.util.List;

abstract public class Boite {

    //MCD
    private String numBoite;
    private String nomBoite;
    private int annee;
    private int nbPieces;
    //Liaison
    private Theme theme;
    private List<SousBoite> sousBoites = null;

    public Boite (String numBoite, String nomBoite, int annee, int nbPieces, Theme theme) {
        this.numBoite = numBoite;
        this.nomBoite = nomBoite;
        this.annee = annee;
        this.nbPieces = nbPieces;
        this.theme = theme;
    }

    public void setNumBoite(String newVar) {
        numBoite = newVar;
    }

    public String getNumBoite() {
        return numBoite;
    }

    public String getNomBoite() {
        return nomBoite;
    }

    public void setAnnee(int newVar) {
        annee = newVar;
    }

    public int getAnnee() {
        return annee;
    }

    public void setNbPieces(int newVar) {
        nbPieces = newVar;
    }

    public int getNbPieces() {
        return nbPieces;
    }

    public void setTheme(Theme newVar) {
        theme = newVar;
    }

    public Theme getTheme() {
        return theme;
    }

    // public void completerBoite() {
    //     this.etatBoite.setComplete(true);
    // }

    // public void incompleterBoite() {
    //     this.etatBoite.setComplete(false);
    // }

    // public Contenu obtenirContenu() {

    // }

    // public String obtenirStatistiques() {
    // }

    // public String obtenirNumero() {
    // }

    // public String obtenirNom() {
    // }

    // public Theme obtenirTheme() {
    // }
}
