package UI.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import App.Boite;
import App.BoiteService;
import App.PieceService;
import App.ThemeService;
import App.CollectionService;
import App.Piece;
import App.PieceQuantite;
import App.Theme;
import App.BoiteStats;
import App.CollectionItem;
import App.EtatBoite;
import App.FigurineQuantite;
import App.BoiteIdentiqueException;

public class MenuUser {

    public static void rechercherBoite(ConsoleUi ui, BoiteService boiteService) {
        String recherche = ui.lireTexte("Entrez le numéro de la boîte : ");
        Boite b = boiteService.rechercherBoiteParNumero(recherche);
        if (b != null) {
            ui.afficherLigne("Boîte trouvée : " + b.getNom());
        } else {
            ui.afficherLigne("Aucune boîte trouvée avec ce numéro.");
        }
    }

    public static void consulterDetailBoite(ConsoleUi ui, BoiteService boiteService) {
        String num = ui.lireTexte("Numéro de la boîte : ");
        Boite b = boiteService.chargerBoiteComplete(num);
        if (b == null) {
            ui.afficherLigne("Boîte introuvable.");
            return;
        }
        ui.afficherLigne("=== Détails de " + b.getNom() + " (" + b.getNumero() + ") ===");
        ui.afficherLigne("Année : " + b.getAnnee() + " | Thème : " + (b.getTheme() != null ? b.getTheme().getNom() : "Aucun"));
        ui.afficherLigne("\nPièces incluses :");
        for (PieceQuantite pq : b.getPieces()) {
            String supp = pq.isEnSupplement() ? " [Extra]" : "";
            ui.afficherLigne(" - " + pq.getQuantite() + "x " + pq.getPiece().getNom() + supp);
        }
        ui.afficherLigne("\nFigurines incluses :");
        for (FigurineQuantite fq : b.getFigurines()) {
            ui.afficherLigne(" - " + fq.getQuantite() + "x " + fq.getFigurine().getNom());
        }
    }

    public static void explorerParTheme(ConsoleUi ui, ThemeService themeService, BoiteService boiteService) {
        ui.afficherLigne("Thèmes disponibles :");
        for (Theme t : themeService.listerThemes()) {
            ui.afficherLigne("ID " + t.getIdTheme() + " : " + t.getNom());
        }
        int idTheme = ui.lireEntier("Entrez l'ID du thème à explorer : ");
        List<Boite> boites = boiteService.rechercherBoitesParTheme(idTheme);
        if (boites.isEmpty()) {
            ui.afficherLigne("Aucune boîte pour ce thème.");
        } else {
            ui.afficherLigne("Boîtes trouvées :");
            for (Boite b : boites) {
                ui.afficherLigne("- " + b.getNumero() + " : " + b.getNom());
            }
        }
    }

    public static void afficherStatsBoite(ConsoleUi ui, BoiteService boiteService) {
        String num = ui.lireTexte("Numéro de la boîte : ");
        BoiteStats stats = boiteService.calculerStatsBoite(num);
        if (stats != null) {
            ui.afficherLigne("=== Statistiques ===");
            ui.afficherLigne("Total des pièces : " + stats.getTotalPieces());
            ui.afficherLigne("Total des suppléments : " + stats.getTotalSupplement());
            ui.afficherLigne("Répartition par couleur :");
            for (Map.Entry<App.Couleur, Integer> entry : stats.getRepartitionCouleurs().entrySet()) {
                ui.afficherLigne(" - " + entry.getKey().getNom() + " : " + entry.getValue() + " pièces");
            }
        } else {
            ui.afficherLigne("Boîte introuvable.");
        }
    }

    public static void rechercherParPiece(ConsoleUi ui, PieceService pieceService, BoiteService boiteService) {
        String numPiece = ui.lireTexte("Numéro de la pièce : ");
        Piece p = pieceService.rechercherPiece(numPiece);
        if (p != null) {
            ui.afficherLigne("Pièce trouvée : " + p.getNom());
        } else {
            ui.afficherLigne("Pièce introuvable.");
        }
    }

    public static void gererCollection(ConsoleUi ui, CollectionService collection, BoiteService boiteService) {
        ui.afficherLigne("1. Voir ma collection");
        ui.afficherLigne("2. Ajouter une boîte à ma collection");
        int choix = ui.lireChoix("Choix : ", 1, 2);
        if (choix == 1) {
            List<CollectionItem> items = collection.listerCollection();
            if (items.isEmpty()) {
                ui.afficherLigne("Votre collection est vide.");
            } else {
                for (CollectionItem item : items) {
                    ui.afficherLigne(item.getBoite().getNumero() + " - " + item.getBoite().getNom() + " [" + item.getEtat() + "]");
                    if (!item.getPiecesManquantes().isEmpty()) {
                        ui.afficherLigne("    Pièces manquantes :");
                        for (PieceQuantite pq : item.getPiecesManquantes()) {
                            ui.afficherLigne("     > " + pq.getQuantite() + "x " + pq.getPiece().getNom());
                        }
                    }
                }
            }
        } else if (choix == 2) {
            String num = ui.lireTexte("Numéro de la boîte possédée : ");
            Boite b = boiteService.rechercherBoiteParNumero(num);
            if (b != null) {
                collection.ajouterBoite(b, EtatBoite.COMPLETE);
                ui.afficherLigne("Boîte ajoutée à votre collection !");
            } else {
                ui.afficherLigne("Boîte inconnue dans la base de données.");
            }
        }
    }

    public static void composerBoitePerso(ConsoleUi ui, BoiteService boiteService, PieceService pieceService) {
        String nom = ui.lireTexte("Nom de votre nouvelle boîte personnalisée : ");
        List<PieceQuantite> piecesChoisies = new ArrayList<>();
        boolean ajouter = true;
        while (ajouter) {
            String numPiece = ui.lireTexte("Numéro de la pièce à ajouter : ");
            Piece p = pieceService.rechercherPiece(numPiece);
            if (p == null) {
                ui.afficherLigne("Pièce introuvable.");
            } else {
                int qte = ui.lireEntier("Quantité : ");
                boolean supp = ui.lireOuiNon("Est-ce une pièce en supplément ? (o/n) : ");
                piecesChoisies.add(new PieceQuantite(p, qte, supp));
                ui.afficherLigne("Pièce " + p.getNom() + " ajoutée !");
            }
            ajouter = ui.lireOuiNon("Ajouter une autre pièce ? (o/n) : ");
        }
        if (!piecesChoisies.isEmpty()) {
            boolean forcer = false;
            boolean succes = false;
            while (!succes) {
                try {
                    Boite perso = boiteService.composerBoitePersonnalisee(nom, null, piecesChoisies, forcer);
                    if (perso != null) {
                        ui.afficherLigne("Boîte personnalisée créée avec succès : " + perso.getNumero());
                        succes = true;
                    }
                } catch (BoiteIdentiqueException e) {
                    ui.afficherLigne("ATTENTION : " + e.getMessage());
                    forcer = ui.lireOuiNon("Voulez-vous forcer la création de cette boîte identique ? (o/n) : ");
                    if (!forcer) {
                        ui.afficherLigne("Création annulée.");
                        break;
                    }
                }
            }
        }
    }
}