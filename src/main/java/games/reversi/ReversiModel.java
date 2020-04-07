package games.reversi;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import network.Connection;
import network.Handler;
import network.Receiver;
import network.Sender;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReversiModel {
    private static final int mapSize = 8;
    
    private Vector2 fieldSize = new Vector2(640, 640);
    
    private int scoreWhite = 0;
    private int scoreBlack = 0;
    private boolean turn = false;
    
    private ReversiView view;
    
    private boolean isGameFinished = false;
    private boolean againstPlayer = true;
    
    // Networking
    private Connection connection;
    private static final String ip = "localhost";
    private static final int port = 7789;
    
    private Sender sender;
    private String name = "test-user";
    private Receiver receiver;
    
    private Handler handler;
    
    /**
     * The constructor. Sets the view reference.
     *
     * @param view The reference to a view, that is part of this MVC model.
     */
    public ReversiModel(ReversiView view) {
        this.view = view;
        
        connection = new Connection(ip, port);
    
        try {
            receiver = new Receiver(connection.getSocket());
            receiver.start();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) { }
        } catch (IOException ignored) { }
    
        sender = new Sender(connection.getSocket());
        sender.login(name);
        sender.getPlayerlist();

        handler = new Handler(this);
    
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    refreshPlayerList();
                });
            }
        }, 0, 2000);
    }
    
    /**
     * Resets the game variables.
     */
    public void reset() {
        scoreWhite = 0;
        scoreBlack = 0;
        turn = false;
    }
    
    /**
     * Place a black or white tile on the given position on the board.
     *
     * @param xPos X position on the board.
     * @param yPos Y position on the board.
     */
    public void clickPosition(int xPos, int yPos) {
        clickPosition(xPos, yPos, false);
    }
    
    /**
     *  Place a black or white tile on the given position on the board.
     *
     * @param xPos X position on the board.
     * @param yPos Y position on the board.
     * @param forceClick If true, place without checking if the spot is valid.
     */
    public void clickPosition(int xPos, int yPos, boolean forceClick) {
        // If we are not force clicking a tile, check if the tile is a valid tile to click
        if (!forceClick) {
            String tileId = view.getTileId(xPos, yPos);
            if (!tileId.equals(view.getEmptyId()))
                return;
        }
        
        boolean valid;
        // If we force click a tile, always make it a valid move
        if (forceClick)
            valid = true;
            // Else, check each direction (horizontal, vertical & diagonal) for possible moves
        else {
            valid = setTilesInDirection(xPos, yPos,  0, -1);              // Up
            valid = setTilesInDirection(xPos, yPos,  0,  1) || valid;     // Down
            valid = setTilesInDirection(xPos, yPos, -1,  0) || valid;     // Left
            valid = setTilesInDirection(xPos, yPos,  1,  0) || valid;     // Right
            
            valid = setTilesInDirection(xPos, yPos, -1, -1) || valid;     // Up left
            valid = setTilesInDirection(xPos, yPos,  1, -1) || valid;     // Up right
            valid = setTilesInDirection(xPos, yPos, -1,  1) || valid;     // Down left
            valid = setTilesInDirection(xPos, yPos,  1,  1) || valid;     // Down right
        }
        
        // If the position is valid, add score and update the interface
        if (valid) {
            // Add score to the correct player
            if (turn)
                addToScoreWhite();
            else
                addToScoreBlack();
            
            // Place the new tile
            view.updateTileGraphic(turn, xPos, yPos);
            
            // Send the move to the server
//            sender.sendMove();
        }
        
        // Swap the turn, only if this turn was valid
        turn = valid != turn;
        view.updateTurnLabel(turn);
    }
    
    /**
     * Check a direction from a given point, for possible valid moves that can be made.
     *
     * @param xPos The start X position.
     * @param yPos The start Y position.
     * @param xDir The X direction that will be checked.
     * @param yDir The Y direction that will be checked.
     * @return Returns if a valid move could be made it the given direction.
     */
    private boolean setTilesInDirection(int xPos, int yPos, int xDir, int yDir) {
        if (xDir == 0 && yDir == 0) return false;       // Return if no direction is given
        
        // Check if the direction is a valid direction
        boolean valid = isValidDirection(xPos, yPos, xDir, yDir);
        
        // If the clicked position is valid, switch the tiles around so that they belong to the current player
        if (valid) {
            String currentPlayerId  = view.getPlayerId(turn);
            
            int x = xPos + xDir;
            int y = yPos + yDir;
            
            int j = 0;
            // Loop for the amount of tiles checked if the previous loop (i)
            while (x > 0 && x < mapSize - 1 && y > 0 && y < mapSize - 1) {
                j++;
//            for (int j = 1; j < i; j++) {
                // Get the current x and y position
                /*int */x = xPos + xDir * j;
                /*int */y = yPos + yDir * j;
                
                String id = view.getTileId(x, y);
                
                // Keep looping while we haven't found the tile of the current player
                if (!id.equals(currentPlayerId)) {
                    // convert tile to the same tile as the current player
                    view.updateTileGraphic(turn, x, y);
                    
                    // Add score to the correct player, and remove score from the other player
                    if (turn) {
                        addToScoreWhite();
                        subtractFromScoreBlack();
                    }
                    else {
                        addToScoreBlack();
                        subtractFromScoreWhite();
                    }
                }
                // If a tile from the current player is found, stop looping
                else
                    break;
            }
        }
        
        // Return if a successful move was made in this direction
        return valid;
    }
    
    private boolean isValidDirection(int xPos, int yPos, int xDir, int yDir) {
        boolean valid = false, oppositeFound = false;
        String currentPlayerId  = view.getPlayerId(turn);
        String oppositePlayerId = view.getPlayerId(!turn);
    
        int newX = xPos + xDir;
        int newY = yPos + yDir;
    
        // Loop while the new position is still a valid position on the board
        int i = 0;
        while (newX > 0 && newX < mapSize - 1 && newY > 0 && newY < mapSize - 1) {
            i++;
        
            // Set the new position to 1 tile further in the given direction
            newX = xPos + xDir * i;
            newY = yPos + yDir * i;
        
            // Get the ID of the current tile
            String id = view.getTileId(newX, newY);
        
            // If the current tile is empty, stop the loop
            if (id.equals(view.getEmptyId())) break;
        
            // If the current tile is of the opposite player, continue (to the next loop)
            if (id.equals(oppositePlayerId)) {
                oppositeFound = true;
                continue;
            }
        
            // If the current tile is of the player, break and make valid only if an opposite tile was found
            if (id.equals(currentPlayerId)) {
                if (oppositeFound)
                    valid = true;
                break;
            }
        }
        
        return valid;
    }
    
    private boolean isValidTile(int xPos, int yPos) {
        boolean valid;
        valid = isValidDirection(xPos, yPos,  0, -1);              // Up
        valid = isValidDirection(xPos, yPos,  0,  1) || valid;     // Down
        valid = isValidDirection(xPos, yPos, -1,  0) || valid;     // Left
        valid = isValidDirection(xPos, yPos,  1,  0) || valid;     // Right
    
        valid = isValidDirection(xPos, yPos, -1, -1) || valid;     // Up left
        valid = isValidDirection(xPos, yPos,  1, -1) || valid;     // Up right
        valid = isValidDirection(xPos, yPos, -1,  1) || valid;     // Down left
        valid = isValidDirection(xPos, yPos,  1,  1) || valid;     // Down right
        
        return valid;
    }
    
    private void addToScoreWhite() { addToScoreWhite(1); }
    private void addToScoreWhite(int amount) {
        scoreWhite += amount;
        view.updateScoreLabel(scoreWhite, scoreBlack);
        checkWinCondition();
    }
    
    private void subtractFromScoreWhite() { subtractFromScoreWhite(1); }
    private void subtractFromScoreWhite(int amount) {
        scoreWhite -= amount;
        view.updateScoreLabel(scoreWhite, scoreBlack);
        checkWinCondition();
    }
    
    private void addToScoreBlack() { addToScoreBlack(1); }
    private void addToScoreBlack(int amount) {
        scoreBlack += amount;
        view.updateScoreLabel(scoreWhite, scoreBlack);
        checkWinCondition();
    }
    
    private void subtractFromScoreBlack() { subtractFromScoreBlack(1); }
    private void subtractFromScoreBlack(int amount) {
        scoreBlack -= amount;
        view.updateScoreLabel(scoreWhite, scoreBlack);
        checkWinCondition();
    }
    
    private void checkWinCondition() {
        isGameFinished = scoreWhite + scoreBlack == mapSize * mapSize;
    }
    
    public boolean isGameFinished() { return isGameFinished; }
    
    public int getMapSize() { return mapSize; }
    public boolean getTurn() { return turn; }
    public Vector2 getFieldSize() { return fieldSize; }
    public int getScoreWhite() { return scoreWhite; }
    public int getScoreBlack() { return scoreBlack; }
    
    public Vector2[] getAvailablePositions() {
        ArrayList<Vector2> pos = new ArrayList<>();
        String emptyId = view.getEmptyId();
        
        // Loop through the map/board
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                // Get the current tile id
                String tileId = view.getTileId(x, y);
                
                // If the tile is empty, check if the current player can place a valid tile here
                if (tileId.equals(emptyId))
                    if (isValidTile(x, y))
                        pos.add(new Vector2(x, y));
            }
        }
        
        return pos.toArray(new Vector2[0]);
    }
    
    public String[] getPlayerList() {
        sender.getPlayerlist();
        //TODO improve -> sender.getPlayerList is not instant (takes a few ms)
        String[] allPlayers = Handler.playerlist == null ? new String[0] : Handler.playerlist;
        ArrayList<String> players = new ArrayList<>();
        
        for (String p : allPlayers) {
            if (!p.equals(name))
                players.add(p);
        }
        
        return players.toArray(new String[0]);
    }
    
    public void challengePlayer(Button btn) {
        boolean challenged = true;
        String id = null;
        
        try {
            int i = Integer.parseInt(btn.getId());
            id = btn.getId();
            challenged = false;
        }
        catch (NumberFormatException ignored) { }
        
        if (challenged) {
            System.out.println("challenging player:");
            System.out.println("player: " + btn.getId());
            sender.challenge(btn.getId(), "Reversi");
        }
        else {
            System.out.println("accept challenge");
            sender.acceptAChallenge(id);
        }
    }
    
    public void refreshPlayerList() {
        view.refreshPlayerList(getPlayerList());
    }
    
    /**
     * Accept a challenge from someon else.
     * @param nr Challenge number.
     */
    public void acceptChallenge(String nr) {
        System.out.println("challenge " + nr + " accepted");
        sender.acceptAChallenge(nr);
        
        // TODO - call method start new game
    }
    
    //TEMP
    public void setName(String name) { this.name = name; }
    
    public void challengeReceived(String challenger, String nr) {
        view.challengeReceived(challenger, nr);
    }
    
    public void startMatch() {
        view.startMatch();
    }
    
    public void setAgainstPlayer(boolean againstPlayer) { this.againstPlayer = againstPlayer; }
    public boolean isAgainstPlayer() { return againstPlayer; }
}
