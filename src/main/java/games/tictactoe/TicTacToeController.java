package games.tictactoe;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TicTacToeController {
    public static EventHandler<ActionEvent> ButtonHandler(int x, int y, TicTacToeModel model) {
        return event -> {
            // When the game mode is player vs player, always let the player click a tile
            if (model.getGameMode() == TicTacToeModel.GameMode.PLAYER_VS_PLAYER)
                model.clickPosition(x, y);
                // When not playing player vs player, but player vs ai or online, only let the player click on his turn
            else if (model.getBoard().isPlayerTurn())
                model.clickPosition(x, y);
        };
    }

    public static EventHandler<ActionEvent> setSceneInStage(Stage stage, Scene scene) {
        return event -> {
            if (stage == null) System.out.println("stage null");
            if (scene == null) System.out.println("scene null");
            stage.setScene(scene);
        };
    }

    public static EventHandler<ActionEvent> exitGame(TicTacToeModel model) {
        return event -> {
            model.forfeitGame();
        };
    }
}
