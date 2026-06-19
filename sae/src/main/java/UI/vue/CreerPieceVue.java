package UI.vue;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import App.Categorie;
import App.Couleur;
import App.PieceService;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import UI.Controller.ControlNvPiece;


    public class CreerPieceVue extends VBox {

    private GridPane g = new GridPane();
    private ControlNvPiece c;
    private Label titre = new Label("Ajouter une piece");
    private TextField tf = new TextField();
    private TextField tf2 = new TextField();
    private ComboBox<Categorie> cat = new ComboBox<>();
    private ComboBox<Couleur> coul = new ComboBox<>();
    private Button b = new Button("Ajouter le theme");
    private Label lbinfo = new Label("");

    public CreerPieceVue(PieceService p){
        this.setSpacing(20.0);
        this.setPadding(new Insets(25.0));
        this.setMaxWidth(500);
        this.c = new ControlNvPiece(this,p);
        this.c.chargercat();

        g.setHgap(20.0);
        g.setVgap(19.0);
        g.add(new Label("Identifiant"),0,0);
        this.tf.setPrefWidth(300);
        g.add(tf,1,0);
        g.add(new Label("Nom "),0,1);
        this.tf2.setPrefWidth(300);
        g.add(tf2,1,1);
        g.add(new Label("Categorie"),0,2);
        this.cat.setPromptText("Sélectionner une categorie");
        this.cat.setPrefWidth(300);
        g.add(cat,1,2);
        g.add(new Label("couleur"),0,3);
        this.coul.setPromptText("Sélectionner une couleur");
        this.coul.setPrefWidth(300);
        g.add(coul,1,3);
        this.b.setOnAction(e -> c.ajout());
        this.getChildren().addAll(this.titre,g,this.b,this.lbinfo);
    }

    public void ajout(){

    }

    public void clear(){
        this.tf.clear();
        this.tf2.clear();
        this.cat.getSelectionModel().clearSelection();
        this.coul.getSelectionModel().clearSelection();
    }

    
    public String getTf() {
        return tf.getText();
    }
    public String getTf2() {
        return tf2.getText();
    }
    public Categorie getCat() {
        return cat.getValue();
    }
    public Couleur getCoul() {
        return coul.getValue();
    }
    public ComboBox<Categorie> getComboCa(){
        return cat;
    }
    public ComboBox<Couleur> getComboCo(){
        return coul;
    }
    public void setLbinfo(String txt,Color couleur) {
        this.lbinfo.setText(txt);
        this.lbinfo.setTextFill(couleur);
    }
}
