package games.tictactoe;

import games.Game;
import games.Vector2;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.Stack;

public class TicTacToeView extends Game {
    private Stage window;
    private Scene homeScene, gameScene, settingsScene;

    private TicTacToeModel model;

    private Label xScoreLabel = new Label("0");
    private Label oScoreLabel = new Label("0");

    private Vector2 playField = new Vector2(600, 600);
    private ArrayList<ArrayList<Button>> viewMap = new ArrayList<>();

    private Image eBox = new Image(getClass().getResourceAsStream("/images/tictactoe/ttt_empty.png"));
    private Image oBox = new Image(getClass().getResourceAsStream("/images/tictactoe/o_tile.png"));
    private Image xBox = new Image(getClass().getResourceAsStream("/images/tictactoe/x_tile.png"));

    private boolean isTurn = false; //if false -> X's turn, if true -> O's turn
    private Label labelTurn;
    private boolean hasPopup = false;

    private String cf4 = "-fx-text-fill: #f4f4f4";
    private String c1c = "-fx-text-fill: #1c1c1c";

    TicTacToeView() {
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
        xScoreLabel.setText("0");
        oScoreLabel.setText("0");
        isTurn = false;
        viewMap = new ArrayList<>();
    }


    @Override
    public void update() {

    }

    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage) {
        //primary stage
        window = this.stage;
        window.setTitle("Tic Tac Toe");

        HBox topbar = new HBox(65);
        Button homeScreenButton = new Button("Home");
        homeScreenButton.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        homeScreenButton.setOnAction(e -> window.setScene(homeScene));

        Label gameTitle = new Label("Tic Tac Toe");
        gameTitle.setStyle("-fx-font-size: 30;" + cf4);
        gameTitle.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 25));
