package framework;

import games.AbcView;
import games.Game;
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
import reversi.GUI;

// TODO - https://gist.github.com/Col-E/7d31b6b8684669cf1997831454681b85 (smooth scrolling)
public class FrameworkView extends Application {
    private static final String backgroundColor = "#212121";
    
    private Image emptyTileImage = new Image(getClass().getResourceAsStream("/images/framework/inner-shadow_2.png"));
    private Image[] gameImages = new Image[] {
            new Image(getClass().getResourceAsStream("/images/framework/game_reversi.png")),
            new Image(getClass().getResourceAsStream("/images/framework/game_tic_tac_toe.png"))
    };
    
    private Label gameLabel;
    
    @Override
    public void start(Stage stage) throws Exception {
        // Create a label that shows to selected game
        gameLabel = new Label("");
        gameLabel.setTextFill(Color.web("#ffffff"));
        gameLabel.setFont(new Font(26));
        gameLabel.setPadding(new Insets(60, 0, -15, 60));   // TOP, RIGHT, BOTTOM, LEFT
        
        HBox gameTileHolder = new HBox();
        GameTile[] gameTiles = new GameTile[] {
                new GameTile("Reversi", gameImages[0], new GUI()),
                new GameTile("Tic Tac Toe", gameImages[1], new AbcView()),
                new GameTile(),
                new GameTile(),
                new GameTile(),
                new GameTile(),
                new GameTile(),
                new GameTile(),
        };
        for (GameTile tile : gameTiles) {
            if (tile.gameName == null) {
                int size = 300;
                ImageView tileImage = new ImageView(emptyTileImage);
                tileImage.setFitWidth(size);
                tileImage.setFitHeight(size);
                
                gameTileHolder.getChildren().add(tileImage);
                continue;
            }
            
            int size = 300;
            ImageView tileImage = new ImageView(tile.image);
            tileImage.setFitWidth(size);
            tileImage.setFitHeight(size);
        
            int borderSize = 10;
            Rectangle selectionBox = new Rectangle(size + borderSize, size + borderSize);
            selectionBox.setFill(Color.web("#13bed8"));
            selectionBox.setVisible(false);
        
            // Show the selection box when the mouse is over the game tile
            tileImage.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                selectionBox.setVisible(true);
                gameLabel.setText(tile.gameName);
            });
            tileImage.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                selectionBox.setVisible(false);
                gameLabel.setText("");
            });
            
            tileImage.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                // ...
                tile.game.stage.setOnCloseRequest(gEvent -> stage.show());
                tile.game.resetGame();
                tile.game.startGame();
                
                stage.hide();
            });
        
            StackPane sp = new StackPane(selectionBox, tileImage);
            gameTileHolder.getChildren().add(sp);
        }
        
        gameTileHolder.setSpacing(20);
        gameTileHolder.setPadding(new Insets(0, 20, 30, 20));
        
        // Create a scroll pane where all the available games are shown
        Pane p = new Pane();
        p.getChildren().addAll(gameTileHolder);
        
        ScrollPane scrollPane = new ScrollPane(p);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background: " + backgroundColor + ";");
        
        // Change the style of the scrollbar of the scroll pane
        ChangeListener<Skin<?>> skinChangeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                scrollPane.skinProperty().removeListener(this);
    
                for (Node node : scrollPane.lookupAll(".scroll-bar")) {
                    if (node instanceof ScrollBar) {
                        ScrollBar scrollBar = (ScrollBar) node;
                        if (scrollBar.getOrientation() == Orientation.HORIZONTAL)
                            scrollBar.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background: " + backgroundColor + ";");
                    }
                }
            }
        };
        scrollPane.skinProperty().addListener(skinChangeListener);
        
        // Create the bottom row of buttons
        HBox buttons = new HBox();
        
        String[] buttonTexts = new String[] { "A", "B", "C", "D" };
        for (String text : buttonTexts) {
            Button b = new Button(text);
            int size = 50;
            b.setStyle("-fx-background-radius: 5em; -fx-min-width: " + size + "px; -fx-min-height: " + size + "px;" +
                    " -fx-max-width: " + size + "px; -fx-max-height: " + size + "px;");
            buttons.getChildren().add(b);
        }
        
        buttons.setSpacing(75);
        buttons.setAlignment(Pos.CENTER);
    
        VBox vBox = new VBox(gameLabel, scrollPane, buttons);
        vBox.setSpacing(20);
        vBox.setBackground(new Background(new BackgroundFill(Color.web(backgroundColor), null, null)));
        
        Scene scene = new Scene(vBox, 1000, 600);
        
        stage.setScene(scene);
        stage.setTitle("Game Hub");
        stage.setResizable(false);
        stage.show();
    }
    
    class GameTile {
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
