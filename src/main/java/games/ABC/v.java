package games.ABC;

import games.Game;
import games.reversi.Vector2;
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

//TODO - cannot make a match on the edge
public class v extends Game {
    private static final String emptyId = "e";
    private static final String blackId = "b";
    private static final String whiteId = "w";
    
    private m model;
    
    private Image tileEmpty = new Image(getClass().getResourceAsStream("/images/reversi/tile_empty_fade.png"));
    private Image tileWhite = new Image(getClass().getResourceAsStream("/images/reversi/tile_white_0.png"));
    private Image tileBlack = new Image(getClass().getResourceAsStream("/images/reversi/tile_black_0.png"));
    
    private ArrayList<ArrayList<Button>> map = new ArrayList<>();
    
    private Label turnLabel;
    private Label scoreWhiteLabel;
    private Label scoreBlackLabel;
    
    private Vector2 imgSize;
    
    /**
     * The constructor. Creates a new model for this view.
     */
    public v() {
        this.model = new m(this);
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
        map = new ArrayList<>();
        model.reset();
    }
    
    /**
     * This function is called once on the start of the application.
     *
     * @param stage DO NOT USE, USE 'this.stage'.
     */
    @Override
    public void start(Stage stage) {
        // Get a few values from the model
        boolean turn = model.getTurn();
        int mapSize = model.getMapSize();
        Vector2 fieldSize = model.getFieldSize();
        
        // Set the image size that all tiles/buttons on the board will use
        imgSize = new Vector2(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);
        
        // Create a vertical box that will hold all UI components
        VBox vBox = new VBox();
        vBox.setBackground(new Background(new BackgroundFill(Color.web("#005a00"), null, null)));
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(20);
        
        float extraWidth = (float)(vBox.getPadding().getLeft() + vBox.getPadding().getRight());
        float extraHeight = (float)(vBox.getPadding().getTop() + vBox.getPadding().getBottom());// + vBox.getSpacing());
        
//        Button backButton = new Button("Exit");
//        backButton.setMinSize(120, 40);
        
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
        
        extraHeight += /*backButton.getMinHeight() +*/ turnLabel.getMinHeight() + hBox.getHeight() + vBox.getSpacing() * 2;
        
        // Create a grid that will hold all game tile buttons
        GridPane grid = new GridPane();
        vBox.getChildren().add(grid);
        
        // Loop through the board
        for (int y = 0; y < mapSize; y++) {
            map.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                // Create a new button for this spot on the board
                Button btn = new Button();
                btn.setMinSize(fieldSize.x / (float)mapSize, fieldSize.y / (float)mapSize);
                map.get(y).add(btn);
                map.get(y).get(x).setId(emptyId);
                
                // Create an image that we will put over the button
                ImageView img = new ImageView(tileEmpty);
                img.setFitWidth(fieldSize.x / (float)mapSize);
                img.setFitHeight(fieldSize.y / (float)mapSize);
                btn.setGraphic(img);
                
                // Set the event handler for the button
                btn.setOnAction(new c.ButtonHandler(x, y, model));
            
                grid.add(btn, x, y);
            }
        }
        
        // Create the standard 4 tiles that make up the default board layout
        model.clickPosition(3, 3, true);
        model.clickPosition(3, 4, true);
        model.clickPosition(4, 4, true);
        model.clickPosition(4, 3, true);
        
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
        
        Scene scene = new Scene(vBox, fieldSize.x + extraWidth, fieldSize.y + extraHeight);
    
        stage.setScene(scene);
        stage.setTitle("Reversi");
        stage.setResizable(false);
        stage.show();
    }
    
    /**
     * Update the black and white score labels.
     *
     * @param scoreWhite The new score of white that will be shown.
     * @param scoreBlack The new score of black that will be shown.
     */
    public void updateScoreLabel(int scoreWhite, int scoreBlack) {
        scoreBlackLabel.setText(String.valueOf(scoreBlack));
        scoreWhiteLabel.setText(String.valueOf(scoreWhite));
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
     * @param turn Boolean value that represents whether it is black of white's turn.
     * @param xPos The X position on the board.
     * @param yPos The Y position on the board.
     */
    public void updateTileGraphic(boolean turn, int xPos, int yPos) {
        ImageView img = new ImageView(turn ? tileWhite : tileBlack);
        img.setFitWidth(imgSize.x);
        img.setFitHeight(imgSize.y);
        
        Button current = map.get(yPos).get(xPos);
        current.setGraphic(img);
        current.setId(turn ? whiteId : blackId);
    }
    
    public String getTileId(int x, int y) { return map.get(y).get(x).getId(); }
    public String getPlayerId(boolean turn) { return turn ? whiteId : blackId; }
    public String getEmptyId() { return emptyId; }
}
