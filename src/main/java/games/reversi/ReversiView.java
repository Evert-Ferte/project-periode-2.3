package games.reversi;

import games.Game;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

// TODO - cannot make a match on the edge
// TODO - use static final variables for colors
public class ReversiView extends Game{
    private static final Vector2 windowSize = new Vector2(680, 860);
    
    private ReversiModel model;
    
    // Menu variables
    private Scene mainMenu;
    private Scene multiplayerMenu;
    private Scene settingsMenu;
    private Scene choseModeMenu;
    private Scene gameMenu;
    
    private Button startButton;
    private Button multiplayerButton;
    private Button settingsButton;
    private Button vsPlayerButton;
    private Button vsAiButton;
    
    VBox entryHolders;
    
    // Game Variables
    private Image tileEmpty = new Image(getClass().getResourceAsStream("/images/reversi/tile_empty_fade.png"));
    private Image tileWhite = new Image(getClass().getResourceAsStream("/images/reversi/tile_white_0.png"));
    private Image tileBlack = new Image(getClass().getResourceAsStream("/images/reversi/tile_black_0.png"));


    private Vector2 fieldSize = new Vector2(640, 640);
    private ArrayList<ArrayList<Button>> viewMap = new ArrayList<>();
    
    private Label turnLabel;
    private Label scoreWhiteLabel;
    private Label scoreBlackLabel;
    
    private Vector2 imgSize;
    
    /**
     * The constructor. Creates a new model for this view.
     */
    public ReversiView() {
        this.model = new ReversiModel(this);
    }
    
    //TEMP
    public ReversiView(String name) {
        this();
        model.setName(name);
    }
    
    /**
     * Start the game.
     */
    @Override
    public void startGame() {
        start(stage);
    }
    
