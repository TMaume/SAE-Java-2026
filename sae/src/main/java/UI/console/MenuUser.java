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

    /**
     * Recherche une boîte par numéro ou par nom et affiche les résultats.
     *
     * @param ui l'interface console
     * @param boiteService le service de gestion des boîtes
     */
    public static void rechercherBoite(ConsoleUi ui, BoiteService boiteService) {
        String recherche = ui.lireTexte("Entrez le numéro ou le nom de la boîte : ");
        
        // 1. On teste d'abord la recherche par numéro exact
        Boite b = boiteService.rechercherBoiteParNumero(recherche);
        if (b != null) {
            ui.afficherLigne("Boîte trouvée par numéro : " + b.getNumero() + " - " + b.getNom());
        } else {
            // 2. Si pas de numéro, on cherche par nom
            List<Boite> boites = boiteService.rechercherBoitesParNom(recherche);
            if (boites.isEmpty()) {
                ui.afficherLigne("Aucune boîte trouvée avec ce numéro ou ce nom.");
            } else {
                ui.afficherLigne("Boîtes trouvées contenant '" + recherche + "' :");
                for (Boite boite : boites) {
                    ui.afficherLigne("- " + boite.getNumero() + " : " + boite.getNom() + " (" + boite.getAnnee() + ")");
                }
            }
        }
    }

    /**
     * Affiche les détails complets d'une boîte (pièces, figurines, boîtes incluses).
     *
     * @param ui l'interface console
     * @param boiteService le service de gestion des boîtes
     */
    public static void consulterDetailBoite(ConsoleUi ui, BoiteService boiteService) {
        String num = ui.lireTexte("Numéro de la boîte : ");
        Boite b = boiteService.chargerBoiteComplete(num);
        
        if (b == null) {
            ui.afficherLigne("Boîte introuvable.");
            return;
        }
        
        ui.afficherLigne("=== Détails de " + b.getNom() + " (" + b.getNumero() + ") ===");
        ui.afficherLigne("Année : " + b.getAnnee() + " | Thème : " + (b.getTheme() != null ? b.getTheme().getNom() : "Aucun"));
        
        if (!b.getBoitesIncluses().isEmpty()) {
            ui.afficherLigne("\nBoîtes incluses (Pack) :");
            for (App.BoiteQuantite bq : b.getBoitesIncluses()) {
                ui.afficherLigne(" - " + bq.getQuantite() + "x " + bq.getBoite().getNom() + " (" + bq.getBoite().getNumero() + ")");
            }
        }

        ui.afficherLigne("\nPièces incluses :");
        for (PieceQuantite pq : b.getPieces()) {
            String supp = pq.isEnSupplement() ? " [Extra]" : "";
            ui.afficherLigne(" - " + pq.getQuantite() + "x " + pq.getPiece().getNom() + supp);
        }
        
        if (!b.getFigurines().isEmpty()) {
            ui.afficherLigne("\nFigurines incluses :");
            for (App.FigurineQuantite fq : b.getFigurines()) {
                ui.afficherLigne(" - " + fq.getQuantite() + "x " + fq.getFigurine().getNom());
            }
        }
    }

    /**
     * Affiche les boîtes disponibles pour un thème choisi par l'utilisateur.
     *
     * @param ui l'interface console
     * @param themeService le service de gestion des thèmes
     * @param boiteService le service de gestion des boîtes
     */
    public static void explorerParTheme(ConsoleUi ui, ThemeService themeService, BoiteService boiteService) {
        ui.afficherLigne("Thèmes disponibles :");
        for (Theme t : themeService.listerThemes()) {
            ui.afficherLigne("ID " + t.getIdTheme() + " : " + t.getNom());
        }
        
        int idTheme = ui.lireEntier("Entrez l'ID du thème à explorer : ");
        Theme themeChoisi = themeService.rechercherTheme(idTheme);
        
        if (themeChoisi == null) {
            ui.afficherLigne("Thème introuvable.");
            return;
        }

        List<Boite> boites = boiteService.rechercherBoitesParTheme(themeChoisi);
        
        if (boites.isEmpty()) {
            ui.afficherLigne("Aucune boîte pour ce thème (ni pour ses éventuels sous-thèmes).");
        } else {
            ui.afficherLigne("Boîtes trouvées pour le thème '" + themeChoisi.getNom() + "' et ses sous-thèmes :");
            for (Boite b : boites) {
                ui.afficherLigne("- " + b.getNumero() + " : " + b.getNom());
            }
        }
    }

    /**
     * Affiche les statistiques d'une boîte (total pièces, suppléments, répartition couleurs).
     *
     * @param ui l'interface console
     * @param boiteService le service de gestion des boîtes
     */
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

    /**
     * Recherche une pièce et affiche toutes les boîtes qui la contiennent.
     *
     * @param ui l'interface console
     * @param pieceService le service de gestion des pièces
     * @param boiteService le service de gestion des boîtes
     */
    public static void rechercherParPiece(ConsoleUi ui, PieceService pieceService, BoiteService boiteService) {
        String numPiece = ui.lireTexte("Numéro de la pièce : ");
        Piece p = pieceService.rechercherPiece(numPiece);
        
        if (p != null) {
            ui.afficherLigne("Pièce trouvée : " + p.getNom() + " (" + p.getNumero() + ")");
            
            List<Boite> boites = boiteService.rechercherBoitesParPiece(numPiece);
            
            if (boites.isEmpty()) {
                ui.afficherLigne("Cette pièce n'est présente dans aucune boîte de notre base de données.");
            } else {
                ui.afficherLigne("Boîtes contenant cette pièce :");
                for (Boite b : boites) {
                    ui.afficherLigne("- " + b.getNumero() + " : " + b.getNom() + " (" + b.getAnnee() + ")");
                }
            }
        } else {
            ui.afficherLigne("Pièce introuvable dans le catalogue général.");
        }
    }

    /**
     * Permet à l'utilisateur de consulter ou d'alimenter sa collection de boîtes.
     *
     * @param ui l'interface console
     * @param collection le service de gestion de la collection
     * @param boiteService le service de gestion des boîtes
     */
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
                            ui.afficherLigne("     > Il manque " + pq.getQuantite() + "x " + pq.getPiece().getNom() + " (" + pq.getPiece().getNumero() + ")");
                        }
                    }
                }
            }
        } else if (choix == 2) {
            String num = ui.lireTexte("Numéro de la boîte possédée : ");
            
            Boite b = boiteService.chargerBoiteComplete(num);
            
            if (b != null) {
                boolean complete = ui.lireOuiNon("Cette boîte est-elle complète ? (o/n) : ");
                
                if (complete) {
                    collection.ajouterBoite(b, EtatBoite.COMPLETE);
                    ui.afficherLigne("Boîte complète ajoutée à votre collection !");
                } else {
                    collection.ajouterBoite(b, EtatBoite.INCOMPLETE);
                    ui.afficherLigne("Faisons le point sur les pièces manquantes...");
                    
                    List<PieceQuantite> manquantes = new ArrayList<>();
                    
                    for (PieceQuantite pq : b.getPieces()) {
                        if (pq.isEnSupplement()) continue;
                        
                        int qtePossedee = ui.lireEntier("Combien possédez-vous de '" + pq.getPiece().getNom() + "' (Attendues : " + pq.getQuantite() + ") ? : ");
                        
                        if (qtePossedee < pq.getQuantite()) {
                            int calculManque = pq.getQuantite() - qtePossedee;
                            manquantes.add(new PieceQuantite(pq.getPiece(), calculManque, false));
                        }
                    }
                    
                    collection.definirPiecesManquantes(b.getNumero(), manquantes);
                    ui.afficherLigne("Boîte incomplète ajoutée ! Liste des pièces manquantes générée et sauvegardée.");
                }
            } else {
                ui.afficherLigne("Boîte inconnue dans la base de données.");
            }
        }
    }

    /**
     * Permet à l'utilisateur de composer une boîte personnalisée à partir de pièces choisies.
     *
     * @param ui l'interface console
     * @param boiteService le service de gestion des boîtes
     * @param pieceService le service de gestion des pièces
     */
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