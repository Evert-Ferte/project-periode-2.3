package games;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Game extends Application {
    public Stage stage;
    
    public Game() {
        stage = new Stage();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
//        this.stage = stage;
//        System.out.println("stage set");
    }
    
    public void startGame() {
//        stage.show();
    }
    
    public void resetGame() {
        // ...
    }
}
