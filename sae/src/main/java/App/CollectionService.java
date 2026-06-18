package App;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CollectionService {
    private final Map<String, CollectionItem> collection = new LinkedHashMap<>();
    private final Path cheminFichier;
    private final BoiteService boiteService;
    private final PieceService pieceService;
    private final Gson gson;

    private static class CollectionItemDTO {
        String numBoite;
        EtatBoite etat;
        boolean construite;           
        String imagePersonnelle;      
        List<PieceManquanteDTO> piecesManquantes = new ArrayList<>();
        
        boolean boitePersonnalisee;
        String nomBoitePerso;
        int anneePerso;
        int idThemePerso;
        String nomThemePerso;
        String imageBoitePerso;

        List<PieceManquanteDTO> piecesPerso = new ArrayList<>();
        List<FigurinePersoDTO> figurinesPerso = new ArrayList<>();
    }

    private static class PieceManquanteDTO {
        String numPiece;
        int idCoul;
        int quantite;
    }

    private static class FigurinePersoDTO {
        String idFig;
        String nomFig;
        int quantite;
    }

    public CollectionService(Path cheminFichier, BoiteService boiteService, PieceService pieceService) {
        if (cheminFichier == null) throw new IllegalArgumentException("cheminFichier");
        this.cheminFichier = cheminFichier;
        this.boiteService = boiteService;
        this.pieceService = pieceService;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        chargerDepuisJSON();
    }

    private void chargerDepuisJSON() {
        collection.clear();
        if (!Files.exists(cheminFichier)) return;

        try (Reader reader = Files.newBufferedReader(cheminFichier, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<CollectionItemDTO>>() {}.getType();
            List<CollectionItemDTO> dtos = gson.fromJson(reader, listType);

            if (dtos != null) {
                for (CollectionItemDTO dto : dtos) {
                    Boite boite = null;
                    if (dto.boitePersonnalisee) {
                        Theme themePerso = new Theme(dto.idThemePerso, dto.nomThemePerso != null ? dto.nomThemePerso : "Inconnu", null);
                        boite = new Boite(dto.numBoite, dto.nomBoitePerso, dto.anneePerso == 0 ? null : dto.anneePerso, themePerso, dto.imageBoitePerso);
                    } else {
                        boite = boiteService.chargerBoiteComplete(dto.numBoite);
                    }

                    if (boite == null) continue;

                    CollectionItem item = new CollectionItem(boite, dto.etat);
                    item.setBoitePersonnalisee(dto.boitePersonnalisee);
                    item.setConstruite(dto.construite);                   
                    item.setImagePersonnelle(dto.imagePersonnelle);       

                    if (dto.etat == EtatBoite.INCOMPLETE && dto.piecesManquantes != null) {
                        for (PieceManquanteDTO pmDto : dto.piecesManquantes) {
                            Piece pieceGenerique = pieceService.rechercherPiece(pmDto.numPiece);
                            Couleur couleur = pieceService.rechercherCouleur(pmDto.idCoul);
                            if (pieceGenerique != null && couleur != null) {
                                item.getPiecesManquantes().add(new PieceQuantite(pieceGenerique.avecCouleur(couleur), pmDto.quantite, false, null));
                            }
                        }
                    }

                    if (dto.boitePersonnalisee) {
                        if (dto.piecesPerso != null) {
                            for (PieceManquanteDTO pPerso : dto.piecesPerso) {
                                Piece p = pieceService.rechercherPiece(pPerso.numPiece);
                                if (p == null) p = new Piece(pPerso.numPiece, "Pièce Inconnue", null, null);
                                item.getPiecesPerso().add(new PieceQuantite(p, pPerso.quantite, false, null));
                            }
                        }
                        if (dto.figurinesPerso != null) {
                            for (FigurinePersoDTO fPerso : dto.figurinesPerso) {
                                Figurine f = new Figurine(fPerso.idFig, fPerso.nomFig != null ? fPerso.nomFig : "Figurine", 0, "");
                                item.getFigurinesPerso().add(new FigurineQuantite(f, fPerso.quantite));
                            }
                        }
                    }
                    collection.put(boite.getNumero(), item);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture JSON: " + e.getMessage());
        }
    }

    public void sauvegarderDansJSON() {
        try {
            Path parent = cheminFichier.getParent();
            if (parent != null) Files.createDirectories(parent);

            List<CollectionItemDTO> dtos = new ArrayList<>();
            for (CollectionItem item : collection.values()) {
                CollectionItemDTO dto = new CollectionItemDTO();
                dto.numBoite = item.getBoite().getNumero();
                dto.etat = item.getEtat();
                dto.construite = item.isConstruite();             
                dto.imagePersonnelle = item.getImagePersonnelle(); 
                
                dto.boitePersonnalisee = item.isBoitePersonnalisee();
                if (item.isBoitePersonnalisee()) {
                    dto.nomBoitePerso = item.getBoite().getNom();
                    dto.anneePerso = item.getBoite().getAnnee() != null ? item.getBoite().getAnnee() : 0;
                    dto.idThemePerso = item.getBoite().getTheme() != null ? item.getBoite().getTheme().getIdTheme() : 0;
                    dto.nomThemePerso = item.getBoite().getTheme() != null ? item.getBoite().getTheme().getNom() : "Inconnu";
                    dto.imageBoitePerso = item.getBoite().getImageBoite();

                    for (PieceQuantite pq : item.getPiecesPerso()) {
                        PieceManquanteDTO pmDto = new PieceManquanteDTO();
                        pmDto.numPiece = pq.getPiece().getNumero();
                        pmDto.idCoul = pq.getPiece().getCouleur() != null ? pq.getPiece().getCouleur().getIdCouleur() : 0;
                        pmDto.quantite = pq.getQuantite();
                        dto.piecesPerso.add(pmDto);
                    }
                    for (FigurineQuantite fq : item.getFigurinesPerso()) {
                        FigurinePersoDTO fDto = new FigurinePersoDTO();
                        fDto.idFig = fq.getFigurine().getIdFigurine();
                        fDto.nomFig = fq.getFigurine().getNom();
                        fDto.quantite = fq.getQuantite();
                        dto.figurinesPerso.add(fDto);
                    }
                }

                if (item.getEtat() == EtatBoite.INCOMPLETE) {
                    for (PieceQuantite pq : item.getPiecesManquantes()) {
                        PieceManquanteDTO pmDto = new PieceManquanteDTO();
                        pmDto.numPiece = pq.getPiece().getNumero();
                        pmDto.idCoul = pq.getPiece().getCouleur() != null ? pq.getPiece().getCouleur().getIdCouleur() : 0;
                        pmDto.quantite = pq.getQuantite();
                        dto.piecesManquantes.add(pmDto);
                    }
                }
                dtos.add(dto);
            }

            try (Writer writer = Files.newBufferedWriter(cheminFichier, StandardCharsets.UTF_8)) {
                gson.toJson(dtos, writer);
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde JSON: " + e.getMessage());
        }
    }

    public void ajouterBoite(Boite boite, EtatBoite etat) {
        if (boite == null) throw new IllegalArgumentException("boite");
        EtatBoite etatFinal = etat == null ? EtatBoite.INCOMPLETE : etat;
        collection.put(boite.getNumero(), new CollectionItem(boite, etatFinal));
        sauvegarderDansJSON();
    }
    
    public void ajouterBoitePersonnalisee(Boite boite, EtatBoite etat, List<PieceQuantite> pieces, List<FigurineQuantite> figurines) {
        if (boite == null) throw new IllegalArgumentException("boite");
        EtatBoite etatFinal = etat == null ? EtatBoite.INCOMPLETE : etat;
        CollectionItem item = new CollectionItem(boite, etatFinal);
        item.setBoitePersonnalisee(true); 
        
        if (pieces != null) item.getPiecesPerso().addAll(pieces);
        if (figurines != null) item.getFigurinesPerso().addAll(figurines);

        collection.put(boite.getNumero(), item);
        sauvegarderDansJSON();
    }

    public CollectionItem obtenerItem(String numBoite) { return collection.get(numBoite); }
    public void definirEtat(String numBoite, EtatBoite etat) { /*...*/ }
    public void definirPiecesManquantes(String numBoite, List<PieceQuantite> pieces) { /*...*/ }
    public List<CollectionItem> listerCollection() { return new ArrayList<>(collection.values()); }
    public void mettreAJourItem(CollectionItem item) {
        if (item != null && item.getBoite() != null) {
            collection.put(item.getBoite().getNumero(), item);
            sauvegarderDansJSON();
        }
    }

    // --- NOUVELLE MÉTHODE DE SUPPRESSION ---
    public void supprimerItem(String numBoite) {
        if (numBoite != null && collection.containsKey(numBoite)) {
            collection.remove(numBoite);
            sauvegarderDansJSON();
        }
    }
}