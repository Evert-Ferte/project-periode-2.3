package games.reversi;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * this class handels the model and view coupling of the Reversi game
 *
 * @author Evert de la Ferté
 * @version 1.0
 */
public class ReversiController {
    public static EventHandler<ActionEvent> ButtonHandler(int x, int y, ReversiModel model) {
        return event -> {
            // When the game mode is player vs player, always let the player click a tile
            if (model.getGameMode() == ReversiModel.GameMode.PLAYER_VS_PLAYER)
                model.clickPosition(x, y);
            // When not playing player vs player, but player vs ai or online, only let the player click on his turn
            else if (model.getBoard().isPlayerTurn())
                model.clickPosition(x, y);
        };
    }

    /**
     * Change the current scene and stage
     *
     * @param stage to change to
     * @param scene to change to
     * @return an event in which the scene and the stage have been changed
     */
    public static EventHandler<ActionEvent> setSceneInStage(Stage stage, Scene scene) {
        return event -> {
            if (stage == null) System.out.println("stage null");
            if (scene == null) System.out.println("scene null");
            stage.setScene(scene);
        };
    }

    /**
     * The handler for challenging a player through online multi-player
     *
     * @param model contains the data and its related logic
     * @param btn a button to challenge a player
     * @return an event that has invokes the challenge
     */
    public static EventHandler<ActionEvent> challengePlayer(ReversiModel model, Button btn) {
        return event -> model.challengePlayer(btn);
    }

    /**
     * Exit current game
     *
     * @param model contining the data and its related logic
     * @return event invoking exit game logic
     */
    public static EventHandler<ActionEvent> exitGame(ReversiModel model) {
        return event -> {
            model.forfeitGame();
        };
    }
}
