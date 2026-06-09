package UI.console;

import java.util.List;

import App.Boite;
import App.CatalogueService;
import App.Categorie;
import App.Couleur;
import App.FigurineQuantite;
import App.Piece;
import App.PieceQuantite;
import App.Theme;


public class MenuAdmin {


    static void ajouterBoite(ConsoleUi ui, CatalogueService catalogue) {
        String numero = ui.lireTexte("Numéro de la boîte : ");
        String nom = ui.lireTexte("Nom de la boîte : ");
        int annee = ui.lireEntier("Année de sortie : ");
        List<Theme> themes = catalogue.listerThemes();
        if (themes.isEmpty()) {
            ui.afficherLigne("Aucun thème disponible.");
            return;
        }
        ui.afficherLigne("Thèmes disponibles :");
        for (Theme t : themes) {
            ui.afficherLigne("ID " + t.getIdTheme() + " - " + t.getNom());
        }
        int idTheme = ui.lireEntier("ID du thème : ");
        Theme theme = catalogue.rechercherTheme(idTheme);
        if (theme == null) {
            ui.afficherLigne("Thème introuvable.");
            return;
        }
        Boite b = new Boite(numero, nom, annee, theme);
        if (catalogue.ajouterBoite(b)) {
            ui.afficherLigne("Boîte ajoutée au catalogue !");
        } else {
            ui.afficherLigne("Erreur lors de l'ajout (ce numéro existe peut-être déjà).");
        }
    }

    static void ajouterPiece(ConsoleUi ui, CatalogueService catalogue) {
        String numero = ui.lireTexte("Numéro de la pièce : ");
        String nom = ui.lireTexte("Nom de la pièce : ");
        List<Categorie> categories = catalogue.listerCategories();
        if (categories.isEmpty()) {
            ui.afficherLigne("Aucune catégorie disponible.");
            return;
        }
        ui.afficherLigne("Catégories disponibles :");
        for (Categorie c : categories) {
            ui.afficherLigne("ID " + c.getIdCategorie() + " - " + c.getNom());
        }
        int idCat = ui.lireEntier("ID de la catégorie : ");
        Categorie categorie = catalogue.rechercherCategorie(idCat);
        if (categorie == null) {
            ui.afficherLigne("Catégorie introuvable.");
            return;
        }
        Piece p = new Piece(numero, nom, categorie, null);
        if (catalogue.ajouterPiece(p)) {
            ui.afficherLigne("Pièce ajoutée au catalogue !");
        } else {
            ui.afficherLigne("Erreur lors de l'ajout de la pièce.");
        }
    }

    static void creerTheme(ConsoleUi ui, CatalogueService catalogue) {
        int id = ui.lireEntier("ID du thème : ");
        String nom = ui.lireTexte("Nom du thème : ");
        Integer idPere = null;
        if (ui.lireOuiNon("Ce thème a-t-il un thème parent ? ")) {
            int idParent = ui.lireEntier("ID du thème parent : ");
            Theme parent = catalogue.rechercherTheme(idParent);
            if (parent == null) {
                ui.afficherLigne("Thème parent introuvable.");
                return;
            }
            idPere = parent.getIdTheme();
        }
        Theme t = new Theme(id, nom, idPere);
        if (catalogue.ajouterTheme(t)) {
            ui.afficherLigne("Thème créé avec succès !");
        } else {
            ui.afficherLigne("Erreur lors de la création du thème.");
        }
    }

    static void majContenuBoite(ConsoleUi ui, CatalogueService catalogue) {
        String num = ui.lireTexte("Numéro de la boîte à mettre à jour : ");
        Boite b = catalogue.consulterDetailBoite(num);
        if(b == null) {
            ui.afficherLigne("Boîte introuvable.");
            return;
        }
        ui.afficherLigne("1. Ajouter une pièce");
        ui.afficherLigne("2. Ajouter une figurine");
        int choix = ui.lireChoix("Choix : ", 1, 2);
        
        if (choix == 1) {
            String numPiece = ui.lireTexte("Numéro de la pièce : ");
            Piece p = catalogue.rechercherPiece(numPiece);
            if(p != null) {
                List<Couleur> couleurs = catalogue.listerCouleurs();
                if (couleurs.isEmpty()) {
                    ui.afficherLigne("Aucune couleur disponible.");
                    return;
                }
                ui.afficherLigne("Couleurs disponibles :");
                for (Couleur c : couleurs) {
                    ui.afficherLigne("ID " + c.getIdCouleur() + " - " + c.getNom());
                }
                int idCoul = ui.lireEntier("ID de la couleur : ");
                Couleur couleur = catalogue.rechercherCouleur(idCoul);
                if (couleur == null) {
                    ui.afficherLigne("Couleur inconnue.");
                    return;
                }
                int qte = ui.lireEntier("Quantité : ");
                boolean supp = ui.lireOuiNon("Est-ce une pièce en supplément (Extra) ? ");
                Piece pCouleur = p.avecCouleur(couleur);
                if(catalogue.ajouterContenuPiece(num, new PieceQuantite(pCouleur, qte, supp))) {
                    ui.afficherLigne("Pièce ajoutée au contenu de la boîte !");
                }
            } else {
                ui.afficherLigne("Pièce inconnue.");
            }
        } else {
            String numFig = ui.lireTexte("ID figurine : ");
            App.Figurine f = catalogue.rechercherFigurine(numFig);
            if (f != null) {
                int qte = ui.lireEntier("Quantité : ");
                if(catalogue.ajouterContenuFigurine(num, new FigurineQuantite(f, qte))) {
                    ui.afficherLigne("Figurine ajoutée au contenu de la boîte !");
                }
            } else {
                ui.afficherLigne("Figurine inconnue.");
            }
        }
    }
}
