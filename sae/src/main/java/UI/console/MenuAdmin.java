package UI.console;

import java.util.List;
import App.*;

public class MenuAdmin {

    /**
     * Permet à un administrateur de saisir et d'enregistrer une nouvelle boîte.
     *
     * @param ui l'interface console
     * @param boiteService le service de gestion des boîtes
     * @param themeService le service de gestion des thèmes
     */
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
        
        Boite b = new Boite(numero, nom, annee, theme, null);

        boiteService.rechercherBoiteParNumero(numero); 
        ui.afficherLigne("Fonctionnalité d'ajout à lier avec ton BoiteService.");
    }

    /**
     * Permet à un administrateur de saisir et d'enregistrer une nouvelle pièce.
     *
     * @param ui l'interface console
     * @param pieceService le service de gestion des pièces
     */
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

    /**
     * Permet à un administrateur de créer un nouveau thème.
     *
     * @param ui l'interface console
     * @param themeService le service de gestion des thèmes
     */
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

    /**
     * Permet à un administrateur d'ajouter une pièce au contenu d'une boîte existante.
     *
     * @param ui l'interface console
     * @param boiteService le service de gestion des boîtes
     * @param pieceService le service de gestion des pièces
     */
    public static void majContenuBoite(ConsoleUi ui, BoiteService boiteService, PieceService pieceService) {
        String numBoite = ui.lireTexte("Numéro de la boîte à modifier : ");
        Boite b = boiteService.chargerBoiteComplete(numBoite);
        
        if (b == null) {
            ui.afficherLigne("Boîte introuvable.");
            return;
        }
        
        ui.afficherLigne("Modification du contenu de la boîte : " + b.getNom() + " (" + b.getNumero() + ")");
        ui.afficherLigne("1. Ajouter une pièce");
        ui.afficherLigne("2. Annuler");
        int choix = ui.lireChoix("Choix : ", 1, 2);
        
        if (choix == 1) {
            String numPiece = ui.lireTexte("Numéro de la pièce à ajouter : ");
            Piece p = pieceService.rechercherPiece(numPiece);
            
            if (p != null) {
                int qte = ui.lireEntier("Quantité : ");
                boolean supp = ui.lireOuiNon("Est-ce une pièce en supplément (Extra) ? (o/n) : ");
                
                PieceQuantite pq = new PieceQuantite(p, qte, supp, null);
                
                if (boiteService.ajouterPieceABoite(numBoite, pq)) {
                    ui.afficherLigne("Succès : Pièce ajoutée au contenu de la boîte !");
                } else {
                    ui.afficherLigne("Erreur : Impossible de lier la pièce. La boîte ne possède pas d'identifiant de contenu (Table CONTENU).");
                }
            } else {
                ui.afficherLigne("Erreur : Pièce introuvable dans le catalogue. Veuillez d'abord la créer.");
            }
        }
    }
}