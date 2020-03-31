package TicTacToe;

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

public class Main extends Application {

    private static final String emptyId = "e";
    private static final String xId = "x";
    private static final String oId = "o";
    //board size x * x
    private static final int boardSize = 3;

    private Controller playField = new Controller(600, 600);
    private ArrayList<ArrayList<Button>> board = new ArrayList<>();

    private Image eBox = new Image(getClass().getResourceAsStream("/images.tictactoe/ttt_empty.png"));
    private Image oBox = new Image(getClass().getResourceAsStream("/images.tictactoe/o_tile.png"));
    private Image xBox = new Image(getClass().getResourceAsStream("/images.tictactoe/x_tile.png"));

    private  boolean isTurn = false; //if false -> X's turn, if true -> O's turn
    private Label labelTurn;

    String[][] boardArjan = {{"x", "x", "o"},
            {"o", "x", "x"},
            {"o", "o", "o"}};

    @Override
    public void start(Stage primaryStage){
        //primary stage
        primaryStage.setTitle("Tic Tac Toe");
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        primaryStage.setResizable(false);
        vbox.setBackground(new Background((new BackgroundFill(Color.web("cfcfcf"), null, null))));
        vbox.setPadding(new Insets(100));
        float extraWidth = (float)(vbox.getPadding().getLeft() + vbox.getPadding().getRight());
        float extraHeight = (float)(vbox.getPadding().getTop() + vbox.getPadding().getBottom());

        //in-game title
        Label title = new Label("Tic Tac Toe");
        title.setFont(new Font(40));
        vbox.getChildren().add(title);

        //Turn label
        labelTurn = new Label(isTurn ? "O" : "X" + "'s turn");
        labelTurn.setFont(new Font(30));
        vbox.getChildren().add(labelTurn);


        //Score labels
        HBox hbox = new HBox();
        Label xLabel = new Label("X:");
        Label oLabel = new Label("O:");
        Label xScoreLabel = new Label("5");
        Label oScoreLabel = new Label("3");
        xScoreLabel.setPadding(new Insets(0 ,100, 0, 0));
        hbox.getChildren().addAll(xLabel, xScoreLabel, oLabel, oScoreLabel);
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(hbox);

        //creating grid
        GridPane grid = new GridPane();
        vbox.getChildren().add(grid);

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

                btn.setMinSize(playField.x / (float)boardSize, playField.y / (float)boardSize);

                ImageView img = new ImageView(eBox);
                img.setFitWidth(playField.x / (float)boardSize);
                img.setFitHeight(playField.y / (float)boardSize);
                btn.setGraphic(img);

                btn.setOnAction(new ButtonHandler(x, y, this));

                grid.add(btn, x, y);
            }
        }

        primaryStage.setScene(new Scene(vbox, playField.x + extraWidth, playField.y + extraHeight));
        primaryStage.show();

    }

    private void onTileClick(int xPos, int yPos){
        Button current = board.get(yPos).get(xPos);
        String currentId = current.getId();

        if (!currentId.equals(emptyId))
            return;
        else {
            ImageView img = new ImageView(isTurn ? oBox : xBox);
            img.setFitWidth(playField.x / (float)boardSize);
            img.setFitHeight(playField.y / (float)boardSize);
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

