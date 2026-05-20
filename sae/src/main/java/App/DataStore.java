package App;

public final class DataStore {
    private DataStore() {
    }

    public static void chargerDemo(InMemoryCatalogueRepository repo) {
        Categorie brique = new Categorie(1, "Brique");
        Categorie plaque = new Categorie(2, "Plaque");
        repo.ajouterCategorie(brique);
        repo.ajouterCategorie(plaque);

        Couleur rouge = new Couleur(1, "Rouge", "FF0000", false);
        Couleur bleu = new Couleur(2, "Bleu", "0000FF", false);
        Couleur noir = new Couleur(3, "Noir", "000000", false);
        Couleur gris = new Couleur(4, "Gris", "888888", false);
        repo.ajouterCouleur(rouge);
        repo.ajouterCouleur(bleu);
        repo.ajouterCouleur(noir);
        repo.ajouterCouleur(gris);

        Theme city = new Theme(1, "City", null);
        Theme police = new Theme(2, "Police", 1);
        Theme space = new Theme(3, "Space", null);
        Theme mars = new Theme(4, "Mars", 3);
        repo.ajouterTheme(city);
        repo.ajouterTheme(police);
        repo.ajouterTheme(space);
        repo.ajouterTheme(mars);

        Piece p3001 = new Piece("3001", "Brique 2x4", brique, null);
        Piece p3020 = new Piece("3020", "Plaque 2x4", plaque, null);
        Piece p3062 = new Piece("3062", "Tuile 1x1", plaque, null);
        Piece p3622 = new Piece("3622", "Brique 1x3", brique, null);
        repo.ajouterPiece(p3001);
        repo.ajouterPiece(p3020);
        repo.ajouterPiece(p3062);
        repo.ajouterPiece(p3622);

        Figurine f1 = new Figurine("fig-001", "Policier", 5);
        Figurine f2 = new Figurine("fig-002", "Astronaute", 6);
        repo.ajouterFigurine(f1);
        repo.ajouterFigurine(f2);

        Boite stationPolice = new Boite("60001", "Station Police", 2010, police);
        stationPolice.ajouterPiece(new PieceQuantite(p3001.avecCouleur(rouge), 10, false));
        stationPolice.ajouterPiece(new PieceQuantite(p3020.avecCouleur(bleu), 6, false));
        stationPolice.ajouterPiece(new PieceQuantite(p3062.avecCouleur(noir), 2, true));
        stationPolice.ajouterFigurine(new FigurineQuantite(f1, 2));
        stationPolice.setNbPieces(stationPolice.calculerNbPieces());
        repo.ajouterBoite(stationPolice);

        Boite baseMars = new Boite("70001", "Base Mars", 2012, mars);
        baseMars.ajouterPiece(new PieceQuantite(p3001.avecCouleur(rouge), 8, false));
        baseMars.ajouterPiece(new PieceQuantite(p3020.avecCouleur(gris), 5, false));
        baseMars.ajouterPiece(new PieceQuantite(p3622.avecCouleur(noir), 3, false));
        baseMars.ajouterFigurine(new FigurineQuantite(f2, 1));
        baseMars.setNbPieces(baseMars.calculerNbPieces());
        repo.ajouterBoite(baseMars);

        Boite pack = new Boite("90000", "Pack Aventure", 2015, city);
        pack.ajouterBoiteIncluse(new BoiteQuantite(stationPolice, 1));
        pack.ajouterBoiteIncluse(new BoiteQuantite(baseMars, 1));
        pack.setNbPieces(pack.calculerNbPieces());
        repo.ajouterBoite(pack);
    }
}