    /**
     * Reset the game and it's values.
     */
    @Override
    public void resetGame() {
        viewMap = new ArrayList<>();
        model.reset();
    }
    
    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage) {
        mainMenu = createMainMenu();
        multiplayerMenu = createMultiplayerMenu();
        settingsMenu = createSettingsMenu();
        choseModeMenu = createChooseModeMenu();
        gameMenu = createGameMenu();
    
        setActionListeners();
        
        this.stage.setScene(mainMenu);
        this.stage.setTitle("Reversi");
        this.stage.setResizable(false);
        this.stage.show();
    }
    
    private Scene createMainMenu() {
        // Create the main title for this screen
        Label title = new Label("Reversi");
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        
        // Create all the buttons
        startButton = new Button("Play");
        multiplayerButton = new Button("Multiplayer");
        settingsButton = new Button("Settings");
        
        // Set the size for all buttons
        startButton.setMinSize(200, 40);
        multiplayerButton.setMinSize(200, 40);
        settingsButton.setMinSize(200, 40);
        
        // Create a box that holds all the buttons from above
        VBox buttonHolder = new VBox(startButton, multiplayerButton, settingsButton);
        buttonHolder.setPadding(new Insets(100, 0, 100, 0));
        buttonHolder.setSpacing(50);
        buttonHolder.setAlignment(Pos.TOP_CENTER);
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, buttonHolder);
        vBox.setAlignment(Pos.TOP_CENTER);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(vBox, windowSize.x, windowSize.y);
    }
    
    private Scene createMultiplayerMenu() {
        // Create the main title for this screen
        Label title = new Label("Multiplayer");
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        
        // Create a box that holds all the player entries
        entryHolders = new VBox();
        entryHolders.setMinSize(100, 560);
        entryHolders.setPadding(new Insets(60, 20, 20, 20));
        entryHolders.setSpacing(20);
        
        // Get a list of all online players
        refreshPlayerList(model.getPlayerList());
        
        // Create the back button, which leads to the main menu
        Button backButton = new Button("<");
        backButton.setMinSize(100, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
        
        // Create the refresh button, which refreshes the player list
        Button refreshButton = new Button("Refresh");
        refreshButton.setMinSize(100, 40);
        refreshButton.setOnAction(event -> model.refreshPlayerList());
        
        HBox buttons = new HBox(backButton, refreshButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, entryHolders, buttons);
        vBox.setAlignment(Pos.TOP_CENTER);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(vBox, windowSize.x, windowSize.y);
    }
    
    private Scene createSettingsMenu() {
        // Create the main title for this screen
        Label title = new Label("Settings");
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        
        // ...
    
        VBox emptyBox = new VBox();
        emptyBox.setPadding(new Insets(250, 0, 100, 0));
        
        // ...
        
        Button backButton = new Button("<");
        backButton.setMinSize(100, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, emptyBox, backButton);
        vBox.setAlignment(Pos.TOP_CENTER);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(vBox, windowSize.x, windowSize.y);
    }
    
    private Scene createChooseModeMenu() {
        // Create the main title for this screen
        Label title = new Label("Choose game mode");
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
    
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
    
        vsPlayerButton = new Button("Player vs Player");
        vsPlayerButton.setMinSize(200, 40);
        vsAiButton = new Button("Player vs Computer");
        vsAiButton.setMinSize(200, 40);
        
        VBox modeButtons = new VBox(vsPlayerButton, vsAiButton);
        modeButtons.setPadding(new Insets(150, 0, 100, 0));
        modeButtons.setSpacing(40);
        modeButtons.setAlignment(Pos.TOP_CENTER);
    
        Button backButton = new Button("<");
        backButton.setMinSize(100, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
    
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, modeButtons, backButton);
        vBox.setAlignment(Pos.TOP_CENTER);
    
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(vBox, windowSize.x, windowSize.y);
    }
    
    private Scene createGameMenu() {
        // Get a few values from the model
        boolean turn = model.getTurn();
        int mapSize = model.getMapSize();

        // Set the image size that all tiles/buttons on the board will use
        imgSize = new Vector2(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);
    
        // Create a vertical box that will hold all UI components
        VBox vBox = new VBox();
        vBox.setBackground(new Background(new BackgroundFill(Color.web("#005a00"), null, null)));
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(20);
    
        // Create the turn label, which shows who's turn it is
        turnLabel = new Label((turn ? "White" : "Black") + "'s turn");
        turnLabel.setAlignment(Pos.CENTER);
        turnLabel.setMinSize(fieldSize.x * 0.8f, 60);
        turnLabel.setFont(new Font(30));
        turnLabel.setBackground(new Background(new BackgroundFill(Color.web("005200"), null, null)));
        turnLabel.setTextFill(Color.web(turn ? "#ffffff" : "000000"));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(turnLabel);
    
        // Create a horizontal box that holds the score of black and white
        HBox hBox = new HBox();
        // Create the white label
        Label labelWhite = new Label("White");
        labelWhite.setTextFill(Color.web("#ffffff"));
        // Create the white score label
        scoreWhiteLabel = new Label("0");
        scoreWhiteLabel.setMinSize(40, 20);
        scoreWhiteLabel.setAlignment(Pos.CENTER);
        scoreWhiteLabel.setTextFill(Color.web("#ffffff"));
        scoreWhiteLabel.setBackground(new Background(new BackgroundFill(Color.web("005200"), null, null)));
        // Create the black label
        Label labelBlack = new Label("Black");
        labelBlack.setTextFill(Color.web("#000000"));
        // Create the black score label
        scoreBlackLabel = new Label("0");
        scoreBlackLabel.setMinSize(40, 20);
        scoreBlackLabel.setAlignment(Pos.CENTER);
        scoreBlackLabel.setTextFill(Color.web("#000000"));
        scoreBlackLabel.setBackground(new Background(new BackgroundFill(Color.web("005200"), null, null)));
        // Create the colon that separates the black and white score
        Label colon = new Label(":");
        colon.setTextFill(Color.web("#000000"));
    
        // Add the above labels, and configure the horizontal box
        hBox.getChildren().addAll(labelWhite, scoreWhiteLabel, colon, scoreBlackLabel, labelBlack);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(hBox);
        
        // Create a grid that will hold all game tile buttons
        GridPane grid = new GridPane();
        vBox.getChildren().add(grid);
    
        // Loop through the board
        for (int y = 0; y < mapSize; y++) {
            viewMap.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                // Create a new button for this spot on the board
                Button btn = new Button();
                btn.setMinSize(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);
                viewMap.get(y).add(btn);
                viewMap.get(y).get(x).setId(model.getEmptyId());
            
                // Create an image that we will put over the button
                ImageView img = new ImageView(tileEmpty);
                img.setFitWidth(fieldSize.x / (float)mapSize);
                img.setFitHeight(fieldSize.y / (float)mapSize);
                btn.setGraphic(img);
            
                // Set the event handler for the button
                btn.setOnAction(new ReversiController.ButtonHandler(x, y, model));
            
                grid.add(btn, x, y);
            }
        }
    
        Button backButton = new Button("Exit");
        backButton.setMinSize(120, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
        vBox.getChildren().add(backButton);
    
        // Create the standard 4 tiles that make up the default board layout
        model.clickPosition(3, 3, true);
        model.clickPosition(3, 4, true);
        model.clickPosition(4, 4, true);
        model.clickPosition(4, 3, true);

        update();
    
        //TEMP
        //TODO fix bug here - upper right tile can never be clicked (probably same bug as not being able to click on the edges)
//        model.clickPosition(5, 0, true);
//        model.clickPosition(6, 0, true);
//
//        model.clickPosition(6, 1, true);
//
//        model.clickPosition(7, 1, true);
//        model.clickPosition(7, 2, true);
//        turn = !turn;
        //TEMP
        
        return new Scene(vBox, windowSize.x, windowSize.y);
    }
    
    private void setActionListeners() {
//        startButton.setOnAction(ReversiController.setSceneInStage(stage, gameMenu));
        startButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, choseModeMenu));
        multiplayerButton.setOnAction(ReversiController.setSceneInStage(stage, multiplayerMenu));
        settingsButton.setOnAction(ReversiController.setSceneInStage(stage, settingsMenu));
    
        //TODO IMPROVE, MAKE SEPARATE FUNCTION
        EventHandler<ActionEvent> startEvent = event -> {
            model.reset();
            for (ArrayList<Button> btns : viewMap) {
                for (Button btn : btns) {
                    int mapSize = model.getMapSize();
                
                    // Create an image that we will put over the button
                    ImageView img = new ImageView(tileEmpty);
                    img.setFitWidth(fieldSize.x / (float)mapSize);
                    img.setFitHeight(fieldSize.y / (float)mapSize);
                    btn.setGraphic(img);
                
                    btn.setId(model.getEmptyId());
                }
            }
            // Create the standard 4 tiles that make up the default board layout
            model.clickPosition(3, 3, true);
            model.clickPosition(3, 4, true);
            model.clickPosition(4, 4, true);
            model.clickPosition(4, 3, true);
            update();
        };
        
        vsPlayerButton.addEventHandler(ActionEvent.ACTION, event -> model.setAgainstPlayer(true));
        vsPlayerButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, gameMenu));
        vsPlayerButton.addEventHandler(ActionEvent.ACTION, startEvent);
        
        vsAiButton.addEventHandler(ActionEvent.ACTION, event -> model.setAgainstPlayer(false));
        vsAiButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, gameMenu));
        vsAiButton.addEventHandler(ActionEvent.ACTION, startEvent);
    }
    
    public void refreshPlayerList(String[] players) {
        if (entryHolders == null) return;
        
        // Remove all current nodes
        entryHolders.getChildren().clear();
        
        // Loop through all online players
        for (String player : players) {
            // Create a label that shows the player name
            Label playerLabel = new Label(player);
            playerLabel.setFont(new Font(24));
            
            // Create the button where you can challenge and accept challenges
            Button actionButton = new Button("Challenge");
            actionButton.setOnAction(ReversiController.challengePlayer(model, actionButton));
            actionButton.setMinSize(100, 40);
            actionButton.setId(player);
        
            // Create a border pane so we can align all components to the borders
            BorderPane entry = new BorderPane();
            entry.setBackground(new Background(new BackgroundFill(Color.web("#dddddd"), null, null)));
            entry.setMinSize(100, 80);
        
            // Create a box for the left side. We do this the vertically center the label
            VBox leftBorder = new VBox(playerLabel);
            leftBorder.setAlignment(Pos.CENTER);
            leftBorder.setPadding(new Insets(0, 0, 0, 40));
        
            // Create a box for the right side. We do this the vertically center the button
            VBox rightBorder = new VBox(actionButton);
            rightBorder.setAlignment(Pos.CENTER);
            rightBorder.setPadding(new Insets(0, 20, 0, 0));
        
            // Add the 2 borders
            entry.setLeft(leftBorder);
            entry.setRight(rightBorder);
        
            // Add the new player entry
            entryHolders.getChildren().add(entry);
        }
    }
    
    /**
     * Update the black and white score labels.
     */
    public void updateScoreLabel() {
        scoreBlackLabel.setText(String.valueOf(model.getScoreBlack()));
        scoreWhiteLabel.setText(String.valueOf(model.getScoreWhite()));
    }
    
    /**
     * Updates the turn label. The turn label shows who's turn it is.
     *
     * @param turn Boolean value that represents whether it is black of white's turn.
     */
    public void updateTurnLabel(boolean turn) {
        turnLabel.setText((turn ? "White" : "Black") + "'s turn");
        turnLabel.setTextFill(Color.web(turn ? "#ffffff" : "000000"));
    }
    
    /**
     * Update the graphics (image) for a specific tile.
     *
     * @param player Boolean value that represents whether it is black of white's turn.
     * @param xPos The X position on the board.
     * @param yPos The Y position on the board.
     */
    public void updateTileGraphic(boolean player, int xPos, int yPos) {
        ImageView img = new ImageView(player ? tileWhite : tileBlack);
        img.setFitWidth(imgSize.x);
        img.setFitHeight(imgSize.y);

        Button current = viewMap.get(yPos).get(xPos);
        current.setGraphic(img);
        current.setId(model.getPlayerId(player));
    }

    public void updateMapGraphic(){
        ArrayList<ArrayList<String>> modelMap = model.getModelMap();
        int mapSize = model.getMapSize();
        for (int y=0; y<mapSize; y++) {
            for(int x=0; x<mapSize; x++ ){
                String id = modelMap.get(y).get(x);
                if(id.equals(model.getWhiteId())){
                    updateTileGraphic(true, x, y);
                }else if (id.equals(model.getBlackId())){
                    updateTileGraphic(false, x, y);
                }

            }
        }
    }

    public void update(){
        updateMapGraphic();
        updateTurnLabel(model.getTurn());
        updateScoreLabel();
    }
    
    public void challengeReceived(String challenger, String nr) {
        for (Node entry : entryHolders.getChildren()) {
            for (Node node : ((BorderPane) entry).getChildren()) {
                VBox b = (VBox) node;
                Node n = b.getChildren().get(0);
                String entryId = n.getId();
                
                if (entryId != null) {
                    if (entryId.equals(challenger)) {
                        System.out.println(entryId);
                        Button btn = ((Button) n);
                        System.out.println(btn.toString());
    
                        Platform.runLater(() -> {
                            btn.setText("Accept");
                            btn.setBackground(new Background(new BackgroundFill(Color.web("#10d12d"), null, null)));
                        });
                        n.setId(nr);
                        break;
                    }
                }
            }
        }
    }
    
    public void startMatch() {
        Platform.runLater(() -> {
            stage.setScene(gameMenu);
        });
    }
}
