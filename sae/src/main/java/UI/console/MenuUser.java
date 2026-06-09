package UI.console;

import java.util.ArrayList;
import java.util.List;

import App.Boite;
import App.BoiteStats;
import App.CatalogueService;
import App.CollectionItem;
import App.CollectionService;
import App.EtatBoite;
import App.FigurineQuantite;
import App.Piece;
import App.PieceQuantite;
import App.Theme;

public class MenuUser {
    
        static void rechercherBoite(ConsoleUi ui, CatalogueService catalogue) {
        String recherche = ui.lireTexte("Entrez le numéro ou le nom de la boîte : ");
        Boite b = catalogue.rechercherBoiteParNumero(recherche);
        if (b != null) {
            ui.afficherLigne("Boîte trouvée : " + b.toString());
        } else {
            List<Boite> boites = catalogue.rechercherBoitesParNom(recherche);
            if (boites.isEmpty()) {
                ui.afficherLigne("Aucune boîte trouvée.");
            } else {
                ui.afficherLigne("Boîtes trouvées :");
                for (Boite boite : boites) {
                    ui.afficherLigne("- " + boite.toString());
                }
            }
        }
    }

    static void consulterDetailBoite(ConsoleUi ui, CatalogueService catalogue) {
        String num = ui.lireTexte("Numéro de la boîte : ");
        Boite b = catalogue.consulterDetailBoite(num);
        if (b == null) {
            ui.afficherLigne("Boîte introuvable.");
            return;
        }
        ui.afficherTitre("Détails de " + b.getNom() + " (" + b.getNumero() + ")");
        ui.afficherLigne("Année : " + (b.getAnnee() != null ? b.getAnnee() : "Inconnue"));
        ui.afficherLigne("Thème : " + (b.getTheme() != null ? b.getTheme().getNom() : "Aucun"));
        ui.afficherLigne("Pièces contenues : ");
        if (b.getPieces().isEmpty()) ui.afficherLigne("  Aucune pièce enregistrée.");
        for (PieceQuantite pq : b.getPieces()) {
            ui.afficherLigne(" - " + pq.getQuantite() + "x " + pq.getPiece().getNom() + (pq.isEnSupplement() ? " (supplément)" : ""));
        }
        if (!b.getFigurines().isEmpty()) {
            ui.afficherLigne("Figurines : ");
            for (FigurineQuantite fq : b.getFigurines()) {
                ui.afficherLigne(" - " + fq.getQuantite() + "x " + fq.getFigurine().getNom());
            }
        }
    }

    static void explorerParTheme(ConsoleUi ui, CatalogueService catalogue) {
        ui.afficherLigne("Thèmes disponibles :");
        for (Theme t : catalogue.listerThemes()) {
            ui.afficherLigne("ID " + t.getIdTheme() + " - " + t.getNom());
        }
        int idTheme = ui.lireEntier("ID du thème à explorer : ");
        Theme theme = catalogue.rechercherTheme(idTheme);
        if (theme == null) {
            ui.afficherLigne("Thème introuvable.");
            return;
        }
        List<Boite> boites = catalogue.rechercherBoitesParTheme(theme); // Gère les sous-thèmes grâce au service !
        if (boites.isEmpty()) {
            ui.afficherLigne("Aucune boîte trouvée pour ce thème (et ses sous-thèmes).");
        } else {
            for (Boite b : boites) {
                ui.afficherLigne("- " + b.toString());
            }
        }
    }

    static void afficherStatsBoite(ConsoleUi ui, CatalogueService catalogue) {
        String num = ui.lireTexte("Numéro de la boîte : ");
        Boite b = catalogue.consulterDetailBoite(num);
        if (b == null) {
            ui.afficherLigne("Boîte introuvable.");
            return;
        }
        BoiteStats stats = catalogue.calculerStatsBoite(b);
        ui.afficherTitre("Statistiques de la boîte " + b.getNom());
        ui.afficherLigne("Total des pièces : " + stats.getTotalPieces());
        ui.afficherLigne("Pièces en supplément : " + stats.getTotalSupplement());
        ui.afficherLigne("Répartition par couleur :");
        for (var entry : stats.getRepartitionCouleurs().entrySet()) {
            ui.afficherLigne(" - " + entry.getKey().getNom() + " : " + entry.getValue() + " pièce(s)");
        }
    }

