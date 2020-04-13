package games.reversi;

import games.Game;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;

// TODO - use static final variables for colors
public class ReversiView extends Game{
    private static final Vector2 windowSize = new Vector2(680, 860);
    private static final Vector2 fieldSize = new Vector2(560, 560); // 640, 640
    
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
    
    private VBox entryHolders;
    
    // Game Variables
    private Image tileEmpty = new Image(getClass().getResourceAsStream("/images/reversi/tile_empty_2.png"));
    private Image tileWhite = new Image(getClass().getResourceAsStream("/images/reversi/tile_white_2.png"));
    private Image tileBlack = new Image(getClass().getResourceAsStream("/images/reversi/tile_black_2.png"));
    
    private ArrayList<ArrayList<Button>> viewMap = new ArrayList<>();
    
    private Label turnLabel;
    private Label scoreWhiteLabel;
    private Label scoreBlackLabel;
    
    private Vector2 imgSize;
    
    private String btnStyle = "-fx-background-color: #000000, linear-gradient(#7ebcea, #2f4b8f),"+
            "linear-gradient(#426ab7, #263e75), linear-gradient(#395cab, #223768); -fx-background-insets: 0,1,2,3;"+
            "-fx-background-radius: 3,2,2,2; -fx-padding: 12 30 12 30; -fx-text-fill: white; -fx-font-size: 12px;";
    
    /**
     * The constructor. Creates a new model for this view.
     */
    public ReversiView() {
        this.model = new ReversiModel(this);
    }
    
    //TEMP
    public ReversiView(String name) {
        this();
        model.setClientName(name);
    }
    //TEMP
    
    /**
     * Start the game.
     */
    @Override
    public void startGame() {
        model.startApplication();
        start(stage);
    }
    
    /**
     * Reset the game and it's values.
     */
    @Override
    public void resetGame() {//TODO check if main reset and stuff , and if true reset all
        model.resetVariables();
        update();
    }
    
