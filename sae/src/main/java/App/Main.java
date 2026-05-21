package App;

import BD.ConnexionMySQL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUi ui = new ConsoleUi(scanner);
        ConsoleConfirmation confirmation = new ConsoleConfirmation(ui);

        ui.afficherTitre("Bienvenue dans Briqu'IUTO - Gestionnaire de Lego");

        GestionUtilisateurs gestionUtilisateurs = new GestionUtilisateurs(GestionUtilisateurs.cheminParDefaut());
        AuthentificationService authentification = new AuthentificationService(ui, gestionUtilisateurs);
        Utilisateur utilisateur = authentification.demarrer();
        if (utilisateur == null) {
            ui.afficherLigne("Au revoir !");
            scanner.close();
            return;
        }

        CatalogueRepository depot = creerDepotBase(ui);
        if (depot == null) {
            scanner.close();
            return;
        }
        CatalogueService catalogue = new CatalogueService(depot, confirmation);
        CollectionService collection = new CollectionService();

        boolean estAdmin = utilisateur.getRole() == RoleUtilisateur.ADMIN;

        boolean continuer = true;
        while (continuer) {
            ui.afficherTitre("Menu Principal");
            ui.afficherLigne("Connecte : " + utilisateur.getIdentifiant() + " (" + utilisateur.getRole() + ")");
            ui.afficherLigne("--- Utilisateur (Collectionneur) ---");
            ui.afficherLigne("1. Rechercher une boîte (numéro ou nom)");
            ui.afficherLigne("2. Consulter le détail d'une boîte");
            ui.afficherLigne("3. Explorer les boîtes par thème");
            ui.afficherLigne("4. Afficher les statistiques d'une boîte");
            ui.afficherLigne("5. Rechercher les boîtes contenant une pièce précise");
            ui.afficherLigne("6. Gérer ma collection personnelle (Ajout, État, Pièces manquantes)");
            ui.afficherLigne("7. Composer une boîte personnalisée");
            if (estAdmin) {
                ui.afficherLigne("--- Administrateur ---");
                ui.afficherLigne("8. Ajouter une boîte dans la base");
                ui.afficherLigne("9. Ajouter une pièce");
                ui.afficherLigne("10. Créer un thème");
                ui.afficherLigne("11. Mettre à jour le contenu d'une boîte (Ajout pièce/figurine)");
            }
            ui.afficherLigne("0. Quitter");

            int maxChoix = estAdmin ? 11 : 7;
            int choix = ui.lireChoix("Votre choix : ", 0, maxChoix);

            try {
                switch (choix) {
                    case 0:
                        continuer = false;
                        ui.afficherLigne("Au revoir !");
                        break;
                    case 1: rechercherBoite(ui, catalogue); break;
                    case 2: consulterDetailBoite(ui, catalogue); break;
                    case 3: explorerParTheme(ui, catalogue); break;
                    case 4: afficherStatsBoite(ui, catalogue); break;
                    case 5: rechercherParPiece(ui, catalogue); break;
                    case 6: gererCollection(ui, catalogue, collection); break;
                    case 7: composerBoitePerso(ui, catalogue); break;
                    case 8: ajouterBoite(ui, catalogue); break;
                    case 9: ajouterPiece(ui, catalogue); break;
                    case 10: creerTheme(ui, catalogue); break;
                    case 11: majContenuBoite(ui, catalogue); break;
                }
            } catch (Exception e) {
                ui.afficherLigne("Erreur inattendue : " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static CatalogueRepository creerDepotBase(ConsoleUi ui) {
        try {
            ConnexionMySQL connexion = new ConnexionMySQL();
            connexion.connecter(null, null, null, null);
            ui.afficherLigne("Connexion a la base etablie.");
            return new DbCatalogueRepository(connexion);
        } catch (Exception e) {
            ui.afficherLigne("Connexion impossible a la base: " + e.getMessage());
            return null;
        }
    }

    private static void rechercherBoite(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void consulterDetailBoite(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void explorerParTheme(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void afficherStatsBoite(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void rechercherParPiece(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void gererCollection(ConsoleUi ui, CatalogueService catalogue, CollectionService collection) {
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

    private static void composerBoitePerso(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void ajouterBoite(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void ajouterPiece(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void creerTheme(ConsoleUi ui, CatalogueService catalogue) {
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

    private static void majContenuBoite(ConsoleUi ui, CatalogueService catalogue) {
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
            Figurine f = catalogue.rechercherFigurine(numFig);
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