package games.tictactoe;

import games.Game;
import games.Model;
import games.Vector2;
import games.reversi.ReversiController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.ArrayList;

import java.util.Optional;

/**
 * contains all the GUI element of the toc-tac-toe game
 *
 * @author Maric Aronds
 * @version 1.0
 */
public class TicTacToeView extends Game {
    private Vector2 playField = new Vector2(600, 600);

    private ArrayList<ArrayList<Button>> viewMap = new ArrayList<>();
    private TicTacToeModel model;

    private Image eBox = new Image(getClass().getResourceAsStream("/images/tictactoe/ttt_empty.png"));
    private Image oBox = new Image(getClass().getResourceAsStream("/images/tictactoe/o_tile.png"));
    private Image xBox = new Image(getClass().getResourceAsStream("/images/tictactoe/x_tile.png"));

    private Label turnLabel;
    private Label xScoreLabel;
    private Label oScoreLabel;

    private Label xLabel;
    private Label oLabel;

    private boolean hasPopup = false;

    private String cf4 = "-fx-text-fill: #f4f4f4";
    private String c1c = "-fx-text-fill: #1c1c1c";

    private Scene mainMenu;
    private Scene settingsMenu;
    private Scene choseModeMenu;
    private Scene gameMenu;

    private Button continueGame;
    private Button newGame;
    private Button settings;
    private Button mainMenuButton;
    private Button settingsScreenButton;
    private Button okButton;
    private Button backButton;

    private ToggleButton togglePlayerX;
    private ToggleButton togglePlayerO;

    private Alert settingsAlert;

    private Popup popup;

    private Vector2 imgSize;

