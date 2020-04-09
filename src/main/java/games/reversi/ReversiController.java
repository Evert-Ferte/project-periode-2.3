package games.reversi;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ReversiController {
    public static EventHandler<ActionEvent> ButtonHandler(int x, int y, ReversiModel model) {
        return event -> {
            // When the game mode is player vs player, always let the player click a tile
            if (model.getGameMode() == ReversiModel.GameMode.PLAYER_VS_PLAYER)
                model.clickPositionNew(x, y);
            // When not playing player vs player, but player vs ai or online, only let the player click on his turn
            else if (model.isPlayerTurn())
                model.clickPositionNew(x, y);
        };
    }
    
    public static EventHandler<ActionEvent> setSceneInStage(Stage stage, Scene scene) {
        return event -> {
            if (stage == null) System.out.println("stage null");
            if (scene == null) System.out.println("scene null");
            stage.setScene(scene);
        };
    }
    
    public static EventHandler<ActionEvent> challengePlayer(ReversiModel model, Button btn) {
        return event -> model.challengePlayer(btn);
    }
    
    public static EventHandler<ActionEvent> exitGame(ReversiModel model) {
        return event -> {
            model.forfeitGame();
        };
    }
}
