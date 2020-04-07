package games.reversi;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

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
            model.clickPosition(xPos, yPos);
    
            //TODO - add delay for AI move.
//            Timer timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    Platform.runLater(() -> {
//                        // delay
//                    });
//                }
//            }, 0, 3000);
            model.AiMove();
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