    public TicTacToeView() {
        this.model = new TicTacToeModel(this);
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
        model.resetVariables();
        update();
    }

    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage) {
        mainMenu = createMainMenu();
        settingsMenu = createSettingsMenu();
        gameMenu = createGameMenu();

        setActionListeners();

        this.stage.setScene(mainMenu);
        this.stage.setTitle("TicTacToe");
        this.stage.setResizable(false);
        this.stage.show();
    }

    private Scene createMainMenu() {
        // Home Scene
        VBox homeLayout = new VBox(20);
        homeLayout.setStyle("-fx-background-color: #2b2b2b;");

        Label homeTitle = new Label("Tic Tac Toe");
        homeTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f4f4f4");
        homeTitle.setFont(new Font(50));

        continueGame = new Button("Continue game");
        continueGame.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        newGame = new Button("New game");

        newGame.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        settings = new Button("Settings");
        settings.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");

        homeLayout.getChildren().addAll(homeTitle, continueGame, newGame, settings);
        homeLayout.setAlignment(Pos.CENTER);
        return new Scene(homeLayout, 600, 600);
    }

    private Scene createGameMenu() {
        TicTacToeBoard board = model.getBoard();
        boolean isTurn = board.getWhiteTurn();

        // GameScene
        VBox gameLayout = new VBox();
        gameLayout.setStyle("-fx-background-color: #2b2b2b;");

        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setPadding(new Insets(70));
        float extraWidth = (float) (gameLayout.getPadding().getLeft() + gameLayout.getPadding().getRight());
        float extraHeight = (float) (gameLayout.getPadding().getTop() + gameLayout.getPadding().getBottom());

        //Turn label
        turnLabel = new Label((isTurn ? "O" : "X") + "'s turn");
        turnLabel.setFont(new Font(30));
        turnLabel.setStyle(cf4);

        //Score labels
        HBox hbox = new HBox();
        xLabel = new Label("X:");
        oLabel = new Label("O:");
        oScoreLabel = new Label("0");
        xScoreLabel = new Label("0");
        oLabel.setStyle(cf4);
        xLabel.setStyle(cf4);
        xLabel.setStyle(cf4);
        xScoreLabel.setStyle(cf4);
        oScoreLabel.setStyle(cf4);
        xScoreLabel.setPadding(new Insets(0, 100, 0, 0));
        hbox.getChildren().addAll(xLabel, xScoreLabel, oLabel, oScoreLabel);
        hbox.setAlignment(Pos.CENTER);

        //creating grid
        GridPane grid = new GridPane();

        for (int y = 0; y < 3; y++) {
            viewMap.add(new ArrayList<>());
            for (int x = 0; x < 3; x++) {
                Button btn = new Button();
                viewMap.get(y).add(btn);
                btn.setStyle("-fx-background-radius:0;" + "-fx-focus-color: #cfcfcf;");
                viewMap.get(y).get(x).setId(model.getBoard().getEmptyId());

                btn.setMinSize(150, 150);

                ImageView img = new ImageView(eBox);
                img.setFitWidth(150);
                img.setFitHeight(151);
                btn.setGraphic(img);

                btn.setOnAction(TicTacToeController.ButtonHandler(x, y, model));

                grid.add(btn, x, y);
            }
        }

        imgSize = new Vector2(150,151);
        //sets gap between buttons
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #1c1c1c");
        HBox topbar = createTopBar();
        gameLayout.getChildren().addAll(topbar, turnLabel, hbox, grid);
        return new Scene(gameLayout, playField.x, playField.y);
    }

    public Scene createSettingsMenu(){
        // SettingsScene
        VBox settingsLayout = new VBox();
        settingsLayout.setStyle("-fx-background-color: #2b2b2b");
        TicTacToeBoard board = model.getBoard();

        // Title bar
        HBox settingsBar = new HBox();
        settingsBar.setPadding(new Insets(10, 12, 15, 12));
        settingsBar.setSpacing(10);
        settingsBar.setStyle("-fx-background-color: #323232");
        Label settingsLabel = new Label("Settings");
        settingsLabel.setStyle(cf4);
        settingsLabel.setFont(new Font(25));
        settingsLabel.setAlignment(Pos.CENTER);
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);

        settingsAlert = new Alert(Alert.AlertType.NONE);
        settingsAlert.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        settingsAlert.setAlertType(Alert.AlertType.WARNING);settingsAlert.setContentText("If you continue the board will be cleared. This doesn't affect the score");


        StackPane backButtonStack = new StackPane();
        backButton = new Button("Done");
        backButton.setStyle("-fx-background-color: transparent;" + "-fx-font-weight: bold;" + "-fx-font-size: 20;" + "-fx-padding: 0;" + cf4);

        backButtonStack.setAlignment(Pos.CENTER_RIGHT);
        backButtonStack.getChildren().add(backButton);
        settingsBar.getChildren().addAll(settingsLabel, spacer, backButtonStack);


        HBox selectPlayer = new HBox();
        selectPlayer.setPadding(new Insets(10));
        selectPlayer.setSpacing(5);
        selectPlayer.setAlignment(Pos.CENTER);
        ToggleGroup togglePlayer = new ToggleGroup();
        Label togglePlayerLabel = new Label("Choose your player:");
        togglePlayerLabel.setStyle(cf4);
        togglePlayerLabel.setFont(new Font(20));

        togglePlayerX = new ToggleButton("X");
        togglePlayerO = new ToggleButton("O");

        if (board.getPlayerTurn()){
            togglePlayerO.setSelected(true);
        } else {
            togglePlayerX.setSelected(true);
        }

        togglePlayerO.setToggleGroup(togglePlayer);
        togglePlayerX.setToggleGroup(togglePlayer);

        selectPlayer.getChildren().addAll(togglePlayerLabel, togglePlayerX, togglePlayerO);

        settingsLayout.getChildren().addAll(settingsBar, selectPlayer);

        return new Scene(settingsLayout, 600, 600);
    }

    public HBox createTopBar(){
        HBox topbar = new HBox(65);
        mainMenuButton = new Button("Home");
        mainMenuButton.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");

        Label gameTitle = new Label("Tic Tac Toe");
        gameTitle.setStyle("-fx-font-size: 30;" + cf4);
        gameTitle.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 25));
