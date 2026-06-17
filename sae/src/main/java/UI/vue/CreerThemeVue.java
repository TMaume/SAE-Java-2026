package UI.vue;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import App.Theme;
import App.ThemeService;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import UI.Controller.ControlNvTheme;

public class CreerThemeVue extends VBox{
    
    private GridPane g = new GridPane();
    private ControlNvTheme c;
    private Label titre = new Label("Ajouter un thème");
    private TextField tf = new TextField();
    private TextField tf2 = new TextField();
    private ComboBox<Theme> parent = new ComboBox<>();
    private Button b = new Button("Ajouter le theme");
    private Label lbinfo = new Label("");

    public CreerThemeVue(ThemeService t){
        this.setSpacing(20.0);
        this.setPadding(new Insets(25.0));
        this.setMaxWidth(420);
        this.c = new ControlNvTheme(this,t);
        this.c.chargerThemes();

        g.setHgap(20.0);
        g.setVgap(19.0);
        g.add(new Label("Identifiant"),0,0);
        this.tf.setPrefWidth(300);
        g.add(tf,1,0);
        g.add(new Label("Nom "),0,1);
        this.tf2.setPrefWidth(300);
        g.add(tf2,1,1);
        g.add(new Label("parent(optionel)"),0,2);
        this.parent.setPromptText("Sélectionner un thème");
        this.parent.setPrefWidth(300);
        g.add(parent,1,2);
        this.b.setOnAction(e -> c.ajout());

        this.getChildren().addAll(this.titre,g,this.b,this.lbinfo);
    }

    public void ajout(){

    }

    public void clear(){
        this.tf.clear();
        this.tf2.clear();
        this.parent.getSelectionModel().clearSelection();
    }

    
    public String getTf() {
        return tf.getText();
    }
    public String getTf2() {
        return tf2.getText();
    }
    public Theme getTheme() {
        return parent.getValue();
    }
    public ComboBox<Theme> getCombo(){
        return parent;
    }
    public void setLbinfo(String txt,Color couleur) {
        this.lbinfo.setText(txt);
        this.lbinfo.setTextFill(couleur);
    }
}
