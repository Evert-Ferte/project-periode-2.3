import com.sun.javafx.geom.Vec2d;
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
    private int width = 400;
    private int height = 400;
    private int mapSize = 8;
    private ArrayList<ArrayList<Button>> map = new ArrayList<>();

    private Image tileEmpty = new Image(getClass().getResourceAsStream("tile_empty.png"));
    private Image tileWhite = new Image(getClass().getResourceAsStream("tile_white.png"));
    private Image tileBlack = new Image(getClass().getResourceAsStream("tile_black.png"));

    private boolean turn = true;

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
                map.get(y).get(x).setId("empty");

                btn.setMinSize((float)width / (float)mapSize, (float)height / (float)mapSize);

                ImageView img = new ImageView(tileEmpty);
                img.setFitWidth((float)width / (float)mapSize);
                img.setFitHeight((float)height / (float)mapSize);
                btn.setGraphic(img);

                btn.setOnAction(new ButtonHandler(x, y, this));

                grid.add(btn, y, x);
            }
        }

        clickPosition(3, 3, true);
        clickPosition(3, 4, true);
        clickPosition(4, 4, true);
        clickPosition(4, 3, true);

        Scene scene = new Scene(grid, width, height);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Reversi");
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
        if (currentId != "empty")
            return;

        // ...
        String tileId = turn ? "tileWhite" : "tileBlack";
        boolean valid = false;

        if (forceClick)
            valid = true;
        else {
            if (yPos + 1 < mapSize) {
                String topId = map.get(yPos + 1).get(xPos).getId();
                if (topId == "tileWhite" || topId == "tileBlack") valid = true;
            }
            if (yPos - 1 >= 0) {
                String botId = map.get(yPos - 1).get(xPos).getId();
                if (botId == "tileWhite" || botId == "tileBlack") valid = true;
            }
            if (xPos + 1 < mapSize) {
                String rightId = map.get(yPos).get(xPos + 1).getId();
                if (rightId == "tileWhite" || rightId == "tileBlack") valid = true;
            }
            if (xPos - 1 >= 0) {
                String leftId = map.get(yPos).get(xPos - 1).getId();
                if (leftId == "tileWhite" || leftId == "tileBlack") valid = true;
            }
        }

        if (!valid) { return; }

        // Place the new tile
        ImageView img2 = new ImageView(turn ? tileWhite : tileBlack);
        img2.setFitWidth((float)width / (float)mapSize);
        img2.setFitHeight((float)height / (float)mapSize);

        current.setGraphic(img2);
        current.setId(turn ? "tileWhite" : "tileBlack");

        if (!forceClick)
            checkMove(xPos, yPos);

        turn = !turn;
    }

    private void checkMove(int xPos, int yPos) {
        String tileId = !turn ? "tileWhite" : "tileBlack";

        // check directions (top, bot, left, right, diagonal) for black or white tile
            // skip if the direction is of the same color as the current color
        ArrayList<Vec2d> directions = new ArrayList<>();
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                // Skip the (0,0) because self + 0 does nothing
                if (x == 0 && y == 0) continue;

                // Check if the position is in the map
                if (yPos + y < 0 || yPos + y >= mapSize)
                    continue;
                if (xPos + x < 0 || xPos + x >= mapSize)
                    continue;

                if (map.get(yPos + y).get(xPos + x).getId() == tileId)
                    directions.add(new Vec2d(x, y));
            }
        }

        // for each direction
            // loop till end of map
        System.out.println("directions.size(): " + directions.size());
        for (int i = 0; i < directions.size(); i++) {
            boolean hasNext = true;
            Vec2d pos = new Vec2d(xPos + directions.get(i).x, yPos + directions.get(i).y);
            do {
                ImageView img2 = new ImageView(turn ? tileWhite : tileBlack);
                img2.setFitWidth((float)width / (float)mapSize);
                img2.setFitHeight((float)height / (float)mapSize);

                map.get((int)pos.y).get((int)pos.x).setGraphic(img2);

                // set hasNext
                pos = new Vec2d(pos.x + directions.get(i).x, pos.y + directions.get(i).y);

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