//        gameTitle.setFont(new Font(30));

        //Settings button
        settingsScreenButton = new Button("Settings");
        settingsScreenButton.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        topbar.getChildren().addAll(mainMenuButton, gameTitle, settingsScreenButton);
        topbar.setAlignment(Pos.CENTER);

        return topbar;
    }

    private void setActionListeners() {
        mainMenuButton.setOnAction(TicTacToeController.setSceneInStage(stage,mainMenu)); // add setScene action to mainMenuButton
        continueGame.setOnAction(TicTacToeController.setSceneInStage(stage, gameMenu));

        newGame.addEventHandler(ActionEvent.ACTION, event -> model.gameStart(Model.GameMode.PLAYER_VS_PLAYER));
        newGame.addEventHandler(ActionEvent.ACTION, ReversiController.setSceneInStage(stage, gameMenu));

        settings.setOnAction(TicTacToeController.setSceneInStage(stage, settingsMenu));
        settingsScreenButton.setOnAction(TicTacToeController.setSceneInStage(stage, settingsMenu));

        backButton.addEventHandler(ActionEvent.ACTION,event -> { Optional<ButtonType> result = settingsAlert.showAndWait();if (result.get() == ButtonType.OK){ model.resetVariables(); }});
        backButton.addEventHandler(ActionEvent.ACTION,TicTacToeController.setSceneInStage(stage,gameMenu));

        togglePlayerX.addEventHandler(ActionEvent.ACTION, e -> model.getBoard().setPlayerToWhite(false));
        togglePlayerO.addEventHandler(ActionEvent.ACTION, e -> model.getBoard().setPlayerToWhite(true));
        togglePlayerX.addEventHandler(ActionEvent.ACTION, e -> model.setAiPlayer(true));
        togglePlayerO.addEventHandler(ActionEvent.ACTION, e -> model.setAiPlayer(false));

    }

    @Override
    public void update() {
        updateViewMap();
        updateScoreLabel();
        updateTurnLabel(model.getBoard().isWhiteTurn());
    }

    @Override
    public void closeGame() {
        stage.close();
    }

    /**
     * Update the black and white score labels.
     */
    public void updateScoreLabel() {
        Platform.runLater(() -> {
            TicTacToeBoard board = model.getBoard();
            xScoreLabel.setText(String.valueOf(board.getScoreBlack()));
            oScoreLabel.setText(String.valueOf(board.getScoreWhite()));
        });
    }

    /**
     * Updates the turn label. The turn label shows who's turn it is.
     *
     * @param turn Boolean value that represents whether it is black of white's turn.
     */
    public void updateTurnLabel(boolean turn) {
        Platform.runLater(() -> {
            turnLabel.setText((turn ? "O" : "X") + "'s turn");
            turnLabel.setTextFill(Color.web(turn ? "#ffffff" : "000000"));
        });
    }

    private void updateViewMap() {
        TicTacToeBoard board = model.getBoard();
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
            ImageView img = new ImageView(turn ? oBox : xBox);
            img.setFitWidth(imgSize.x);
            img.setFitHeight(imgSize.y);

            Button current = viewMap.get(yPos).get(xPos);
            current.setGraphic(img);
            current.setId(model.getBoard().getPlayerId(turn));
        });
    }
    public void updateTileGraphic(int xPos, int yPos) {
        Platform.runLater(() -> {
            ImageView img = new ImageView(eBox);
            img.setFitWidth(imgSize.x);
            img.setFitHeight(imgSize.y);

            Button current = viewMap.get(yPos).get(xPos);
            current.setGraphic(img);
            current.setId(model.getBoard().getEmptyId());
        });
    }


    public void hasWon(String player) {
        popup = new Popup();
        VBox popupBox = new VBox();

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        popupBox.setEffect(dropShadow);

        Label fooHasWon = new Label();
        fooHasWon.setMinHeight(50);
        fooHasWon.setMinWidth(250);
        fooHasWon.setAlignment(Pos.CENTER);

        popupBox.setBackground(new Background(new BackgroundFill(Color.web("#fff"), null, null)));
        popupBox.setStyle("-fx-padding: 20 0;");
        popupBox.setAlignment(Pos.CENTER);

        okButton = new Button("Play again!");
        okButton.setOnAction(event -> {model.gameStart(Model.GameMode.PLAYER_VS_PLAYER); popup.hide();hasPopup = false;});
        okButton.setMinWidth(200);
        okButton.setMinHeight(50);

        popupBox.getChildren().addAll(fooHasWon, okButton);
        popup.getContent().addAll(popupBox);

        TicTacToeBoard board = model.getBoard();

        if (player.equals(board.getWhiteId())) {
            if (Integer.valueOf(oScoreLabel.getText()) == 4) {
                fooHasWon.setText("O has won the game and reached 5 points!");
                okButton.setText("Start new game");
                xScoreLabel.setText("0");
                oScoreLabel.setText("-1");
            } else {
                fooHasWon.setText("O has won the game!");
            }
        } else if (player.equals(board.getBlackId())) {
            if (Integer.valueOf(xScoreLabel.getText()) == 4) {
                fooHasWon.setText("X has won the game and reached 5 points!");
                okButton.setText("Start new game");
                xScoreLabel.setText("-1");
                oScoreLabel.setText("0");
            } else {
                fooHasWon.setText("X has won the game!");
            }
        } else {
            fooHasWon.setText("It's a draw!");
        }

        if (!popup.isShowing()) {
            popup.show(stage);
        } else {
            popup.hide();
        }

    }

    @Override
    public void startMatch() {
        Platform.runLater(() -> {
            stage.setScene(gameMenu);
        });
    }

    @Override
    public void goToMainMenu() {
        Platform.runLater(() -> {
            stage.setScene(mainMenu);
        });
    }
}
