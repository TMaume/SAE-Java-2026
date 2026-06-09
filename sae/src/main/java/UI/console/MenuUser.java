package UI.console;

import java.util.ArrayList;
import java.util.List;
import App.Boite;
import App.BoiteService;
import App.PieceService;
import App.Piece;
import App.PieceQuantite;
import App.BoiteIdentiqueException;

public class MenuUser {
    
    static void rechercherBoite(ConsoleUi ui, BoiteService boiteService) {
        String recherche = ui.lireTexte("Entrez le numéro de la boîte : ");
        Boite b = boiteService.rechercherBoiteParNumero(recherche);
        if (b != null) {
            ui.afficherLigne("Boîte trouvée : " + b.getNom());
        } else {
            ui.afficherLigne("Aucune boîte trouvée avec ce numéro.");
        }
    }

    static void composerBoitePerso(ConsoleUi ui, BoiteService boiteService, PieceService pieceService) {
        String nom = ui.lireTexte("Nom de votre nouvelle boîte personnalisée : ");
        List<PieceQuantite> piecesChoisies = new ArrayList<>();
        boolean ajouter = true;
        
        while(ajouter) {
            String numPiece = ui.lireTexte("Numéro de la pièce à ajouter : ");
            Piece p = pieceService.rechercherPiece(numPiece);
            if(p == null) {
                ui.afficherLigne("Pièce introuvable.");
            } else {
                int qte = ui.lireEntier("Quantité : ");
                boolean supp = ui.lireOuiNon("Est-ce une pièce en supplément ? (o/n) : ");
                piecesChoisies.add(new PieceQuantite(p, qte, supp));
                ui.afficherLigne("Pièce " + p.getNom() + " ajoutée !");
            }
            ajouter = ui.lireOuiNon("Ajouter une autre pièce ? (o/n) : ");
        }
        
        if(!piecesChoisies.isEmpty()) {
            boolean forcer = false;
            boolean succes = false;
            
            while (!succes) {
                try {
                    // On tente la création (forcer est à false au premier passage)
                    Boite perso = boiteService.composerBoitePersonnalisee(nom, null, piecesChoisies, forcer);
                    if (perso != null) {
                        ui.afficherLigne("Boîte personnalisée créée avec succès : " + perso.getNumero());
                        succes = true;
                    }
                } catch (BoiteIdentiqueException e) {
                    // L'erreur métier est attrapée ici ! C'est l'UI qui décide de la suite.
                    ui.afficherLigne("ATTENTION : " + e.getMessage());
                    forcer = ui.lireOuiNon("Voulez-vous forcer la création de cette boîte identique ? (o/n) : ");
                    if (!forcer) {
                        ui.afficherLigne("Création annulée.");
                        break;
                    }
                    // Si l'utilisateur a dit oui, la boucle recommence avec forcer = true
                }
            }
        }
    }
}