abstract public class Boite {

    private String numBoite;
    private String nomBoite;
    private int annee;
    private int nbPieces;
    private EtatBoite etatBoite;
    private Theme theme;

    public Boite() {
    }

    public void setNumBoite(String newVar) {
        numBoite = newVar;
    }

    public String getNumBoite() {
        return numBoite;
    }

    public void setNomBoite(String newVar) {
        nomBoite = newVar;
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

    public void setEtatBoite(EtatBoite newVar) {
        etatBoite = newVar;
    }

    public EtatBoite getEtatBoite() {
        return etatBoite;
    }

    public void setTheme(Theme newVar) {
        theme = newVar;
    }

    public Theme getTheme() {
        return theme;
    }

    public Contenu obtenirContenu() {
    }

    public String obtenirStatistiques() {
    }

    public String obtenirNumero() {
    }

    public String obtenirNom() {
    }

    public Theme obtenirTheme() {
    }
}
