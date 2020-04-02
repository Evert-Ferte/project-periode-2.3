package games.ABC;

import games.Game;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AbcView extends Game
{
    
    @Override
    public void start(Stage stage) throws Exception {
        // ...
    }
    
    @Override
    public void startGame() {
        Label l = new Label("hello world");
    
        Scene scene = new Scene(l, 400, 400);
        
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void resetGame() {
        // ...
    }
}
