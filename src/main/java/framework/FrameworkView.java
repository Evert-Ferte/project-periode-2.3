package framework;

import games.Game;
import games.reversi.ReversiView;
import games.reversi.Vector2;
import games.tictactoe.Main;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FrameworkView extends Application {
    private static final Vector2 windowSize = new Vector2(1000, 600);
    private static final String backgroundColor = "#212121";
    
    private Label gameLabel;
    
    private Image emptyTileImage = new Image(getClass().getResourceAsStream("/images/framework/inner-shadow_2.png"));
    private Image[] gameImages = new Image[] {
            new Image(getClass().getResourceAsStream("/images/framework/game_reversi.png")),
            new Image(getClass().getResourceAsStream("/images/framework/game_tic_tac_toe.png"))
    };
    
    /**
     * This function is called on the start of the application.
     * This function creates all UI components.
     *
     * @param stage The main stage of the application
     */
    @Override
    public void start(Stage stage) {
        // Create the game label, game tiles and buttons
        gameLabel               = createGameLabel();
        ScrollPane scrollPane   = createGameTiles(stage);
        HBox buttons            = createButtons();
        
        // Create a vertical layout for all UI components
        VBox vBox = new VBox(gameLabel, scrollPane, buttons);
        vBox.setSpacing(20);
        vBox.setBackground(new Background(new BackgroundFill(Color.web(backgroundColor), null, null)));
        
        // Create a new scene containing the vertical layout
        Scene scene = new Scene(vBox, windowSize.x, windowSize.y);
        
        // Configure and show the stage
        stage.setScene(scene);
        stage.setTitle("Game Hub");
        stage.setResizable(false);
        stage.show();
    }
    
    /**
     * Creates the top label showing the selected games.
     *
     * @return Returns the game label.
     */
    private Label createGameLabel() {
        Label label = new Label("");
        
        // Set the label color, font size and padding
        label.setTextFill(Color.web("#ffffff"));
        label.setFont(new Font(26));
        label.setPadding(new Insets(60, 0, -15, 60));   // INFO: Insets(TOP, RIGHT, BOTTOM, LEFT)
        
        return label;
    }
    
    /**
     * Creates the games tiles shown in the middle of the screen.
     *
     * @param stage The main stage component of this application.
     * @return Returns a ScrollPane object containing all available games.
     */
    private ScrollPane createGameTiles(Stage stage) {
        // Create a list with games and some empty tiles
        GameTile[] gameTiles = new GameTile[] {
                new GameTile("Reversi", gameImages[0], new ReversiView()),
                new GameTile("Tic Tac Toe", gameImages[1], new Main()),
                new GameTile(), new GameTile(), new GameTile(), new GameTile(), new GameTile(), new GameTile(),
        };
        
        // Create the horizontal box which will contain the (game)tiles
        HBox tiles = new HBox();
        tiles.setSpacing(20);
        tiles.setPadding(new Insets(0, 20, 30, 20));
        
        // Loop through all games, and create the UI components for them
        for (GameTile tile : gameTiles) {
            // If the game(name) does not exist, create an empty tile, and go to the next iteration
            if (tile.gameName == null) {
                int size = 300;
                ImageView tileImage = new ImageView(emptyTileImage);
                tileImage.setFitWidth(size);
                tileImage.setFitHeight(size);
            
                tiles.getChildren().add(tileImage);
                continue;
            }
            
            // Create the tile image
            int size = 300;
            ImageView tileImage = new ImageView(tile.image);
            tileImage.setFitWidth(size);
            tileImage.setFitHeight(size);
            
            // Create the selection box around the tile image
            int borderSize = 10;
            Rectangle selectionBox = new Rectangle(size + borderSize, size + borderSize);
            selectionBox.setFill(Color.web("#13bed8"));
            selectionBox.setVisible(false);
            
            // Now we setup a few mouse event handlers...
            tileImage.addEventHandler(MouseEvent.MOUSE_ENTERED, FrameworkController.MOUSE_ENTERED(selectionBox, gameLabel, tile));
            tileImage.addEventHandler(MouseEvent.MOUSE_EXITED, FrameworkController.MOUSE_EXITED(selectionBox, gameLabel));
            tileImage.addEventHandler(MouseEvent.MOUSE_RELEASED, FrameworkController.MOUSE_RELEASED(tile, stage));
            
            // Create a StackPane, where we stack the tile image on top of the selection box
            StackPane sp = new StackPane(selectionBox, tileImage);
            tiles.getChildren().add(sp);
        }
        
        // Create a scroll pane containing all (game)tiles
        // TODO - disable vertical scrolling (dragging)
        ScrollPane scrollPane = new ScrollPane(tiles);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background: " + backgroundColor + ";");
        
        // Change the style of the scrollbar of the scroll pane
        // - We need to add a new skin which is done below
        ChangeListener<Skin<?>> skinChangeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                scrollPane.skinProperty().removeListener(this);
                
                // Find the ScrollBar in the ScrollPane
                for (Node node : scrollPane.lookupAll(".scroll-bar")) {
                    if (node instanceof ScrollBar) {
                        ScrollBar scrollBar = (ScrollBar) node;
                        // Change the style of the horizontal ScrollBar
                        if (scrollBar.getOrientation() == Orientation.HORIZONTAL)
                            scrollBar.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background: " + backgroundColor + ";");
                    }
                }
            }
        };
        scrollPane.skinProperty().addListener(skinChangeListener);
        
        return scrollPane;
    }
    
    /**
     * Creates the bottom row of buttons.
     *
     * @return Returns a HBox component containing all buttons.
    */
    private HBox createButtons() {
        // The parent for all the buttons
        HBox buttonHolder = new HBox();
        
        // A list of the text for on the buttons
        String[] buttonTexts = new String[] { "A", "B", "C", "D" };
        
        // Loop for all texts (see above)
        for (String text : buttonTexts) {
            // Create a new button, with size 'bSize' and the style for the button
            Button b = new Button(text);
            int bSize = 50;
            b.setStyle("-fx-background-radius: 5em; -fx-min-width: " + bSize + "px; -fx-min-height: " + bSize + "px;" +
                    " -fx-max-width: " + bSize + "px; -fx-max-height: " + bSize + "px;");
            
            // Add the button to the button holder
            buttonHolder.getChildren().add(b);
        }
        
        // Set the spacing and alignment for the button holder
        buttonHolder.setSpacing(75);
        buttonHolder.setAlignment(Pos.CENTER);
        
        return buttonHolder;
    }
    
    /**
     * A class containing INFORMATION on a game tile in the framework.
     * INFORMATION:
     * - game name
     * - game image
     * - reference to the game class (child of Game.java)
     */
    static class GameTile {
        public String gameName;
        public Image image;
        public Game game;
        
        public GameTile() { }
        public GameTile(String gameName, Image image, Game game) {
            this.gameName = gameName;
            this.image = image;
            this.game = game;
        }
    }
}