    public void closeGame() {
        model.closeConnection();
        stage.close();
    }
    
    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage) {
        mainMenu            = createMainMenu();
        multiplayerMenu     = createMultiplayerMenu();
        settingsMenu        = createSettingsMenu();
        choseModeMenu       = createChooseModeMenu();
        gameMenu            = createGameMenu();

        setActionListeners();
        
        multiplayerMenu.getStylesheets().add("scrollPane.css");
        this.stage.setScene(mainMenu);
        this.stage.setTitle("Reversi");
        this.stage.setResizable(false);
        this.stage.show();
    }
    
    private Scene createMainMenu() {
        // Create the main title for this screen
        Label title = new Label("Reversi");
        title.setTextFill(Color.web("#ffffff"));
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        line.setStroke(Color.web("#ffffff"));
        
        // Create all the buttons
        startButton = new Button("Play");
        multiplayerButton = new Button("Multiplayer");
        settingsButton = new Button("Settings");
        
        // Set the size for all buttons
        startButton.setMinSize(200, 40);
        multiplayerButton.setMinSize(200, 40);
        settingsButton.setMinSize(200, 40);
        
        startButton.setStyle(btnStyle);
        multiplayerButton.setStyle(btnStyle);
        settingsButton.setStyle(btnStyle);
        
        // Create a box that holds all the buttons from above
        VBox buttonHolder = new VBox(startButton, multiplayerButton, settingsButton);
        buttonHolder.setPadding(new Insets(100, 0, 100, 0));
        buttonHolder.setSpacing(50);
        buttonHolder.setAlignment(Pos.TOP_CENTER);
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, buttonHolder);
        vBox.setAlignment(Pos.TOP_CENTER);
        
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/images/reversi/background_texture.png")));
        StackPane container = new StackPane(background, vBox);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(container, windowSize.x, windowSize.y);
    }
    
    private Scene createMultiplayerMenu() {
        // Create the main title for this screen
        Label title = new Label("Multiplayer");
        title.setTextFill(Color.web("#ffffff"));
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        line.setStroke(Color.web("#ffffff"));
        
        // Create a box that holds all the player entries
        entryHolders = new VBox();
        entryHolders.setMinSize(windowSize.x, 0);
        entryHolders.setPadding(new Insets(60, 0, 0, 0));
        entryHolders.setSpacing(20);
        
        // Get a list of all online players
        refreshPlayerList(model.getPlayerList());
        
        ScrollPane scrollPane = new ScrollPane(entryHolders);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setMinSize(windowSize.x, 560);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        // Create the back button, which leads to the main menu
        Button backButton = new Button("<");
        backButton.setMinSize(100, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
        backButton.setStyle(btnStyle);
        
        // Create the refresh button, which refreshes the player list
        Button refreshButton = new Button("Refresh");
        refreshButton.setMinSize(100, 40);
        refreshButton.setOnAction(event -> model.refreshPlayerList());
        refreshButton.setStyle(btnStyle);
        
        HBox buttons = new HBox(backButton, refreshButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, scrollPane, buttons);
        vBox.setAlignment(Pos.TOP_CENTER);
    
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/images/reversi/background_texture.png")));
        StackPane container = new StackPane(background, vBox);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(container, windowSize.x, windowSize.y);
    }
    
    private Scene createSettingsMenu() {
        // Create the main title for this screen
        Label title = new Label("Settings");
        title.setTextFill(Color.web("#ffffff"));
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        line.setStroke(Color.web("#ffffff"));
        
        //region option: AI type
        
        Label aiTypeLabel = new Label("Computer algorithm");
        aiTypeLabel.setTextFill(Color.web("#ffffff"));
        
        // Create a choice box containing the light and dark theme
        ChoiceBox<String> aiTypeChoiceBox = new ChoiceBox<>();
        aiTypeChoiceBox.getItems().addAll("Random", "Minimax");
        aiTypeChoiceBox.setMinWidth(120);
        aiTypeChoiceBox.setValue("Minimax");
        aiTypeChoiceBox.setOnAction(e -> model.setAi(aiTypeChoiceBox.getValue().toLowerCase()));
    
        BorderPane aiTypeOption = new BorderPane();
        aiTypeOption.setMaxSize(500, 100);
        aiTypeOption.setPadding(new Insets(70, 0, 0, 0));
        aiTypeOption.setLeft(aiTypeLabel);
        aiTypeOption.setRight(aiTypeChoiceBox);
        
        //endregion
    
        Label networkingHeader = new Label("Networking settings");
        networkingHeader.setFont(new Font(24));
        networkingHeader.setPadding(new Insets(70, 0, 0, 0));
        networkingHeader.setTextFill(Color.web("#ffffff"));
        
        //region option: IP address
        
        Label ipAddressLabel = new Label("IP address");
        ipAddressLabel.setTextFill(Color.web("#ffffff"));
    
        TextField ipAddressField = new TextField(model.getIp());
        ipAddressField.setMinWidth(120);
        ipAddressField.addEventHandler(ActionEvent.ACTION, e -> model.setIp(ipAddressField.getText()));
        
        BorderPane ipAddressOption = new BorderPane();
        ipAddressOption.setMaxSize(500, 100);
        ipAddressOption.setPadding(new Insets(40, 0, 0, 0));
        ipAddressOption.setLeft(ipAddressLabel);
        ipAddressOption.setRight(ipAddressField);
        
        //endregion
    
        //region option: Port
    
        Label portLabel = new Label("Port");
        portLabel.setTextFill(Color.web("#ffffff"));
    
        TextField portField = new TextField(String.valueOf(model.getPort()));
        portField.setMinWidth(120);
        portField.addEventHandler(ActionEvent.ACTION, e -> model.setPort(Integer.parseInt(portField.getText())));
        
        // Force the field to be numeric only
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (portField.getLength() == 0) return;
                Integer.parseInt(newValue);
            }
            catch (NumberFormatException ignored) {
                portField.setText(portField.getText(0, portField.getLength() - 1));
            }
        });
    
        BorderPane portOption = new BorderPane();
        portOption.setMaxSize(500, 100);
        portOption.setPadding(new Insets(20, 0, 0, 0));
        portOption.setLeft(portLabel);
        portOption.setRight(portField);
    
        //endregion
        
        //region option: timeout
        
        Label timeoutLabel = new Label("Server timeout in seconds");
        timeoutLabel.setTextFill(Color.web("#ffffff"));
    
        TextField timeoutField = new TextField(String.valueOf(model.getTimeout()));
        timeoutField.setMinWidth(120);
        timeoutField.addEventHandler(ActionEvent.ACTION, e -> model.setTimeout(Integer.parseInt(timeoutField.getText())));
        
        BorderPane timeoutOption = new BorderPane();
        timeoutOption.setMaxSize(500, 100);
        timeoutOption.setPadding(new Insets(20, 0, 0, 0));
        timeoutOption.setLeft(timeoutLabel);
        timeoutOption.setRight(timeoutField);
        
        //endregion
        
        // Create the back button
        Button backButton = new Button("<");
        backButton.setMinSize(100, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
        backButton.setStyle(btnStyle);
        
        BorderPane backButtonHolder = new BorderPane();
        backButtonHolder.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.BOTTOM_CENTER);
        backButtonHolder.setPadding(new Insets(200, 0, 0, 0));
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, aiTypeOption, networkingHeader, ipAddressOption, portOption, timeoutOption, backButtonHolder);
        vBox.setAlignment(Pos.TOP_CENTER);
    
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/images/reversi/background_texture.png")));
        StackPane container = new StackPane(background, vBox);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(container, windowSize.x, windowSize.y);
    }
    
    private Scene createChooseModeMenu() {
        // Create the main title for this screen
        Label title = new Label("Choose game mode");
        title.setTextFill(Color.web("#ffffff"));
        title.setFont(new Font(32));
        title.setPadding(new Insets(100, 0, 0, 0));
        
        // Create a line that divides the title and the buttons
        Line line = new Line(-200, 0, 200, 0);
        line.setScaleY(4);
        line.setStroke(Color.web("#ffffff"));
        
        vsPlayerButton = new Button("Player vs Player");
        vsPlayerButton.setMinSize(200, 40);
        vsPlayerButton.setStyle(btnStyle);
        vsAiButton = new Button("Player vs Computer");
        vsAiButton.setMinSize(200, 40);
        vsAiButton.setStyle(btnStyle);
        
        VBox modeButtons = new VBox(vsPlayerButton, vsAiButton);
        modeButtons.setPadding(new Insets(150, 0, 100, 0));
        modeButtons.setSpacing(40);
        modeButtons.setAlignment(Pos.TOP_CENTER);
        
        Button backButton = new Button("<");
        backButton.setMinSize(100, 40);
        backButton.setOnAction(ReversiController.setSceneInStage(stage, mainMenu));
        backButton.setStyle(btnStyle);
        
        // Create a vertical box that hold all UI components created above
        VBox vBox = new VBox(title, line, modeButtons, backButton);
        vBox.setAlignment(Pos.TOP_CENTER);
    
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/images/reversi/background_texture.png")));
        StackPane container = new StackPane(background, vBox);
        
        // Set the above vertical box to the scene with a given width and height and return it
        return new Scene(container, windowSize.x, windowSize.y);
    }
    
    private Scene createGameMenu() {
        // Get and set some initial variables
        ReversiBoard board = model.getBoard();
        boolean isWhiteTurn = model.getBoard().isWhiteTurn();
        boolean isPlayerTurn = model.getBoard().isPlayerTurn();
        int mapSize = model.getBoard().getMapSize();
        imgSize = new Vector2(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);
        
        // Create and set the background
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/images/reversi/interface_design.png")));
        background.resize(windowSize.x, windowSize.y);
    
        // Create the back button
        Button backButton = new Button("Exit");
        backButton.setMinSize(90, 40);
        backButton.setStyle(btnStyle);
        backButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, mainMenu));
        backButton.addEventHandler(ActionEvent.ACTION, ReversiController.exitGame(model));
        VBox backButtonHolder = new VBox(backButton);
        backButtonHolder.setAlignment(Pos.TOP_LEFT);
        backButtonHolder.setPadding(new Insets(35, 0, 0, 35));
        backButtonHolder.setMaxSize(windowSize.x, 30);
    
        // Create the turn label, which shows who's turn it is
        turnLabel = new Label((isPlayerTurn ? "Your" : "Opponent's") + " turn");
        turnLabel.setAlignment(Pos.CENTER);
        turnLabel.setMinSize(fieldSize.x * 0.8f, 60);
        turnLabel.setFont(new Font(30));
        turnLabel.setTextFill(Color.web(isWhiteTurn ? "#ffffff" : "000000"));
        turnLabel.setPadding(new Insets(80, 0, 0, 0));
        
        //region Creation of score labels
        
        // Create the white label
        Label labelWhite = new Label("White");
        labelWhite.setTextFill(Color.web("#ffffff"));
        labelWhite.setFont(new Font(24));
        // Create the white score label
        scoreWhiteLabel = new Label("0");
        scoreWhiteLabel.setMinSize(40, 20);
        scoreWhiteLabel.setAlignment(Pos.CENTER);
        scoreWhiteLabel.setTextFill(Color.web("#ffffff"));
        scoreWhiteLabel.setFont(new Font(24));
        // Create the black label
        Label labelBlack = new Label("Black");
        labelBlack.setTextFill(Color.web("#000000"));
        labelBlack.setFont(new Font(24));
        // Create the black score label
        scoreBlackLabel = new Label("0");
        scoreBlackLabel.setMinSize(40, 20);
        scoreBlackLabel.setAlignment(Pos.CENTER);
        scoreBlackLabel.setTextFill(Color.web("#000000"));
        scoreBlackLabel.setFont(new Font(24));
    
        HBox whiteScoreHolder = new HBox(labelWhite, scoreWhiteLabel);
        HBox blackScoreHolder = new HBox(scoreBlackLabel, labelBlack);
        whiteScoreHolder.setSpacing(90);
        blackScoreHolder.setSpacing(100);
    
        BorderPane scoreHolder = new BorderPane();
        scoreHolder.setPadding(new Insets(50, 100, 15, 80));
        scoreHolder.setLeft(whiteScoreHolder);
        scoreHolder.setRight(blackScoreHolder);
        scoreHolder.setPadding(new Insets(157, 105, 0, 105));
        
        //endregion
        
        // Create a grid that will hold all game tile buttons
        GridPane grid = new GridPane();
    
        // Loop through the board
        String emptyId = board.getEmptyId();
        for (int y = 0; y < mapSize; y++) {
            viewMap.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                // Create a new button for this spot on the board
                Button btn = new Button();
                btn.setMinSize(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);
                viewMap.get(y).add(btn);
                viewMap.get(y).get(x).setId(emptyId);
                btn.setStyle("-fx-background-color: transparent;");
            
                // Create an image that we will put over the button
                ImageView img = new ImageView(tileEmpty);
                img.setFitWidth(fieldSize.x / (float)mapSize);
                img.setFitHeight(fieldSize.y / (float)mapSize);
                btn.setGraphic(img);
            
                // Set the event handler for the button
                btn.setOnAction(ReversiController.ButtonHandler(x, y, model));
            
                grid.add(btn, x, y);
            }
        }
        
        VBox gridHolder = new VBox(grid);
        gridHolder.setPadding(new Insets(240, 60, 60, 60));
    
        StackPane sp = new StackPane(background, turnLabel, scoreHolder, gridHolder, backButtonHolder);
        sp.setAlignment(Pos.TOP_CENTER);
    
        return new Scene(sp, windowSize.x, windowSize.y);
    }
    
    private void setActionListeners() {
//        startButton.setOnAction(ReversiController.setSceneInStage(stage, gameMenu));
        startButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, choseModeMenu));
        multiplayerButton.setOnAction(ReversiController.setSceneInStage(stage, multiplayerMenu));
        settingsButton.setOnAction(ReversiController.setSceneInStage(stage, settingsMenu));

