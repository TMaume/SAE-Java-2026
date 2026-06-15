package UI.vue;

import javafx.scene.layout.BorderPane;

public class VuePrincipale extends BorderPane{
private Banniere b = new Banniere();

public VuePrincipale(){
    this.setTop(b);
    this.setLeft(null);
    this.setCenter(null);
    }
}
