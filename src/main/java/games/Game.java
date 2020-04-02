package games;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class Game extends Application {
    public Stage stage;
    
    /**
     * The constructor. Initializes the stage if the application.
     */
    public Game() {
        stage = new Stage();
    }
    
    /**
     * Start the game.
     */
    public abstract void startGame();
    
    /**
     * Reset the game and it's values.
     */
    public abstract void resetGame();
    
    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     * @throws Exception Throws any exceptions caught.
     */
    @Override
    public abstract void start(Stage stage) throws Exception;
}
