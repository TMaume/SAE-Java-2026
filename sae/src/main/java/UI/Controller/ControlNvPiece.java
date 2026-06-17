package UI.Controller;

import java.util.List;

import App.Categorie;
import App.Couleur;
import App.Piece;
import App.PieceService;
import App.Theme;
import UI.vue.CreerPieceVue;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

public class ControlNvPiece {
    private CreerPieceVue vue;
    private PieceService model;

    public ControlNvPiece(CreerPieceVue v,PieceService t){
        this.vue = v;
        this.model = t;
    }
    

    public void chargercat(){
        if (model != null) {
            List<Categorie> cats = model.listerCategories();
            if (cats.isEmpty()) {
                vue.setLbinfo("Aucun thème disponible en base de données.", Color.RED);
            } else {
                vue.getComboCa().setItems(FXCollections.observableArrayList(cats));
            }
        }
    }
    public void chargercoul(){
        if (model != null) {
            List<Couleur> coul = model.listerCouleurs();
            if (coul.isEmpty()) {
                vue.setLbinfo("Aucun thème disponible en base de données.", Color.RED);
            } else {
                vue.getComboCo().setItems(FXCollections.observableArrayList(coul));
            }
        }
    }

    public void ajout(){
        try{Integer.parseInt(vue.getTf());}catch(NumberFormatException e){vue.setLbinfo("ID de theme non valide", Color.RED);return;}
        if (!model.idvalide(Integer.parseInt(vue.getTf()))){vue.setLbinfo("ID de theme deja existante", Color.RED);return;}
        if((!(vue.getTf() == null) && !(vue.getTf2() == null)))model.ajouterPiece(new Piece(vue.getTf(), vue.getTf2(), vue.getCat(),vue.getCoul()));
        else;
        vue.setLbinfo("Nouveau theme ajouté avec succes",Color.GREEN);
    }
}
