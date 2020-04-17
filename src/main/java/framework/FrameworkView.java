package framework;

import customization.ColorSet;
import games.Game;
import games.reversi.ReversiView;
import games.Vector2;
import games.tictactoe.TicTacToeView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
import javafx.util.Duration;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Handles all the GUI functionality of the framework
 *
 * @author Evert de la Ferté
 * @version 1.0
 */
// TODO - font for framework, reversi and ttt
public class FrameworkView extends Application {
    private static final Vector2 WINDOW_SIZE = new Vector2(1000, 600);
    private static final ColorSet DARK_THEME = new ColorSet("Dark theme", "#212121", "#1e1e1e", "#878787", "#ffffff", "#000000");
    private static final ColorSet LIGHT_THEME = new ColorSet("Light theme", "#e5e5e5", "#d8d8d8", "#333333", "#000000", "#ffffff");
    
    private ColorSet currentColorSet = DARK_THEME;
    
    private Label gameLabel;
    
    private Image emptyTileImage = new Image(getClass().getResourceAsStream("/images/framework/inner-shadow_2.png"));
    private Image[] gameImages = new Image[] {
            new Image(getClass().getResourceAsStream("/images/framework/game_reversi.png")),
            new Image(getClass().getResourceAsStream("/images/framework/game_tic_tac_toe.png"))
    };
    
    private Image settingsImage = new Image(getClass().getResourceAsStream("/images/general/settings.png"));
    private Image homeImage = new Image(getClass().getResourceAsStream("/images/general/home.png"));
    private Image closeImage = new Image(getClass().getResourceAsStream("/images/general/power.png"));
    
    private Scene mainScene;
    private Scene optionScene;
    
    private Label currentTimeLabel;
    private ChoiceBox<String> themeChoiceBox;
    
    private boolean use12HourNotation = false;
    CheckBox cb;
    
    /**
     * This function is called on the start of the application.
     * This function creates all UI components.
     *
     * @param stage The main stage of the application
     */
    @Override
    public void start(Stage stage) {
        // Create the game label, game tiles and buttons
        optionScene                 = createSettingsMenu(stage);
        BorderPane notificationBar  = createNotificationBar();
        gameLabel                   = createGameLabel();
        ScrollPane scrollPane       = createGameTiles(stage);
        HBox buttonHolder           = createButtons(stage);
        
        // Create a vertical layout for all UI components
        VBox vBox = new VBox(notificationBar, gameLabel, scrollPane, buttonHolder);
        vBox.setSpacing(20);
        vBox.setBackground(new Background(new BackgroundFill(Color.web(currentColorSet.primary), null, null)));
        
        addThemeChangeListener(change -> vBox.setBackground(new Background(new BackgroundFill(Color.web(currentColorSet.primary), null, null))));
        
        // Create a new scene containing the vertical layout
        mainScene = new Scene(vBox, WINDOW_SIZE.x, WINDOW_SIZE.y);
        
        // Configure and show the stage
        stage.setScene(mainScene);
        stage.setTitle("Game Hub");
        stage.setResizable(false);
        stage.show();
    }
    
