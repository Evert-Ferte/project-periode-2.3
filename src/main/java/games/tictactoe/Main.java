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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Stack;

public class Main extends Game {
    private Stage window;
    private Scene homeScene, gameScene, settingsScene;

    private static final String emptyId = "e";
    private static final String xId = "x";
    private static final String oId = "o";
    //board size x * x
    private static final int boardSize = 3;

    private Controller playField = new Controller(600, 600);
    private ArrayList<ArrayList<Button>> board = new ArrayList<>();

    private Image eBox = new Image(getClass().getResourceAsStream("/images/tictactoe/ttt_empty.png"));
    private Image oBox = new Image(getClass().getResourceAsStream("/images/tictactoe/o_tile.png"));
    private Image xBox = new Image(getClass().getResourceAsStream("/images/tictactoe/x_tile.png"));

    private  boolean isTurn = false; //if false -> X's turn, if true -> O's turn
    private Label labelTurn;

    String[][] boardArjan = {{"x", "x", "o"},
            {"o", "x", "x"},
            {"o", "o", "o"}};
    
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
        //TODO implementeer deze methode (reset de nodige variabelen hier)
    }
    
    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage){
        //primary stage
        window = this.stage;    //TODO - kan weg als je wil. deze class wordt nu ge-overerfd van de class game.java, deze heeft een eigen stage variabel. kan je bij door this.stage te doen.
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
        Button continueGame = new Button("Continue");
        continueGame.setStyle("-fx-background-color: #1da4e2;" + "-fx-background-radius: 30;" + "fx-border-radius: 30;" + "-fx-padding: 8 20;" + "-fx-font-weight: bold;" + "-fx-font-size: 15;" + "-fx-text-fill: white");
        continueGame.setOnAction(e -> window.setScene(gameScene));
        Button newGame = new Button("New game");
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
        Label xScoreLabel = new Label("5");
        Label oScoreLabel = new Label("3");
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
        for (int y = 0; y < boardSize; y++) {
            board.add(new ArrayList<>());
            for (int x = 0; x < boardSize; x++) {
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

    private void onTileClick(int xPos, int yPos){
        Button current = board.get(yPos).get(xPos);
        String currentId = current.getId();

        if (!currentId.equals(emptyId))
            return;
        else {
            ImageView img = new ImageView(isTurn ? oBox : xBox);
            img.setFitWidth(150);
            img.setFitHeight(150);
            current.setGraphic(img);
            current.setId(isTurn ? oId : xId);
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





//    private static final String[] players = {"x", "o"};
//
//    private static void checkGameOver(String[][] boardArjan) {
//        for(String player: players){
//            if (((boardArjan[0][0].equals(player))&&(boardArjan[0][1].equals(player))&&(boardArjan[0][2].equals(player)))||
//                    ((boardArjan[1][0].equals(player))&&(boardArjan[1][1].equals(player))&&(boardArjan[1][2].equals(player)))||
//                    ((boardArjan[2][0].equals(player))&&(boardArjan[2][1].equals(player))&&(boardArjan[0][2].equals(player)))||
//
//                    ((boardArjan[0][0].equals(player))&&(boardArjan[1][0].equals(player))&&(boardArjan[2][0].equals(player))) ||
//                    ((boardArjan[0][1].equals(player))&&(boardArjan[1][1].equals(player))&&(boardArjan[2][1].equals(player)))||
//                    ((boardArjan[0][2].equals(player))&&(boardArjan[1][2].equals(player))&&(boardArjan[2][2].equals(player)))||
//
//                    ((boardArjan[0][0].equals(player))&&(boardArjan[1][1].equals(player))&&(boardArjan[2][2].equals(player)))||
//                    ((boardArjan[0][2].equals(player))&&(boardArjan[1][1].equals(player))&&(boardArjan[2][0].equals(player)))){
//                running = false;
//                System.out.println(player + " wins!");
//            }
//        }
//
//    }


    public static void main(String[] args) {
        launch(args);
    }
}

