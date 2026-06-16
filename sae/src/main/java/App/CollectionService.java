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

    // Structures internes pour la sérialisation propre sans boucles de référence
    private static class CollectionItemDTO {
        String numBoite;
        EtatBoite etat;
        List<PieceManquanteDTO> piecesManquantes = new ArrayList<>();
    }

    private static class PieceManquanteDTO {
        String numPiece;
        int idCoul;
        int quantite;
    }

    public CollectionService(Path cheminFichier, BoiteService boiteService, PieceService pieceService) {
        if (cheminFichier == null) {
            throw new IllegalArgumentException("cheminFichier");
        }
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
                    Boite boite = boiteService.chargerBoiteComplete(dto.numBoite);
                    if (boite == null) continue;

                    CollectionItem item = new CollectionItem(boite, dto.etat);

                    if (dto.etat == EtatBoite.INCOMPLETE && dto.piecesManquantes != null) {
                        for (PieceManquanteDTO pmDto : dto.piecesManquantes) {
                            Piece pieceGenerique = pieceService.rechercherPiece(pmDto.numPiece);
                            Couleur couleur = pieceService.rechercherCouleur(pmDto.idCoul);
                            
                            if (pieceGenerique != null && couleur != null) {
                                Piece pieceAvecCouleur = pieceGenerique.avecCouleur(couleur);
                                PieceQuantite pq = new PieceQuantite(pieceAvecCouleur, pmDto.quantite, false, null);
                                item.getPiecesManquantes().add(pq);
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

    public CollectionItem obtenerItem(String numBoite) {
        return collection.get(numBoite);
    }

    public void definirEtat(String numBoite, EtatBoite etat) {
        CollectionItem item = collection.get(numBoite);
        if (item == null) throw new IllegalArgumentException("boite introuvable");
        item.setEtat(etat);
        if (etat == EtatBoite.COMPLETE) {
            item.getPiecesManquantes().clear();
        }
        sauvegarderDansJSON();
    }

    public void definirPiecesManquantes(String numBoite, List<PieceQuantite> pieces) {
        CollectionItem item = collection.get(numBoite);
        if (item == null) throw new IllegalArgumentException("boite introuvable");
        item.getPiecesManquantes().clear();
        if (pieces != null) {
            item.getPiecesManquantes().addAll(pieces);
        }
        item.setEtat(EtatBoite.INCOMPLETE);
        sauvegarderDansJSON();
    }

    public List<CollectionItem> listerCollection() {
        return new ArrayList<>(collection.values());
    }
}