//        gameTitle.setFont(new Font(30));

        //Settings button
        Button settingsScreenButton = new Button("Settings");
        settingsScreenButton.setOnAction(e -> window.setScene(settingsScene));
        settingsScreenButton.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        topbar.getChildren().addAll(homeScreenButton, gameTitle, settingsScreenButton);
        topbar.setAlignment(Pos.CENTER);

        // Home Scene
        VBox homeLayout = new VBox(20);
        homeLayout.setStyle("-fx-background-color: #2b2b2b;");

        Label homeTitle = new Label("Tic Tac Toe");
        homeTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f4f4f4");
        homeTitle.setFont(new Font(50));
        Button continueGame = new Button("Continue game");
        continueGame.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        continueGame.setOnAction(e -> window.setScene(gameScene));
        Button newGame = new Button("New game");
        newGame.setOnAction((event) -> {    // lambda expression
            oScoreLabel.setText("0");
            xScoreLabel.setText("0");
            resetBoard();
            window.setScene(gameScene);
        });
        newGame.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        Button settings = new Button("Settings");
        settings.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        settings.setOnAction(e -> window.setScene(settingsScene));

        homeLayout.getChildren().addAll(homeTitle, continueGame, newGame, settings);
        homeLayout.setAlignment(Pos.CENTER);
        homeScene = new Scene(homeLayout, 600, 600);

        // End of Home Scene

        // GameScene
        VBox gameLayout = new VBox();
        gameLayout.setStyle("-fx-background-color: #2b2b2b;");

        gameLayout.setAlignment(Pos.CENTER);
        window.setResizable(false);
        gameLayout.setPadding(new Insets(70));
        float extraWidth = (float) (gameLayout.getPadding().getLeft() + gameLayout.getPadding().getRight());
        float extraHeight = (float) (gameLayout.getPadding().getTop() + gameLayout.getPadding().getBottom());

        //Turn label
        labelTurn = new Label((isTurn ? "O" : "X") + "'s turn");
        labelTurn.setFont(new Font(30));
        labelTurn.setStyle(cf4);

        //Score labels
        HBox hbox = new HBox();
        Label xLabel = new Label("X:");
        Label oLabel = new Label("O:");
        oLabel.setStyle(cf4);
        xLabel.setStyle(cf4);
        xLabel.setStyle(cf4);
        xScoreLabel.setStyle(cf4);
        oScoreLabel.setStyle(cf4);
        //Label xScoreLabel = new Label("0");
        //Label oScoreLabel = new Label("0");
        xScoreLabel.setPadding(new Insets(0, 100, 0, 0));
        hbox.getChildren().addAll(xLabel, xScoreLabel, oLabel, oScoreLabel);
        hbox.setAlignment(Pos.CENTER);

        //creating grid
        GridPane grid = new GridPane();

        //sets gap between buttons
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #1c1c1c");

        //creating buttons
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

                btn.setOnAction(new ButtonHandler(x, y, this));

                grid.add(btn, x, y);
            }
        }
        gameLayout.getChildren().addAll(topbar, labelTurn, hbox, grid);
        gameScene = new Scene(gameLayout, playField.x, playField.y);
        // End of GameScene

        // SettingsScene
        VBox settingsLayout = new VBox();
        settingsLayout.setStyle("-fx-background-color: #2b2b2b");

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

        Alert settingsAlert = new Alert(Alert.AlertType.NONE);
        settingsAlert.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        settingsAlert.setAlertType(Alert.AlertType.WARNING);settingsAlert.setContentText("If you continue the board will be cleared. This doesn't affect the score");


        StackPane backButtonStack = new StackPane();
        Button backButton = new Button("Done");
        backButton.setStyle("-fx-background-color: transparent;" + "-fx-font-weight: bold;" + "-fx-font-size: 20;" + "-fx-padding: 0;" + cf4);

        backButton.setOnAction(e -> {
            Optional<ButtonType> result = settingsAlert.showAndWait();
            if (result.get() == ButtonType.OK){
                window.setScene(gameScene);
                resetBoard();
                labelTurn.setText((isTurn ? "O" : "X") + "'s turn");
            }
        });

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

        ToggleButton togglePlayerX = new ToggleButton("X");
        ToggleButton togglePlayerO = new ToggleButton("O");

        if (isTurn){
            togglePlayerO.setSelected(true);
        } else {
            togglePlayerX.setSelected(true);
        }

        togglePlayerX.setOnAction(e -> isTurn = false);
        togglePlayerO.setOnAction(e -> isTurn = true);

        togglePlayerO.setToggleGroup(togglePlayer);
        togglePlayerX.setToggleGroup(togglePlayer);

        selectPlayer.getChildren().addAll(togglePlayerLabel, togglePlayerX, togglePlayerO);



        settingsLayout.getChildren().addAll(settingsBar, selectPlayer);

        settingsScene = new Scene(settingsLayout, 600, 600);
        // End of settingsScene

        window.setScene(homeScene);
        window.show();
    }

    private Scene createMainMenu() {

    }

    private Scene createGameMenu() {

    }

    private void onTileClick(int xPos, int yPos) {
        Button current = viewMap.get(yPos).get(xPos);
        String currentId = current.getId();

        if (!hasPopup){
            if (!currentId.equals(emptyId)) {
                return;
            }else {
                ImageView img = new ImageView(isTurn ? oBox : xBox);
                img.setFitWidth(150);
                img.setFitHeight(151);
                current.setGraphic(img);
                current.setId(isTurn ? oId : xId);
            }
            if(isGameOver()){
                if(checkWinner(xId)){
                    hasWon(xId);
                    xScoreLabel.setText(""+(Integer.valueOf(xScoreLabel.getText())+1));
                }else if(checkWinner(oId)){
                    hasWon(oId);
                    oScoreLabel.setText(""+(Integer.valueOf(oScoreLabel.getText())+1));
                } else {
                    hasWon("draw");
                }

                hasPopup = true;
                return;
            }
            swapTurn();
        }

    }

    private void hasWon(String player) {
        Popup popup = new Popup();
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

        Button okButton = new Button("Play again!");

        okButton.setOnAction(e -> {resetBoard(); popup.hide();hasPopup = false;});
        okButton.setMinWidth(200);
        okButton.setMinHeight(50);

        popupBox.getChildren().addAll(fooHasWon, okButton);
        popup.getContent().addAll(popupBox);

        if (player.equals(oId)) {
            if (Integer.valueOf(oScoreLabel.getText()) == 4) {
                fooHasWon.setText("O has won the game and reached 5 points!");
                okButton.setText("Start new game");
                xScoreLabel.setText("0");
                oScoreLabel.setText("-1");
            } else {
                fooHasWon.setText("O has won the game!");
            }
        } else if (player.equals(xId)) {
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

    class ButtonHandler implements EventHandler<ActionEvent> {

        private int xPos;
        private int yPos;
        private TicTacToeView main;

        public ButtonHandler(int xPos, int yPos, TicTacToeView main) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.main = main;
        }

        @Override
        public void handle(ActionEvent event) {
            main.onTileClick(xPos, yPos);
        }
    }

    @Override
    public void startMatch() {

    }

    @Override
    public void goToMainMenu() {

    }
}
