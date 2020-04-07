package games.reversi;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ReversiController {
    static class ButtonHandler implements EventHandler<ActionEvent> {
        private int xPos;
        private int yPos;
        private ReversiModel model;
        
        public ButtonHandler(int xPos, int yPos, ReversiModel model) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.model = model;
        }
        
        @Override
        public void handle(ActionEvent event) {
            model.AiMove();
            model.clickPosition(xPos, yPos);
        }
    }
    
    public static EventHandler<ActionEvent> setSceneInStage(Stage stage, Scene scene) {
        return event -> {
            if (stage == null) System.out.println("stage null");
            if (scene == null) System.out.println("scene null");
            stage.setScene(scene);
        };
    }
    
    public static EventHandler<ActionEvent> challengePlayer(ReversiModel model, Button btn) {
        return event -> {
            model.challengePlayer(btn);
        };
    }
}
