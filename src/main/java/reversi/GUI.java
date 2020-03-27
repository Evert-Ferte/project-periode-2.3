package reversi;

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

public class GUI extends Application {
    private static final String emptyId = "e";
    private static final String blackId = "b";
    private static final String whiteId = "w";
    private static final int mapSize = 8;
    
    private Vector2 fieldSize = new Vector2(640, 640);
    private ArrayList<ArrayList<Button>> map = new ArrayList<>();

    private Image tileEmpty = new Image(getClass().getResourceAsStream("/images/reversi/tile_empty_fade.png"));
    private Image tileWhite = new Image(getClass().getResourceAsStream("/images/reversi/tile_white_0.png"));
    private Image tileBlack = new Image(getClass().getResourceAsStream("/images/reversi/tile_black_0.png"));

    private boolean turn = false;
    private Label turnLabel;

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = new VBox();
        vBox.setBackground(new Background(new BackgroundFill(Color.web("#005a00"), null, null)));
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(20);
        
        float extraWidth = (float)(vBox.getPadding().getLeft() + vBox.getPadding().getRight());
        float extraHeight = (float)(vBox.getPadding().getTop() + vBox.getPadding().getBottom());// + vBox.getSpacing());
        
//        Button backButton = new Button("Exit");
//        backButton.setMinSize(120, 40);
//        vBox.getChildren().add(backButton);
        
        turnLabel = new Label((turn ? "White" : "Black") + "'s turn");
        turnLabel.setAlignment(Pos.CENTER);
        turnLabel.setMinSize(fieldSize.x * 0.8f, 60);
        turnLabel.setFont(new Font(30));
        turnLabel.setBackground(new Background(new BackgroundFill(Color.web("005200"), null, null)));
        turnLabel.setTextFill(Color.web(turn ? "#ffffff" : "000000"));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(turnLabel);
        
        HBox hBox = new HBox();
        Label labelWhite = new Label("White");
        Label labelBlack = new Label("Black");
        Label colon = new Label(":");
        Label labelScoreWhite = new Label("99");
        Label labelScoreBlack = new Label("99");
        labelScoreWhite.setMinSize(40, 20);
        labelScoreWhite.setAlignment(Pos.CENTER);
        labelScoreWhite.setTextFill(Color.web("#ffffff"));
        labelWhite.setTextFill(Color.web("#ffffff"));
        labelScoreWhite.setBackground(new Background(new BackgroundFill(Color.web("005200"), null, null)));
        labelScoreBlack.setMinSize(40, 20);
        labelScoreBlack.setAlignment(Pos.CENTER);
        labelScoreBlack.setTextFill(Color.web("#000000"));
        labelBlack.setTextFill(Color.web("#000000"));
        labelScoreBlack.setBackground(new Background(new BackgroundFill(Color.web("005200"), null, null)));
        
        hBox.getChildren().addAll(labelWhite, labelScoreWhite, colon, labelScoreBlack, labelBlack);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(hBox);
        
        extraHeight += /*backButton.getMinHeight() +*/ turnLabel.getMinHeight() + vBox.getSpacing() /* * 2*/;
        
        GridPane grid = new GridPane();
        vBox.getChildren().add(grid);

        for (int y = 0; y < mapSize; y++) {
            map.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                Button btn = new Button();
                map.get(y).add(btn);
                map.get(y).get(x).setId(emptyId);

                btn.setMinSize(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);

                ImageView img = new ImageView(tileEmpty);
                img.setFitWidth(fieldSize.x / (float)mapSize);
                img.setFitHeight(fieldSize.y / (float)mapSize);
                btn.setGraphic(img);

                btn.setOnAction(new ButtonHandler(x, y, this));

                grid.add(btn, x, y);
            }
        }
        
        clickPosition(3, 3, true);
        clickPosition(3, 4, true);
        clickPosition(4, 4, true);
        clickPosition(4, 3, true);
    
        System.out.println(turn ? "white's turn" : "black's turn");
        
        //TEMP
        //TODO fix bug here - upper right tile can never be clicked
//        clickPosition(5, 0, true);
//        clickPosition(6, 0, true);
//
//        clickPosition(6, 1, true);
//
//        clickPosition(7, 1, true);
//        clickPosition(7, 2, true);
//        turn = !turn;
        //TEMP

        Scene scene = new Scene(vBox, fieldSize.x + extraWidth, fieldSize.y + extraHeight);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Reversi");
