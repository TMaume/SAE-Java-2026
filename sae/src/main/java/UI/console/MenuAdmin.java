package UI.console;

import java.util.List;
import App.Boite;
import App.BoiteService;
import App.PieceService;
import App.ThemeService;
import App.Categorie;
import App.Piece;
import App.Theme;

public class MenuAdmin {

    static void ajouterBoite(ConsoleUi ui, BoiteService boiteService, ThemeService themeService) {
        String numero = ui.lireTexte("Numéro de la boîte : ");
        String nom = ui.lireTexte("Nom de la boîte : ");
        int annee = ui.lireEntier("Année de sortie : ");
        
        List<Theme> themes = themeService.listerThemes();
        if (themes.isEmpty()) {
            ui.afficherLigne("Aucun thème disponible.");
            return;
        }
        ui.afficherLigne("Thèmes disponibles :");
        for (Theme t : themes) {
            ui.afficherLigne("ID " + t.getIdTheme() + " - " + t.getNom());
        }
        
        int idTheme = ui.lireEntier("ID du thème : ");
        Theme theme = themeService.rechercherTheme(idTheme);
        if (theme == null) {
            ui.afficherLigne("Thème introuvable.");
            return;
        }
        
        Boite b = new Boite(numero, nom, annee, theme);

        boiteService.rechercherBoiteParNumero(numero); 
        ui.afficherLigne("Fonctionnalité d'ajout à lier avec ton BoiteService.");
    }

    static void ajouterPiece(ConsoleUi ui, PieceService pieceService) {
        String numPiece = ui.lireTexte("Numéro de la nouvelle pièce : ");
        String nomPiece = ui.lireTexte("Nom de la pièce : ");
        
        List<Categorie> cats = pieceService.listerCategories();
        for (Categorie c : cats) {
            ui.afficherLigne("ID " + c.getId() + " - " + c.getNom());
        }
        int idCat = ui.lireEntier("ID Catégorie : ");
        Categorie cat = pieceService.rechercherCategorie(idCat);
        
        if(cat != null) {
            Piece p = new Piece(numPiece, nomPiece, cat, null);
            if(pieceService.ajouterPiece(p)) {
                ui.afficherLigne("Pièce ajoutée avec succès !");
            } else {
                ui.afficherLigne("Erreur lors de l'ajout.");
            }
        } else {
            ui.afficherLigne("Catégorie introuvable.");
        }
    }

    static void creerTheme(ConsoleUi ui, ThemeService themeService) {
        int idTheme = ui.lireEntier("ID du nouveau thème : ");
        String nomTheme = ui.lireTexte("Nom du thème : ");
        
        Theme nouveau = new Theme(idTheme, nomTheme, null);
        if(themeService.ajouterTheme(nouveau)) {
            ui.afficherLigne("Thème créé !");
        } else {
            ui.afficherLigne("Erreur lors de la création.");
        }
    }

    static void majContenuBoite(ConsoleUi ui, BoiteService boiteService) {
        String numBoite = ui.lireTexte("Numéro de la boîte à modifier : ");
        App.Boite b = boiteService.rechercherBoiteParNumero(numBoite);
        if (b != null) {
            ui.afficherLigne("Boîte trouvée. (L'ajout/suppression de pièces est à implémenter)");
        } else {
            ui.afficherLigne("Boîte introuvable.");
        }
    }
}