package framework;

import games.Game;
import games.reversi.ReversiView;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class FrameworkController {
    /**
     * Mouse event for when the mouse enters a game tile.
     * Shows the selection box and sets the game label to the correct game.
     *
     * @param selectionBox The selection box around the game tile.
     * @param gameLabel The label that shows the selected game.
     * @param tile The tile clicked.
     * @return Returns this event.
     */
    public static EventHandler<MouseEvent> MOUSE_ENTERED(Node selectionBox, Label gameLabel, FrameworkView.GameTile tile) {
        return mouseEvent -> {
            selectionBox.setVisible(true);
            gameLabel.setText(tile.gameName);
        };
    }
    
    /**
     * Mouse event for when the mouse exits a game tile.
     * Hides the selection box and sets the game label to empty.
     *
     * @param selectionBox The selection box around the game tile.
     * @param gameLabel The label that shows the selected game.
     * @return Returns this mouse event.
     */
    public static EventHandler<MouseEvent> MOUSE_EXITED(Node selectionBox, Label gameLabel) {
        return mouseEvent -> {
            selectionBox.setVisible(false);
            gameLabel.setText("");
        };
    }
    
    /**
     * Mouse event for when the mouse is released after clicking a game tile.
     * Starts the clicked game and closes the framework interface.
     *
     * @param tile The tile clicked.
     * @param stage The main stage of the application.
     * @return Returns this mouse event.
     */
    public static EventHandler<MouseEvent> MOUSE_RELEASED(FrameworkView.GameTile tile, Stage stage) {
        return mouseEvent -> {
            tile.game.stage.setOnCloseRequest(gEvent -> stage.show());
            tile.game.resetGame();
            tile.game.startGame();
    
//            Game view = new ReversiView("evert");
//            view.resetGame();
//            view.startGame();
    
            stage.hide();
        };
    }
}
