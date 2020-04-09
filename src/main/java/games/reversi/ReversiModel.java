package games.reversi;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import network.Connection;
import network.Handler;
import network.Receiver;
import network.Sender;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ReversiModel implements Cloneable{
    private static final int mapSize = 8;
    private static final Vector2 fieldSize = new Vector2(640, 640);
    
    private static final String ai = "random";
    private static final int minAiMoveDelay = 180;
    private static final int maxAiMoveDelay = 800;
    
    private int scoreWhite = 0;
    private int scoreBlack = 0;
    private boolean whiteTurn = false;
    private boolean playerTurn = false;
    
    private ReversiView view;
    
    private GameMode gameMode = GameMode.PLAYER_VS_PLAYER;
    public enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI,
        ONLINE
    }
    
    // Networking
    private Connection connection;
    private static final String ip = "145.33.225.170"; //"localhost";
    private static final int port = 7789;
    
    private Sender sender;
    private String name = /*"challenge me pls";*/ "groep2d5";
    
    /**
     * The constructor. Sets the view reference.
     *
     * @param view The reference to a view, that is part of this MVC model.
     */
    public ReversiModel(ReversiView view) {
        this.view = view;
    }
    
    //region class initialization functions
    
    /**
     * Called when clicking on a game tile.
     */
    public void startApplication() {
        createConnection();
    }
    
    /**
     * Creates a connection with the game server.
     */
    private void createConnection() {
        connection = new Connection(ip, port);
    
        if (connection.isConnected()) {
            try {
                Receiver receiver = new Receiver(connection.getSocket());
                receiver.start();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ignored) { }
            } catch (IOException ignored) { }
        
            sender = new Sender(connection.getSocket());
            sender.login(name);
            sender.getPlayerlist();
        
            Handler handler = new Handler(this);
        }
    }
    
    //endregion
    
    
    public void gameStart(GameMode mode) {
        resetVariables();        // TODO - old method, check and renew!
        
        setGameMode(mode);
        placeStartingTiles();
        view.startMatch();
        
        // If not online, assign the player to the black tiles
        if (mode != GameMode.ONLINE)
            setPlayerToWhite(false);
        else
            setPlayerTurn(false);
        
        // TODO - turn should always be false at start, but set isPlayerTurn is player can go first
        //  (online=variable, ai=alwaysStart, pvp=?)
    }
    public void gameEnd(boolean won) {
        // TODO - do general game end stuff here, and call onGameWon() or onGameLost()
        
        if (won) onGameWon();
        else     onGameLost();
    }
    private void onGameWon() {
        System.out.println("game won");
        // TODO - specific game won stuff here
    }
    private void onGameLost() {
        // TODO - specific game lost stuff here
        System.out.println("game lost");
    }
    public void forfeitGame() {
        if (gameMode == GameMode.ONLINE)
            sender.forfeitAGame();
        else
            gameEnd(false);
    }
    
    /**
     * Resets the game variables.
     */
    public void resetVariables() {
        setScoreWhite(0);
        setScoreBlack(0);
//        setOnlineMatch(false);
        setWhiteTurn(false);
        setPlayerTurn(true);
    }
    
    //region Turn handling functions
    
    private void turnHandler() {
        turnEnd();
        
        if (gameMode == GameMode.PLAYER_VS_AI)
            if (!isPlayerTurn())
                AiMove();
    
        System.out.println("next turn");
        System.out.println("isPlayerTurn: " + isPlayerTurn());
    }
    private void turnStart() {
    
    }
    private void turnEnd() {
        // Update the view on the end of each turn
        boolean gameFinished = isGameFinished();
    
        switchTurn();
        
        updateView();
    }
    
    private void switchTurn() {
        setWhiteTurn(!isWhiteTurn());
        setPlayerTurn(!isPlayerTurn());
    }
    private void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }
    
    //endregion
    
    /**
     * Updates all UI components.
     */
    private void updateView() { view.update(); }
    
    
    
    
    //region Functions for placing and flipping tiles
    
    /**
     * Places a tile on the board (if given a valid position), and flips tiles in trapped by the
     * current active player.
     *
     * @param x X position where the tile will be placed.
     * @param y Y position where the tile will be placed.
     */
    public void clickPositionNew(int x, int y) {
//        if (isGameFinished()) return;
        
        // Get and check for possible tiles that can be flipped at the clicked position
        ArrayList<Vector2> tilesToFlip = getTilesToFlip(x, y);
        boolean validClick = tilesToFlip.size() > 0;
        
        // Return if the click was not on a valid position
        if (!validClick) return;
        
        // Flip all tiles that need to be flipped
        flipTiles(tilesToFlip);
        
        // Also flip the clicked tile
        ArrayList<Vector2> clickedPosition = new ArrayList<>();
        clickedPosition.add(new Vector2(x, y));
        flipTiles(clickedPosition, false);
        if (isWhiteTurn())  addScoreToWhite();
        else                addScoreToBlack();
        
        // Send the move to the server (only if this command came from our client (it is our turn) )
        if (isPlayerTurn())
            sender.sendMove(convertPositionToIndex(x, y));
        
        turnHandler();
    }
    
    /**
     * Places the tiles that form the starting setup.
     */
    public void placeStartingTiles() {
        view.resetTiles();
        
        ArrayList<Vector2> tilesToFlip = new ArrayList<>();
        
//        setTurn(false); //TODO for if we want a standard layout for the starting tiles, otherwise remove this.
        
        // Place the first 2 white/black tiles
        tilesToFlip.add(new Vector2(3, 4));
        tilesToFlip.add(new Vector2(4, 3));
        flipTiles(tilesToFlip, false);
        
        // Switch turns so we can place the other color
        switchTurn();
    
        // Place the second 2 black/white tiles
        tilesToFlip.set(0, new Vector2(3, 3));
        tilesToFlip.set(1, new Vector2(4, 4));
        flipTiles(tilesToFlip, false);
        
        // Switch turns again so we end up where we were
        switchTurn();
        
        // Add 2 points to both black and white
        addScoreToBlack(2);
        addScoreToWhite(2);
        
        updateView();
    }
    
    /**
     * Flip all tiles from the list to the color of the current active player. Also add score to the current active
     * player, and remove score from the waiting player.
     *
     * @param tiles Tiles that will be flipped.
     */
    private void flipTiles(ArrayList<Vector2> tiles) { flipTiles(tiles, true); }
    /**
     * Flip all tiles from the list to the color of the current active player. You have the option to add/remove
     * score or not.
     *
     * @param tiles Tiles that will be flipped.
     * @param addScore Should score be added while flipping the tiles.
     */
    private void flipTiles(ArrayList<Vector2> tiles, boolean addScore) {
        if (tiles.size() <= 0) return;
    
        // Flip all tiles in the list
        for (Vector2 tile : tiles) {
            view.updateTileGraphic(isWhiteTurn(), (int)tile.x, (int)tile.y);
            
            // Skip adding score, if we don't need to
            if (!addScore) continue;
            
            // Add score to the correct player, and remove score from the other player
            if (isWhiteTurn()) {
                addScoreToWhite();
                subtractScoreFromBlack();
            }
            else {
                addScoreToBlack();
                subtractScoreFromWhite();
            }
        }
    }
    
    /**
     * Search for tiles that can be flipped from the given point. Returns a list of vectors which contain all tiles
     * that can be flipped.
     *
     * @param x X position to look from.
     * @param y Y position to look from.
     * @return Returns a list of vectors containing all tiles that can be flipped.
     */
    private ArrayList<Vector2> getTilesToFlip(int x, int y) {
        // Check if the clicked position is empty
        if (!view.getTileId(x, y).equals(view.getEmptyId())) return new ArrayList<>();
        
        // Check all directions for tiles that can be flipped
        ArrayList<Vector2> tilesUp       = getTilesInDirection(x, y,  0, -1);
        ArrayList<Vector2> tilesDown     = getTilesInDirection(x, y,  0,  1);
        ArrayList<Vector2> tilesLeft     = getTilesInDirection(x, y, -1,  0);
        ArrayList<Vector2> tilesRight    = getTilesInDirection(x, y,  1,  0);
    
        ArrayList<Vector2> tilesUpLeft   = getTilesInDirection(x, y, -1, -1);
        ArrayList<Vector2> tilesUpRight  = getTilesInDirection(x, y,  1, -1);
        ArrayList<Vector2> tilesDownLeft = getTilesInDirection(x, y, -1,  1);
        ArrayList<Vector2> tilesDownRight= getTilesInDirection(x, y,  1,  1);
        
        // Merge all directions to 1 list
        ArrayList<Vector2> r = new ArrayList<>();
        r.addAll(tilesUp);
        r.addAll(tilesDown);
        r.addAll(tilesLeft);
        r.addAll(tilesRight);
        r.addAll(tilesUpLeft);
        r.addAll(tilesUpRight);
        r.addAll(tilesDownLeft);
        r.addAll(tilesDownRight);
        
        return r;       // Return the merged list
    }
    
    /**
     * Gets a list of positions that can be flipped from a specific point to a given direction.
     *
     * @param x X start position.
     * @param y Y start position.
     * @param xDir X direction that will be searched.
     * @param yDir Y direction that will be searched.
     * @return Returns a list of positions.
     */
    private ArrayList<Vector2> getTilesInDirection(int x, int y, int xDir, int yDir) {
        String oppositePlayerId = view.getPlayerId(!whiteTurn);
        String playerId         = view.getPlayerId(whiteTurn);
        boolean valid = false;
        ArrayList<Vector2> tilesInDirection = new ArrayList<>();
        
        int newX = x + xDir;
        int newY = y + yDir;
        
        // Loop while the new position is still a valid position on the board
        int i = 1;
        while (isPositionInBoard(newX, newY)) {
            // Get the ID of the current tile
            String id = view.getTileId(newX, newY);
            
            // tile = opposite                  add to tiles
            if (id.equals(oppositePlayerId))    tilesInDirection.add(new Vector2(newX, newY));
            // tile = player                    valid and stop
            else if (id.equals(playerId))       { valid = true; break; }
            // tile = empty                     stop
            else                                break;
    
            // Set the new position to 1 tile further in the given direction
            i++;
            newX = x + xDir * i;
            newY = y + yDir * i;
        }
        
        // Only return the tiles if the player encloses the other players' tiles
        return valid ? tilesInDirection : new ArrayList<>();
    }
    
    //endregion
    
    /**
     * Returns true of the given position is in the board, and false if not.
     *
     * @param x The X position.
     * @param y The Y position
     * @return Returns if the given position is in the board.
     */
    private boolean isPositionInBoard(int x, int y) { return (x >= 0 && x < mapSize) && (y >= 0 && y < mapSize); }
    
    private void setGameMode(GameMode mode) {
        gameMode = mode;
    }
    
    public Vector2[] getAvailablePositions() {
        ArrayList<Vector2> pos = new ArrayList<>();
        String emptyId = view.getEmptyId();
        
        // Loop through the map/board
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                // Get the current tile id
                String tileId = view.getTileId(x, y);
                
                // If the tile is empty, check if the current player can place a valid tile here
                if (tileId.equals(emptyId)) {
//                    if (isValidTile(x, y))
//                        pos.add(new Vector2(x, y));
                    // TODO - sometimes it cannot find tiles on the first move of the game (fix this)
                    ArrayList<Vector2> p = getTilesToFlip(x, y);
                    
                    if (p.size() > 0)
                        pos.add(new Vector2(x, y));
                }
            }
        }
        
        return pos.toArray(new Vector2[0]);
    }
    
    // GETTERS AND SETTERS BELOW
    
    //region Getters and setters for the score (both black and white score)
    
    private void setScoreBlack(int score) { this.scoreBlack = score; }
    private void setScoreWhite(int score) { this.scoreWhite = score; }
    
    public int getScoreBlack() { return scoreBlack; }
    public int getScoreWhite() { return scoreWhite; }
    
    private void addScoreToBlack() { addScoreToBlack(1); }
    private void addScoreToBlack(int amount) { setScoreBlack(getScoreBlack() + amount); }
    private void subtractScoreFromBlack() { subtractScoreFromBlack(1); }
    private void subtractScoreFromBlack(int amount) { setScoreBlack(getScoreBlack() - amount); }
    
    private void addScoreToWhite() { addScoreToWhite(1); }
    private void addScoreToWhite(int amount) { setScoreWhite(getScoreWhite() + amount); }
    private void subtractScoreFromWhite() { subtractScoreFromWhite(1); }
    private void subtractScoreFromWhite(int amount) { setScoreWhite(getScoreWhite() - amount); }
    
    //endregion
    
    public int getMapSize() { return mapSize; }
    
    public boolean isWhiteTurn() { return whiteTurn; }
    
    public boolean isPlayerWhite() {
        return isWhiteTurn() == isPlayerTurn();
    }
    
    /**
     * Assigns the player to black or white.
     *
     * @param playerIsWhite Asign player to white?
     */
    public void setPlayerToWhite(boolean playerIsWhite) {
        setPlayerTurn(!playerIsWhite);
        setWhiteTurn(false);
    }
    
    public GameMode getGameMode() { return gameMode; }
    
    public Vector2 getFieldSize() { return fieldSize; }
    
    //TODO - not always true, sometimes there are no available moves left to do,
    // but not all tiles are filled, then the game is also finished
    public boolean isGameFinished() { return scoreWhite + scoreBlack == mapSize * mapSize; }
    
    
    
    
    
    
    
    
    
    
    
    public void AiMove() {
        // Return if we are not playing against AI
        if (gameMode == GameMode.PLAYER_VS_PLAYER) return;
        
        int delay = minAiMoveDelay + new Random().nextInt(maxAiMoveDelay - minAiMoveDelay);
//        System.out.println("ai scheduled to move after " + delay + " milliseconds");
        
        // Call AiMove after a few milliseconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {
                Platform.runLater(() -> {
                    move();
                });
            }
        }, delay);
    }
    
    //TODO this is temporary, find a fix for this (should not be separate function
    private void move() {
        System.out.println("ai moving");
        if (ai.equals("random")) Ai.aiRandom(this);
        if (ai.equals("minimax")){
            try {
                Ai.aiMiniMax(this,"White", 10);
            }catch (Exception e){
                System.out.println("Ai couldn't clone model: " + e);
            }
        }
    }
    
    
    
    
    
    
    
    
    
    

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
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
        
        if (connection.isConnected())
            System.out.println(valid ? "valid move" : "not a valid move");
        
        // If the position is valid, add score and update the interface
        if (valid) {
            // Add score to the correct player
            if (whiteTurn)
                addScoreToWhite();
            else
                addScoreToBlack();
            
            // Place the new tile
            view.updateTileGraphic(whiteTurn, xPos, yPos);
            
            // Send the move to the server
            if (!forceClick)
                if (connection.isConnected())
                    sender.sendMove(convertPositionToIndex(xPos, yPos));
        }
        
        // Swap the turn, only if this turn was valid
        setWhiteTurn(valid != isWhiteTurn());
        
        playerTurn = valid != playerTurn;
        view.updateTurnLabel(whiteTurn);
        
        if (gameMode != GameMode.ONLINE) {
            if (gameMode == GameMode.PLAYER_VS_AI && !isPlayerTurn() && !forceClick) {
                System.out.println("\t\t\t\tai moving");
                int delay = minAiMoveDelay + new Random().nextInt(maxAiMoveDelay - minAiMoveDelay);
                
                // Call AiMove after a few milliseconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override public void run() { Platform.runLater(() -> { AiMove(); }); }
                }, delay);
            }
        }
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
            String currentPlayerId  = view.getPlayerId(whiteTurn);
            
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
                    view.updateTileGraphic(whiteTurn, x, y);
                    
                    // Add score to the correct player, and remove score from the other player
                    if (whiteTurn) {
                        addScoreToWhite();
                        subtractScoreFromBlack();
                    }
                    else {
                        addScoreToBlack();
                        subtractScoreFromWhite();
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
        String currentPlayerId  = view.getPlayerId(whiteTurn);
        String oppositePlayerId = view.getPlayerId(!whiteTurn);
    
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
    
//    private void checkWinCondition() {
//        isGameFinished = scoreWhite + scoreBlack == mapSize * mapSize;
//    }
    
    public ReversiView getView() {
        return view;
    }

    void setView(ReversiView view){
        this.view = view;
    }
    
    public String[] getPlayerList() {
        if (connection.isConnected()) sender.getPlayerlist();
        
        try { Thread.sleep(16); }
        catch (InterruptedException ignored) { }
    
        String[] allPlayers;
        if (connection.isConnected())
            allPlayers = Handler.playerlist == null ? new String[0] : Handler.playerlist;
        else allPlayers = new String[0];
        
        ArrayList<String> players = new ArrayList<>();
        
        for (String p : allPlayers) {
            if (!p.equals(name))
                players.add(p);
        }
        
        return players.toArray(new String[0]);
    }
    
    public void challengePlayer(Button btn) {
        if (!connection.isConnected()) return;
        
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
        if (!connection.isConnected()) return;
        view.refreshPlayerList(getPlayerList());
    }
    
    /**
     * Accept a challenge from someon else.
     * @param nr Challenge number.
     */
    public void acceptChallenge(String nr) {
        if (!connection.isConnected()) return;
        System.out.println("challenge " + nr + " accepted");
        sender.acceptAChallenge(nr);
        
        // TODO - call method start new game
    }
    
    //TEMP
    public void setName(String name) { this.name = name; }
    
    public void challengeReceived(String challenger, String nr) {
        if (!connection.isConnected()) return;
        view.challengeReceived(challenger, nr);
    }
    
//    public void setAgainstPlayer(boolean againstPlayer) { this.againstPlayer = againstPlayer; }
//    public boolean isAgainstPlayer() { return againstPlayer; }
    
    public int convertPositionToIndex(int x, int y) {
        return mapSize * y + x;
    }
    public Vector2 convertIndexToPosition(int i) {
        return new Vector2(i % mapSize, (int)Math.floor(i / (float)mapSize));
    }
    
    public String getName() { return name; }
    
    public boolean isPlayerTurn() { return playerTurn; }
    public void setPlayerTurn(boolean playerTurn) { this.playerTurn = playerTurn; }
    
}
