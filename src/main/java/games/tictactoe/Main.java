package games.tictactoe;

import games.Game;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Stack;

public class Main extends Game {
    private Stage window;
    private Scene homeScene, gameScene, settingsScene;

    private static final String emptyId = "e";
    private static final String xId = "x";
    private static final String oId = "o";

    private Label xScoreLabel = new Label("0");
    private Label oScoreLabel = new Label("0");

    private Controller playField = new Controller(600, 600);
    private ArrayList<ArrayList<Button>> board = new ArrayList<>();

    private Image eBox = new Image(getClass().getResourceAsStream("/images/tictactoe/ttt_empty.png"));
    private Image oBox = new Image(getClass().getResourceAsStream("/images/tictactoe/o_tile.png"));
    private Image xBox = new Image(getClass().getResourceAsStream("/images/tictactoe/x_tile.png"));

    private  boolean isTurn = false; //if false -> X's turn, if true -> O's turn
    private Label labelTurn;

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
        board = new ArrayList<>();
    }

    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage){
        //primary stage
        window = this.stage;
        window.setTitle("Tic Tac Toe");

        HBox topbar = new HBox(65);
        Button homeScreenButton = new Button("Home");
        homeScreenButton.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        homeScreenButton.setOnAction(e -> window.setScene(homeScene));

        Label gameTitle = new Label("Tic Tac Toe");
        gameTitle.setStyle("-fx-font-weight: 500;" + "-fx-font-size: 30;");
//        gameTitle.setFont(new Font(30));

        //Settings button
        Button settingsScreenButton = new Button("Settings");
        settingsScreenButton.setOnAction(e -> window.setScene(settingsScene));
        settingsScreenButton.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        topbar.getChildren().addAll(homeScreenButton, gameTitle, settingsScreenButton);
        topbar.setAlignment(Pos.CENTER);

        // Home Scene
        VBox homeLayout = new VBox(20);

        Label homeTitle = new Label("Tic Tac Toe");
        homeTitle.setStyle("-fx-font-weight: bold");
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
//        gameLayout.getChildren().add(test);

        gameLayout.setAlignment(Pos.CENTER);
        window.setResizable(false);
        gameLayout.setPadding(new Insets(70));
        float extraWidth = (float)(gameLayout.getPadding().getLeft() + gameLayout.getPadding().getRight());
        float extraHeight = (float)(gameLayout.getPadding().getTop() + gameLayout.getPadding().getBottom());

        //Turn label
        labelTurn = new Label(isTurn ? "O" : "X" + "'s turn");
        labelTurn.setFont(new Font(30));

        //Score labels
        HBox hbox = new HBox();
        Label xLabel = new Label("X:");
        Label oLabel = new Label("O:");
        //Label xScoreLabel = new Label("0");
        //Label oScoreLabel = new Label("0");
        xScoreLabel.setPadding(new Insets(0 ,100, 0, 0));
        hbox.getChildren().addAll(xLabel, xScoreLabel, oLabel, oScoreLabel);
        hbox.setAlignment(Pos.CENTER);

        //creating grid
        GridPane grid = new GridPane();

        //sets gap between buttons
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #2a2a2a;");

        //creating buttons
        for (int y = 0; y < 3; y++) {
            board.add(new ArrayList<>());
            for (int x = 0; x < 3; x++) {
                Button btn = new Button();
                board.get(y).add(btn);
                btn.setStyle("-fx-background-radius:0;" + "-fx-focus-color: #cfcfcf;");
                board.get(y).get(x).setId(emptyId);

                btn.setMinSize(150, 150);

                ImageView img = new ImageView(eBox);
                img.setFitWidth(150);
                img.setFitHeight(150);
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

        // Title bar
        HBox settingsBar = new HBox();
        settingsBar.setPadding(new Insets(10, 12, 15, 12));
        settingsBar.setSpacing(10);
        settingsBar.setStyle("-fx-border-style: none none none none; -fx-border-width: 1; -fx-border-color: #cfcfcf;");
        Label settingsLabel = new Label("Settings");
        settingsLabel.setFont(new Font(25));
        settingsLabel.setAlignment(Pos.CENTER);
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);

        StackPane backButtonStack = new StackPane();
        Button backButton = new Button("Done");
        backButton.setStyle("-fx-background-color: transparent;" + "-fx-font-weight: bold;" + "-fx-font-size: 20;" + "-fx-padding: 0;");
//        backButton.getOnMouseDragOver(backButton.setStyle("-fx-cursor: hand;"));
        backButton.setOnAction(e -> window.setScene(gameScene));
        backButtonStack.setAlignment(Pos.CENTER_RIGHT);
        backButtonStack.getChildren().add(backButton);
        settingsBar.getChildren().addAll(settingsLabel, spacer, backButtonStack);

        settingsLayout.getChildren().addAll(settingsBar);

        settingsScene = new Scene(settingsLayout, 600, 600);
        // End of settingsScene

        window.setScene(homeScene);
        window.show();
    }

    private boolean checkEmptySpaces(){
        int emptySpaces = 0;
        for (int j=0; j<3; j++){
            for (int i=0; i<3; i++){
                if(board.get(i).get(j).getId().equals(emptyId)){
                    emptySpaces++;
                }
            }
        }
        return emptySpaces == 0;
    }

    private boolean isGameOver(){
        return checkWinner(xId) || checkWinner(oId) || checkEmptySpaces();
    }

    boolean checkWinner(String player) {
        //Diagonal wins.
        if ((board.get(0).get(0).getId().equals(board.get(1).get(1).getId()) && board.get(0).get(0).getId().equals(board.get(2).get(2).getId()) && board.get(0).get(0).getId().equals(player)) ||
                (board.get(2).get(0).getId().equals(board.get(1).get(1).getId()) && board.get(2).get(0).getId().equals(board.get(0).get(2).getId()) && board.get(2).get(0).getId().equals(player))) {
            return true;
        }
        //Vertical and horizontal wins.
        for (int i = 0; i < 3; ++i) {
            if (((board.get(i).get(0).getId().equals(board.get(i).get(1).getId()) && board.get(i).get(0).getId().equals(board.get(i).get(2).getId()) && board.get(i).get(0).getId().equals(player))
                    || (board.get(0).get(i).getId().equals(board.get(1).get(i).getId()) && board.get(0).get(i).getId().equals(board.get(2).get(i).getId()) && board.get(0).get(i).getId().equals(player)))) {
                return true;
            }
        }
        return false;
    }

    private void resetBoard(){
        for(int j=0; j<3; j++){
            for(int i=0; i<3; i++){
                ImageView img = new ImageView(eBox);
                img.setFitWidth(150);
                img.setFitHeight(150);
                board.get(i).get(j).setGraphic(img);
                board.get(i).get(j).setId(emptyId);

            }
        }
    }

    private void hasWon(String player){
        Popup popup = new Popup();
        Label fooHasWon = new Label("eww");
        Button okButton = new Button("confirm");
        okButton.setOnAction(e -> popup.hide());
        okButton.setAlignment(Pos.CENTER);
        fooHasWon.setMinWidth(250);
        fooHasWon.setMinHeight(250);
        fooHasWon.setAlignment(Pos.CENTER);
        fooHasWon.setStyle("-fx-background-color: #fff");
        popup.getContent().addAll(fooHasWon, okButton);
        if (player.equals(oId)){
            fooHasWon.setText("O has Won");
        } if(player.equals(xId)) {
            fooHasWon.setText("X has Won");
        } else {
            fooHasWon.setText("It's a draw!");
        }
        System.out.println(fooHasWon);
        if (!popup.isShowing())
            popup.show(stage);
        else
            popup.hide();

    }

    private void onTileClick(int xPos, int yPos){
        Button current = board.get(yPos).get(xPos);
        String currentId = current.getId();

        if (!currentId.equals(emptyId)) {
            System.out.println("eee");
            return;
        }else {
            ImageView img = new ImageView(isTurn ? oBox : xBox);
            img.setFitWidth(150);
            img.setFitHeight(150);
            current.setGraphic(img);
            current.setId(isTurn ? oId : xId);
        }
        if(isGameOver()){
            if(checkWinner(xId)){
                //TODO print /label x has won the game
                hasWon(xId);
                xScoreLabel.setText(""+(Integer.valueOf(xScoreLabel.getText())+1));
            }else if(checkWinner(oId)){
                //TODO print /label o has won the game
//                Label oWon = new Label("O has won the game");
                hasWon(oId);
                oScoreLabel.setText(""+(Integer.valueOf(oScoreLabel.getText())+1));

            } else {
                hasWon("");
            }
            resetBoard();
            //TODO if score == 5 print x has won the game and reset the game
            return;
        }
        swapTurn();
    }

    class ButtonHandler implements EventHandler<ActionEvent> {
        private int xPos;
        private int yPos;
        private Main main;

        public ButtonHandler(int xPos, int yPos, Main main) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.main = main;
        }

        @Override
        public void handle(ActionEvent event) {
            main.onTileClick(xPos, yPos);
        }
    }

    private void swapTurn () {

        if (isTurn) isTurn = false;
        else isTurn = true;

        labelTurn.setText((isTurn ? "O" : "X") + "'s turn");
    }

//    public String getPosId(int x, int y) { return board.get(y).get(x).getId(); }
//    public String getPlayerId(boolean turn) { return isTurn ? oId : xId; }
//    public String getEmptyId() { return emptyId; }
//    public boolean getTurn() { return isTurn; }
//    public Controller getPlayField() { return playField; }
//    public Label getScoreO() { return oScoreLabel; }
//    public Label getScoreX() { return xScoreLabel; }


    public static void main(String[] args) {
        launch(args);
    }
}