    private BorderPane createNotificationBar() {
        int barHeight = 30;
        
        // Create the current time label
        currentTimeLabel = new Label();
        updateTimeLabel(currentTimeLabel);
        currentTimeLabel.setFont(new Font(16));
        currentTimeLabel.setTextFill(Color.web(currentColorSet.textPrimary));
        HBox timeBox = new HBox(currentTimeLabel);
        timeBox.setAlignment(Pos.CENTER_LEFT);
        timeBox.setPadding(new Insets(0, 0, 0, 20));
        
        // Create the icon holder box
        HBox iconsBox = new HBox();
        iconsBox.setAlignment(Pos.CENTER_RIGHT);
        iconsBox.setSpacing(5);
        iconsBox.setPadding(new Insets(0, 20, 0, 0));
        
        // Set all images in the bar
        ImageView[] icons = new ImageView[] {
                new ImageView(new Image(getClass().getResourceAsStream("/images/general/high-volume.png"))),
                new ImageView(new Image(getClass().getResourceAsStream("/images/general/wifi-signal.png"))),
                new ImageView(new Image(getClass().getResourceAsStream("/images/general/full-battery.png")))
        };
        
        // Add all icons to the icon holder
        for (ImageView icon : icons) {
            icon.setFitWidth(barHeight - 10);
            icon.setFitHeight(barHeight - 10);
            
            iconsBox.getChildren().add(icon);
        }
        
        // Create a border pane with the time on the left and icons on the left
        BorderPane bar = new BorderPane();
        bar.setMinSize(WINDOW_SIZE.x, barHeight);
        bar.setBackground(new Background(new BackgroundFill(Color.web(currentColorSet.secondary), null, null)));
        bar.setLeft(timeBox);
        bar.setRight(iconsBox);
        
        // Create a variable that will update the time every 1 second
        Timeline timeUpdater = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimeLabel(currentTimeLabel)));
        timeUpdater.setCycleCount(Timeline.INDEFINITE);
        timeUpdater.play();
        
        // Add an event that will change the color of the time label and bar when the theme changes
        addThemeChangeListener(change -> {
            currentTimeLabel.setTextFill(Color.web(currentColorSet.textPrimary));
            bar.setBackground(new Background(new BackgroundFill(Color.web(currentColorSet.secondary), null, null)));
        });
        cb.addEventHandler(ActionEvent.ACTION, e -> {
            use12HourNotation = !use12HourNotation;
            updateTimeLabel(currentTimeLabel);
        });
        
        return bar;
    }
    
    /**
     * Creates the top label showing the selected games.
     *
     * @return Returns the game label.
     */
    private Label createGameLabel() {
        Label label = new Label("");
        
        // Set the label color, font size and padding
        label.setTextFill(Color.web(currentColorSet.textPrimary));
        label.setFont(new Font(26));
        label.setPadding(new Insets(30, 0, -15, 60));   // INFO: Insets(TOP, RIGHT, BOTTOM, LEFT)
        
        addThemeChangeListener(change -> label.setTextFill(Color.web(currentColorSet.textPrimary)));
        
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
                new GameTile("Tic Tac Toe", gameImages[1], new TicTacToeView()),
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
        scrollPane.setStyle("-fx-background-color: " +  currentColorSet.primary + "; -fx-background: " + currentColorSet.primary + ";");
        
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
                            scrollBar.setStyle("-fx-background-color: " + currentColorSet.primary + "; -fx-background: " + currentColorSet.primary + ";");
                    }
                }
            }
        };
        scrollPane.skinProperty().addListener(skinChangeListener);
        
        addThemeChangeListener(change -> scrollPane.setStyle("-fx-background-color: " +  currentColorSet.primary +
                "; -fx-background: " + currentColorSet.primary + ";"));
        addThemeChangeListener(change -> {
            Set<Node> nodes = scrollPane.lookupAll(".scroll-bar");
            for (final Node node : nodes) {
                if (node instanceof ScrollBar) {
                    ScrollBar sb = (ScrollBar) node;
                    if (sb.getOrientation() == Orientation.HORIZONTAL)
                        sb.setStyle("-fx-background-color: " + currentColorSet.primary + "; -fx-background: " + currentColorSet.primary + ";");
                }
            }
        });
        
        return scrollPane;
    }
    
    /**
     * Creates the bottom row of buttons.
     *
     * @return Returns a HBox component containing all buttons.
    */
    private HBox createButtons(Stage stage) {
        final int bSize = 40;
        final String btnStyle = "-fx-background-color: " + currentColorSet.complementary +
                "; -fx-background-radius: 5em; -fx-min-width: " + (bSize + 20) + "px; -fx-min-height: " + (bSize + 20) +
                "px; -fx-max-width: " + (bSize + 20) + "px; -fx-max-height: " + (bSize + 20) + "px;";
        
        // Create the settings button
        Button settingsButton = new Button();
        settingsButton.setStyle(btnStyle);
        ImageView settingsImage = new ImageView(this.settingsImage);
        settingsImage.setFitWidth(bSize);
        settingsImage.setFitHeight(bSize);
        settingsButton.setGraphic(settingsImage);
        settingsButton.setOnAction(e -> stage.setScene(optionScene));
        
        // Create the home button
        Button homeButton = new Button();
        homeButton.setStyle(btnStyle);
        ImageView homeImage = new ImageView(this.homeImage);
        homeImage.setFitWidth(bSize);
        homeImage.setFitHeight(bSize);
        homeButton.setGraphic(homeImage);
        homeButton.setOnAction(e -> stage.setScene(mainScene));
    
        // Create the power button
        Button closeButton = new Button();
        closeButton.setStyle(btnStyle);
        ImageView closeImage = new ImageView(this.closeImage);
        closeImage.setFitWidth(bSize);
        closeImage.setFitHeight(bSize);
        closeButton.setGraphic(closeImage);
        closeButton.setOnAction(event -> stage.close());
        
        // The parent for all the buttons
        HBox buttonHolder = new HBox(settingsButton, homeButton, closeButton);
        buttonHolder.setSpacing(75);
        buttonHolder.setAlignment(Pos.CENTER);
        buttonHolder.setPadding(new Insets(20, 0, 0, 0));
        
        // Add an event listener, so that the button's color changes when the theme changes
        addThemeChangeListener(change -> {
            settingsButton.setStyle(btnStyle);
            homeButton.setStyle(btnStyle);
            closeButton.setStyle(btnStyle);
        });
        
        return buttonHolder;
    }
    
    private Scene createSettingsMenu(Stage stage) {
        // Create the color theme label
        Label themeLabel = new Label("Interface theme");
        themeLabel.setTextFill(Color.web(currentColorSet.textPrimary));
        
        // Create a choice box containing the light and dark theme
        themeChoiceBox = new ChoiceBox<>();
        themeChoiceBox.getItems().addAll("Light theme", "Dark theme");
        themeChoiceBox.setMinWidth(120);
        themeChoiceBox.setValue(currentColorSet.hashCode() == DARK_THEME.hashCode() ? "Dark theme" : "Light theme");
        themeChoiceBox.setOnAction(e -> themeChanged(themeChoiceBox.getValue()));
        
        BorderPane option1 = new BorderPane();
        option1.setMaxSize(500, 100);
        option1.setLeft(themeLabel);
        option1.setRight(themeChoiceBox);
        
        // Create a check box to toggle between 12 or 24 hour time notation
        Label timeNotationLabel = new Label("Use 24 hour time notation");
        timeNotationLabel.setTextFill(Color.web(currentColorSet.textPrimary));
        
        cb = new CheckBox();
        cb.fire();
        
        BorderPane option2 = new BorderPane();
        option2.setPadding(new Insets(50, 0, 0, 0));
        option2.setMaxSize(500, 100);
        option2.setLeft(timeNotationLabel);
        option2.setRight(cb);
    
        // Create the back/home button
        final int bSize = 40;
        final String btnStyle = "-fx-background-color: " + currentColorSet.complementary + "; -fx-background-radius: 5em; " +
                "-fx-min-width: " + (bSize + 20) + "px; -fx-min-height: " + (bSize + 20) + "px; -fx-max-width: " + (bSize + 20) + "px; " +
                "-fx-max-height: " + (bSize + 20) + "px;";
        
        // Create the home button
        Button homeButton = new Button();
        homeButton.setStyle(btnStyle);
        ImageView homeImage = new ImageView(this.homeImage);
        homeImage.setFitWidth(bSize);
        homeImage.setFitHeight(bSize);
        homeButton.setGraphic(homeImage);
        homeButton.addEventHandler(ActionEvent.ACTION, e -> stage.setScene(mainScene));
        
        // Create a border pane so we can align the home button to the bottom of the screen
        BorderPane homeButtonHolder = new BorderPane();
        homeButtonHolder.setMinSize(WINDOW_SIZE.x, WINDOW_SIZE.y);
        homeButtonHolder.setBottom(homeButton);
        BorderPane.setAlignment(homeButton, Pos.BOTTOM_CENTER);
        homeButtonHolder.setPadding(new Insets(0, 0, bSize * 3 + 27 + 60, 0));
        
        VBox vBox = new VBox(option1, option2, homeButtonHolder);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(80, 0, 0, 0));
        vBox.setBackground(new Background(new BackgroundFill(Color.web(currentColorSet.primary),
                null, null)));
        
        // Add a change listener, that changes the color if the theme has changed
        addThemeChangeListener(change -> {
            vBox.setBackground(new Background(new BackgroundFill(Color.web(currentColorSet.primary)
                    , null, null)));
            homeButton.setStyle(btnStyle);
            themeLabel.setTextFill(Color.web(currentColorSet.textPrimary));
            timeNotationLabel.setTextFill(Color.web(currentColorSet.textPrimary));
        });
        
        return new Scene(vBox, WINDOW_SIZE.x, WINDOW_SIZE.y);
    }
    
    private void addThemeChangeListener(InvalidationListener listener) {
        themeChoiceBox.valueProperty().addListener(listener);
    }
    
    private void themeChanged(String value) {
        // Set to theme based on the given value
        currentColorSet = value.equals("Light theme") ? LIGHT_THEME : DARK_THEME;
    }
    
    private void updateTimeLabel(Label timeLabel) {
        if (use12HourNotation)
            timeLabel.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
        else
            timeLabel.setText(new SimpleDateFormat("HH:mm").format(new Date()));
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