//        vsPlayerButton.addEventHandler(ActionEvent.ACTION, event -> model.setAgainstPlayer(true));
        vsPlayerButton.addEventHandler(ActionEvent.ACTION, event -> model.gameStart(ReversiModel.GameMode.PLAYER_VS_PLAYER));
        vsPlayerButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, gameMenu));

//        vsAiButton.addEventHandler(ActionEvent.ACTION, event -> model.setAgainstPlayer(false));
        vsAiButton.addEventHandler(ActionEvent.ACTION, event -> model.gameStart(ReversiModel.GameMode.PLAYER_VS_AI));
        vsAiButton.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, gameMenu));
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
            actionButton.addEventHandler(ActionEvent.ACTION, ReversiController.challengePlayer(model, actionButton));
            actionButton.setMinSize(100, 40);
            actionButton.setId(player);
            
            // Create a border pane so we can align all components to the borders
            BorderPane entry = new BorderPane();
            entry.setBackground(new Background(new BackgroundFill(Color.web("#f2f2f2"), null, null)));
            entry.setMinSize(windowSize.x, 80);
            
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
        
        entryHolders.setMinSize(windowSize.x, (80 + 20) * players.length + 60);
    }
    
    /**
     * Update the black and white score labels.
     */
    public void updateScoreLabel() {
        Platform.runLater(() -> {
            ReversiBoard board = model.getBoard();
            scoreBlackLabel.setText(String.valueOf(board.getScoreBlack()));
            scoreWhiteLabel.setText(String.valueOf(board.getScoreWhite()));
        });
    }
    
    /**
     * Updates the turn label. The turn label shows who's turn it is.
     *
     * @param turn Boolean value that represents whether it is black of white's turn.
     */
    public void updateTurnLabel(boolean turn) {
        Platform.runLater(() -> {
            turnLabel.setText((turn ? "Your" : "Opponent's") + " turn");
            turnLabel.setTextFill(Color.web(model.getBoard().isWhiteTurn() ? "#ffffff" : "000000"));
        });
    }
    
    private void updateViewMap() {
        ReversiBoard board = model.getBoard();
        int mapSize = board.getMapSize();
        for (int y=0; y<mapSize; y++) {
            for(int x=0; x<mapSize; x++ ){
                String id = board.getModelMap().get(y).get(x);
                if(id.equals(board.getWhiteId())){
                    updateTileGraphic(true, x, y);
                }else if (id.equals(board.getBlackId())){
                    updateTileGraphic(false, x, y);
                }
                else {
                    updateTileGraphic(x, y);
                }
            }
        }
    }
    
    /**
     * Update the graphics (image) for a specific tile.
     *
     * @param turn Boolean value that represents whether it is black of white's turn.
     * @param xPos The X position on the board.
     * @param yPos The Y position on the board.
     */
    public void updateTileGraphic(boolean turn, int xPos, int yPos) {
        Platform.runLater(() -> {
            ImageView img = new ImageView(turn ? tileWhite : tileBlack);
            img.setFitWidth(imgSize.x);
            img.setFitHeight(imgSize.y);
            
            Button current = viewMap.get(yPos).get(xPos);
            current.setGraphic(img);
            current.setId(model.getBoard().getPlayerId(turn));
        });
    }
    public void updateTileGraphic(int xPos, int yPos) {
        Platform.runLater(() -> {
            ImageView img = new ImageView(tileEmpty);
            img.setFitWidth(imgSize.x);
            img.setFitHeight(imgSize.y);
            
            Button current = viewMap.get(yPos).get(xPos);
            current.setGraphic(img);
            current.setId(model.getBoard().getEmptyId());
        });
    }
    
    /**
     * General method for updating all view components.
     */
    public void update() {
        updateViewMap();
        updateScoreLabel();
//        updateTurnLabel(model.getBoard().isWhiteTurn());
        updateTurnLabel(model.getBoard().isPlayerTurn());
    }
    
    public String getTileId(int x, int y) { return viewMap.get(y).get(x).getId(); }
    
    public void challengeReceived(String challenger, String nr) {
        for (Node entry : entryHolders.getChildren()) {
            for (Node node : ((BorderPane) entry).getChildren()) {
                VBox b = (VBox) node;
                Node n = b.getChildren().get(0);
                String entryId = n.getId();
                
                if (entryId != null) {
                    if (entryId.equals(challenger)) {
                        model.log(entryId);
                        Button btn = ((Button) n);
                        
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
    
    public void goToMainMenu() {
        Platform.runLater(() -> {
            stage.setScene(mainMenu);
        });
    }
}