//        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void clickPosition(int xPos, int yPos) {
        clickPosition(xPos, yPos, false);
    }

    private void clickPosition(int xPos, int yPos, boolean forceClick) {
        // Check if we place the new tile to an existing tile
        Button current = map.get(yPos).get(xPos);
        String currentId = current.getId();

        // Return if the current tile is not empty
        if (!currentId.equals(emptyId))
            return;

        // ...
        boolean valid = false;
        
        if (forceClick)
            valid = true;
        else {
            valid = checkDirection(xPos, yPos,  0, -1);              // Up
            valid = checkDirection(xPos, yPos,  0,  1) || valid;     // Down
            valid = checkDirection(xPos, yPos, -1,  0) || valid;     // Left
            valid = checkDirection(xPos, yPos,  1,  0) || valid;     // Right
            
            valid = checkDirection(xPos, yPos, -1, -1) || valid;     // Up left
            valid = checkDirection(xPos, yPos,  1, -1) || valid;     // Up right
            valid = checkDirection(xPos, yPos, -1,  1) || valid;     // Down left
            valid = checkDirection(xPos, yPos,  1,  1) || valid;     // Down right
        }
        
        if (valid) {
            // Place the new tile
            ImageView img = new ImageView(turn ? tileWhite : tileBlack);
            img.setFitWidth(fieldSize.x / (float)mapSize);
            img.setFitHeight(fieldSize.y / (float)mapSize);
            current.setGraphic(img);
            current.setId(turn ? whiteId : blackId);
        }
        
        // Swap the turn, only if this turn was valid
        turn = valid != turn;
        swapTurn();
    }
    
    private boolean checkDirection(int xPos, int yPos, int xDir, int yDir) {
        if (xDir == 0 && yDir == 0) return false;
        
        boolean valid = false, oppositeFound = false;
        String currentPlayerId  = turn ? whiteId : blackId;
        String oppositePlayerId = turn ? blackId : whiteId;
        
        int newX = xPos + xDir;
        int newY = yPos + yDir;
        
        // Loop while the new position is still a valid position in the map
        int i = 0;
        while (newX > 0 && newX < mapSize - 1 && newY > 0 && newY < mapSize - 1) {
            i++;
            
            newX = xPos + xDir * i;
            newY = yPos + yDir * i;
            
            // Get the ID of the current tile
            String id = map.get(newY).get(newX).getId();
            
            // If empty, stop
            if (id.equals(emptyId)) break;
    
            // If opposite player, continue
            if (id.equals(oppositePlayerId)) {
                oppositeFound = true;
                continue;
            }
    
            // If current player, break and make valid only if an opposite tile was found
            if (id.equals(currentPlayerId)) {
                if (oppositeFound)
                    valid = true;
                break;
            }
        }
    
        if (valid) {
            for (int j = 1; j < i; j++) {
                int x = xPos + xDir * j;
                int y = yPos + yDir * j;
                
                String id = map.get(y).get(x).getId();
                
                if (!id.equals(currentPlayerId)) {
                    // convert tile to the same tile as the current player
                    ImageView img = new ImageView(turn ? tileWhite : tileBlack);
                    img.setFitWidth(fieldSize.x / (float)mapSize);
                    img.setFitHeight(fieldSize.y / (float)mapSize);
                    map.get(y).get(x).setGraphic(img);
                    map.get(y).get(x).setId(currentPlayerId);
                }
                else
                    break;
            }
        }
        
        return valid;
    }
    
    private void swapTurn () {
        // TODO - also switch turn boolean value here...
        
        turnLabel.setText((turn ? "White" : "Black") + "'s turn");
        turnLabel.setTextFill(Color.web(turn ? "#ffffff" : "000000"));
    }
    
    class ButtonHandler implements EventHandler<ActionEvent> {
        private int xPos;
        private int yPos;
        private GUI gui;

        public ButtonHandler(int xPos, int yPos, GUI gui) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.gui = gui;
        }

        @Override
        public void handle(ActionEvent event) {
            gui.clickPosition(xPos, yPos);
        }
    }
}
