package reversi;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GUI extends Application {
    private static final String emptyId = "e";
    private static final String blackId = "b";
    private static final String whiteId = "w";
    private static final int mapSize = 8;
    
    private int width = 800;
    private int height = 800;
    private ArrayList<ArrayList<Button>> map = new ArrayList<>();

    private Image tileEmpty = new Image(getClass().getResourceAsStream("/images/reversi/tile_empty_fade.png"));
    private Image tileWhite = new Image(getClass().getResourceAsStream("/images/reversi/tile_white_0.png"));
    private Image tileBlack = new Image(getClass().getResourceAsStream("/images/reversi/tile_black_0.png"));

    private boolean turn = false;

    public GUI() {
        // ...
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();

        for (int y = 0; y < mapSize; y++) {
            map.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                Button btn = new Button();
                map.get(y).add(btn);
                map.get(y).get(x).setId(emptyId);

                btn.setMinSize((float)width / (float)mapSize, (float)height / (float)mapSize);

                ImageView img = new ImageView(tileEmpty);
                img.setFitWidth((float)width / (float)mapSize);
                img.setFitHeight((float)height / (float)mapSize);
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
        
//        //TEMP
//        clickPosition(5, 0, true);
//        clickPosition(6, 0, true);
//
//        clickPosition(6, 1, true);
//
//        clickPosition(7, 1, true);
//        clickPosition(7, 2, true);
//        turn = !turn;
        //TEMP

        Scene scene = new Scene(grid, width, height);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Reversi");
        primaryStage.show();
    }

    public void clickPosition(int xPos, int yPos) {
        clickPosition(xPos, yPos, false);
    }

    private void clickPosition(int xPos, int yPos, boolean forceClick) {
//        if (!forceClick) System.out.println("click("+xPos+", "+yPos+")");
        
        // Check if we place the new tile to an existing tile
        Button current = map.get(yPos).get(xPos);
        String currentId = current.getId();

        // Return if the current tile is not empty
        if (!currentId.equals(emptyId))
            return;

        // ...
        boolean valid = false;
        
        if (forceClick) {
            // Place the new tile
            ImageView img = new ImageView(turn ? tileWhite : tileBlack);
            img.setFitWidth((float)width / (float)mapSize);
            img.setFitHeight((float)height / (float)mapSize);
    
            current.setGraphic(img);
            current.setId(turn ? whiteId : blackId);
            
            valid = true;
        }
        else {
            valid = checkDirection(xPos, yPos,  0, -1);              // Up
            valid = checkDirection(xPos, yPos,  0,  1) || valid;     // Down
            valid = checkDirection(xPos, yPos, -1,  0) || valid;     // Left
            valid = checkDirection(xPos, yPos,  1,  0) || valid;     // Right
            
            valid = checkDirection(xPos, yPos, -1, -1) || valid;     // Up left
            valid = checkDirection(xPos, yPos,  1, -1) || valid;     // Up right
            valid = checkDirection(xPos, yPos, -1,  1) || valid;     // Down left
            valid = checkDirection(xPos, yPos,  1,  1) || valid;     // Down right
            
            if (valid) {
                // Place the new tile
                ImageView img = new ImageView(turn ? tileWhite : tileBlack);
                img.setFitWidth((float)width / (float)mapSize);
                img.setFitHeight((float)height / (float)mapSize);
        
                current.setGraphic(img);
                current.setId(turn ? whiteId : blackId);
            }
        }
        
        // Swap the turn, only if this turn was valid
        turn = valid != turn;
    
        if (!forceClick) System.out.println(turn ? "white's turn" : "black's turn");
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
//        while (newX >= 0 && newX < mapSize && newY >= 0 && newY < mapSize) {
            i++;
            
            newX = xPos + xDir * i;
            newY = yPos + yDir * i;
            
            // Get the ID of the current tile
            String id = map.get(newY).get(newX).getId();
            
//            System.out.println("checking pos (" + newX + ", " + newY + ")");
    
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
    
//        System.out.println(valid ? "valid" : "not valid");
    
        if (valid) {
            System.out.println("valid found, replacing adjacent tiles ("+i+" tile(s) )");
            for (int j = 1; j < i; j++) {
                int x = xPos + xDir * j;
                int y = yPos + yDir * j;
                
                String id = map.get(y).get(x).getId();
                System.out.println("replacing ("+x+", "+y+") (id: "+id+")");
                
                if (!id.equals(currentPlayerId)) {
                    // convert tile to the same tile as the current player
                    ImageView img = new ImageView(turn ? tileWhite : tileBlack);
                    img.setFitWidth((float)width / (float)mapSize);
                    img.setFitHeight((float)height / (float)mapSize);
                    map.get(y).get(x).setGraphic(img);
                    map.get(y).get(x).setId(currentPlayerId);
                }
                else
                    break;
            }
        }
        
        return valid;
    }

    private void checkMove(int xPos, int yPos) {
        String tileId = !turn ? whiteId : blackId;

        // check directions (top, bot, left, right, diagonal) for black or white tile
            // skip if the direction is of the same color as the current color
        ArrayList<Vector2> directions = new ArrayList<>();
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                // Skip the (0,0) because self + 0 does nothing
                if (x == 0 && y == 0) continue;

                // Check if the position is in the map
                if (yPos + y < 0 || yPos + y >= mapSize)
                    continue;
                if (xPos + x < 0 || xPos + x >= mapSize)
                    continue;

                if (map.get(yPos + y).get(xPos + x).getId().equals(tileId))
                    directions.add(new Vector2(x, y));
            }
        }

        // for each direction
            // loop till end of map
        System.out.println("directions.size(): " + directions.size());
        for (int i = 0; i < directions.size(); i++) {
            boolean hasNext = true;
            Vector2 pos = new Vector2(xPos + directions.get(i).x, yPos + directions.get(i).y);
            do {
                ImageView img2 = new ImageView(turn ? tileWhite : tileBlack);
                img2.setFitWidth((float)width / (float)mapSize);
                img2.setFitHeight((float)height / (float)mapSize);

                map.get((int)pos.y).get((int)pos.x).setGraphic(img2);

                // set hasNext
                pos = new Vector2(pos.x + directions.get(i).x, pos.y + directions.get(i).y);

                // Check if the position is in the map
                if (pos.y < 0 || pos.y >= mapSize)
                    hasNext = false;
                if (pos.x < 0 || pos.x >= mapSize)
                    hasNext = false;
            }
            while (hasNext);

            // Go to that direction and loop until it cant go further
        }
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