    static void rechercherParPiece(ConsoleUi ui, CatalogueService catalogue) {
        String numPiece = ui.lireTexte("Numéro exact de la pièce : ");
        List<Boite> boites = catalogue.rechercherBoitesContenantPiece(numPiece);
        if (boites.isEmpty()) {
            ui.afficherLigne("Aucune boîte répertoriée ne contient cette pièce.");
        } else {
            ui.afficherLigne("Boîtes contenant la pièce :");
            for (Boite b : boites) {
                ui.afficherLigne("- " + b.toString());
            }
        }
    }

    static void gererCollection(ConsoleUi ui, CatalogueService catalogue, CollectionService collection) {
        ui.afficherTitre("Gestion de la Collection");
        ui.afficherLigne("1. Ajouter une boîte à la collection");
        ui.afficherLigne("2. Modifier l'état (Complète/Incomplète) & Déclarer les pièces manquantes");
        ui.afficherLigne("3. Lister ma collection");
        int choix = ui.lireChoix("Choix : ", 1, 3);

        if (choix == 1) {
            String num = ui.lireTexte("Numéro de la boîte : ");
            Boite b = catalogue.rechercherBoiteParNumero(num);
            if (b != null) {
                collection.ajouterBoite(b, EtatBoite.COMPLETE);
                ui.afficherLigne("Boîte ajoutée à votre collection avec succès !");
            } else {
                ui.afficherLigne("Boîte inconnue dans le catalogue.");
            }
        } else if (choix == 2) {
            String num = ui.lireTexte("Numéro de la boîte dans votre collection : ");
            CollectionItem item = collection.obtenirItem(num);
            if (item != null) {
                boolean complete = ui.lireOuiNon("La boîte est-elle complète ? ");
                if (complete) {
                    collection.definirEtat(num, EtatBoite.COMPLETE);
                    ui.afficherLigne("État mis à jour : Complète.");
                } else {
                    collection.definirEtat(num, EtatBoite.INCOMPLETE);
                    if (ui.lireOuiNon("Voulez-vous générer la liste des pièces manquantes maintenant ? ")) {
                        List<PieceQuantite> manquantes = new ArrayList<>();
                        boolean encore = true;
                        while(encore) {
                            String numP = ui.lireTexte("Numéro de la pièce manquante : ");
                            Piece p = catalogue.rechercherPiece(numP);
                            if (p != null) {
                                int qte = ui.lireEntier("Quantité manquante : ");
                                manquantes.add(new PieceQuantite(p, qte, false));
                            } else {
                                ui.afficherLigne("Pièce introuvable dans le catalogue général.");
                            }
                            encore = ui.lireOuiNon("Ajouter une autre pièce manquante ? ");
                        }
                        collection.definirPiecesManquantes(num, manquantes);
                        ui.afficherLigne("Liste des pièces manquantes enregistrée.");
                    }
                }
            } else {
                ui.afficherLigne("Boîte non présente dans votre collection.");
            }
        } else if (choix == 3) {
            List<CollectionItem> items = collection.listerCollection();
            if (items.isEmpty()) {
                ui.afficherLigne("Votre collection est vide.");
            } else {
                for (CollectionItem item : items) {
                    ui.afficherLigne("- " + item.getBoite().getNom() + " (" + item.getEtat() + ")");
                    if (item.getEtat() == EtatBoite.INCOMPLETE && !item.getPiecesManquantes().isEmpty()) {
                        ui.afficherLigne("    Pièces manquantes :");
                        for(PieceQuantite pq : item.getPiecesManquantes()) {
                            ui.afficherLigne("     > " + pq.getQuantite() + "x " + pq.getPiece().getNom());
                        }
                    }
                }
            }
        }
    }

    static void composerBoitePerso(ConsoleUi ui, CatalogueService catalogue) {
        String nom = ui.lireTexte("Nom de votre nouvelle boîte personnalisée : ");
        List<Piece> piecesChoisies = new ArrayList<>();
        boolean ajouter = true;
        
        while(ajouter) {
            String numPiece = ui.lireTexte("Numéro de la pièce à ajouter : ");
            Piece p = catalogue.rechercherPiece(numPiece);
            if(p == null) {
                ui.afficherLigne("Pièce introuvable.");
            } else {
                piecesChoisies.add(p);
                ui.afficherLigne("Pièce " + p.getNom() + " ajoutée !");
            }
            ajouter = ui.lireOuiNon("Ajouter une autre pièce ? ");
        }
        
        if(!piecesChoisies.isEmpty()) {
            Boite perso = catalogue.composerBoitePersonnalisee(nom, piecesChoisies);
            if(perso != null) {
                ui.afficherLigne("Boîte personnalisée créée avec succès sous le numéro : " + perso.getNumero());
            } else {
                ui.afficherLigne("Création annulée ou échouée.");
            }
        }
    }
}
