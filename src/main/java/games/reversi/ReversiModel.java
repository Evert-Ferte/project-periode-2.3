package games.reversi;

import javafx.application.Platform;
import javafx.scene.control.Button;
import network.Connection;
import network.Handler;
import network.Receiver;
import network.Sender;
import java.io.IOException;
import java.util.*;

public class ReversiModel{
    private static final int mapSize = 8;

    private static final String ai = "random";
    private static final int minAiMoveDelay = 180;
    private static final int maxAiMoveDelay = 800;
    
    // General variables
    private ReversiView view;

    private static final String emptyId = "e";
    private static final String blackId = "b";
    private static final String whiteId = "w";

    private int scoreWhite = 0;
    private int scoreBlack = 0;
    private boolean whiteTurn = false;
    private boolean playerTurn = false;

    ArrayList<ArrayList<String>> modelMap;

    private GameMode gameMode = GameMode.PLAYER_VS_PLAYER;

    public enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI,
        ONLINE
    }
    
    // Networking variables
    private static final String ip = /*"145.33.225.170";*/ "localhost";
    private static final int port = 7789;
    private Connection connection;
    
    private String clientName = "client_0";
    private Sender sender;
    private Handler handler;
    
    /**
     * The constructor. Sets the view reference.
     *
     * @param view The reference to a view, that is part of this MVC model.
     */
    public ReversiModel(ReversiView view) {
        this.view = view;
    }
    
    /**
     * Called when clicking on a game tile. Tries to create a connection with the game server.
     */
    public void startApplication() {
        createConnection();
    }
    
    public void gameStart(GameMode mode) {

        resetVariables();

        setGameMode(mode);
        placeStartingTiles();
        view.startMatch();

        // If not online, assign the player to the black tiles
        if (mode != GameMode.ONLINE)
            setPlayerToWhite(false);
        else
            setPlayerTurn(false);
    }
    public void gameEnd(boolean won) {
        // TODO - do general game end stuff here, and call onGameWon() or onGameLost()

        if (won) onGameWon();
        else     onGameLost();
        
        // TODO - close game here
    }
    private void onGameWon() {
        // TODO - specific game won stuff here
        
        log("game won");
    }
    private void onGameLost() {
        // TODO - specific game lost stuff here
        
        log("game lost");
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
        generateModelMap();
        setScoreWhite(0);
        setScoreBlack(0);
        setWhiteTurn(false);
        setPlayerTurn(true);
    }
    
//region Turn handling functions
    
    private void turnHandler() {
        turnEnd();
        
        if (gameMode == GameMode.PLAYER_VS_AI)
            if (!isPlayerTurn())
                AiMove();
        
        log("next turn");
        log("isPlayerTurn: " + isPlayerTurn());
    }
    public void turnStart() {
    
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

    private void generateModelMap(){
        modelMap = new ArrayList<>();
        for (int y = 0; y < mapSize; y++) {
            modelMap.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                modelMap.get(y).add(emptyId);
            }
        }
        System.out.println(modelMap);
    }

//region Functions for placing and flipping tiles
    
    /**
     * Places a tile on the board (if given a valid position), and flips tiles in trapped by the
     * current active player.
     *
     * @param x X position where the tile will be placed.
     * @param y Y position where the tile will be placed.
     */

    public void clickPosition(int x, int y) {
        if (isGameFinished()) return;
        
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
        if (isPlayerTurn() && gameMode == GameMode.ONLINE)
            sender.sendMove(convertPositionToIndex(x, y));

        turnHandler();
    }
    
    /**
     * Places the tiles that form the starting setup.
     */
    public void placeStartingTiles() {
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
            modelMap.get((int)tile.y).set((int)tile.x, getPlayerId(isWhiteTurn()));

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
        if (!modelMap.get(y).get(x).equals(emptyId)) return new ArrayList<>();

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
        String oppositePlayerId = getPlayerId(!whiteTurn);
        String playerId         = getPlayerId(whiteTurn);
        boolean valid = false;
        ArrayList<Vector2> tilesInDirection = new ArrayList<>();

        int newX = x + xDir;
        int newY = y + yDir;

        // Loop while the new position is still a valid position on the board
        int i = 1;
        while (isPositionInBoard(newX, newY)) {
            // Get the ID of the current tile
            String id = modelMap.get(newY).get(newX);

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
    
    /**
     * Desc here...
     */
    public void AiMove() {
        // Return if we are not playing against AI
        if (gameMode == GameMode.PLAYER_VS_PLAYER) return;
        
        int delay = minAiMoveDelay + new Random().nextInt(maxAiMoveDelay - minAiMoveDelay);
        
        ReversiModel modelReference = this;
        
        // Call AiMove after a few milliseconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {
                Platform.runLater(() -> {
                    log("ai moving");
                    if (ai.equals("random")) Ai.aiRandom(modelReference);
                    if (ai.equals("minimax")){
                        try {
                            //Ai.aiMiniMax(modelReference,"White", 10);
                        }catch (Exception e){
                            log("Ai couldn't clone model: " + e);
                        }
                    }
                });
            }
        }, delay);
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

        // Loop through the map/board
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                // Get the current tile id
                String tileId = modelMap.get(y).get(x);
                
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
    
//region Networking functions
    
    /**
     * Tries to create a connection with the game server.
     */
    private void createConnection() {
        connection = new Connection(ip, port);

        if (connection.isConnected()) {
            handler = new Handler(this);
            
            try {
                Receiver receiver = new Receiver(connection.getSocket(), handler);
                receiver.start();
                
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ignored) { }
            } catch (IOException ignored) { }
            
            sender = new Sender(connection.getSocket());
            sender.login(clientName);
            sender.getPlayerlist();
        }
    }
    
    public void closeConnection() {
        connection.terminate();
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
            log("challenging player:");
            log("player: " + btn.getId());
            sender.challenge(btn.getId(), "Reversi");
        }
        else {
            log("accept challenge");
            sender.acceptAChallenge(id);
        }
    }
    
    /**
     * Accept a challenge from someon else.
     * @param nr Challenge number.
     */
    public void acceptChallenge(String nr) {
        if (!connection.isConnected()) return;
        log("challenge " + nr + " accepted");
        sender.acceptAChallenge(nr);
        
        // TODO - call method start new game
    }
    
    public void challengeReceived(String challenger, String nr) {
        if (!connection.isConnected()) return;
        view.challengeReceived(challenger, nr);
    }
    
    public String[] getPlayerList() {
        if (connection.isConnected()) sender.getPlayerlist();
        
        try { Thread.sleep(16); }
        catch (InterruptedException ignored) { }
        
        String[] allPlayers;
        if (connection.isConnected())
            allPlayers = handler.playerlist == null ? new String[0] : handler.playerlist;
        else allPlayers = new String[0];
        
        ArrayList<String> players = new ArrayList<>();
        
        for (String p : allPlayers) {
            p = p.trim();
            if (!p.equals(clientName.trim()))
                players.add(p);
        }
        
        return players.toArray(new String[0]);
    }
    
    public void refreshPlayerList() {
        if (!connection.isConnected()) return;
        view.refreshPlayerList(getPlayerList());
    }

//endregion

//region Getters and setters
    
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

    public ArrayList<ArrayList<String>> getModelMap() {
        return modelMap;
    }

    public String getWhiteId(){ return whiteId;}
    public String getBlackId(){ return blackId;}

    //endregion
    
    public boolean isWhiteTurn() { return whiteTurn; }
    
    public boolean isPlayerWhite() { return isWhiteTurn() == isPlayerTurn(); }
    /**
     * Assigns the player to black or white.
     *
     * @param playerIsWhite Assign player to white?
     */
    public void setPlayerToWhite(boolean playerIsWhite) {
        setPlayerTurn(!playerIsWhite);
        setWhiteTurn(false);
    }

    public String getPlayerId(boolean turn) { return turn ? whiteId : blackId; }
    public String getEmptyId() { return emptyId; }

    public boolean isPlayerTurn() { return playerTurn; }
    public void setPlayerTurn(boolean playerTurn) { this.playerTurn = playerTurn; }
    
    public int convertPositionToIndex(int x, int y) { return mapSize * y + x; }
    public Vector2 convertIndexToPosition(int i) {
        return new Vector2(i % mapSize, (int)Math.floor(i / (float)mapSize));
    }
    
    public String getClientName() { return clientName; }
    public void setClientName(String name) { this.clientName = name; }
    
    public boolean isGameFinished() {
        // Loop through the map and find tiles that can still be flipped
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                ArrayList<Vector2> tiles = getTilesToFlip(x, y);
                
                // The game is finished when no tiles can be flipped, so return false if there are still tiles left
                if (tiles.size() > 0)
                    return false;
            }
        }
        
//        return scoreWhite + scoreBlack == mapSize * mapSize;
        return true;
    }
    
    public GameMode getGameMode() { return gameMode; }
    
    public int getMapSize() { return mapSize; }
    

//endregion
    
    /**
     * log a message, like System.out.println(), but add the client's name before the message.
     *
     * @param message The message that will be printed.
     */
    public void log(String message) {
        StringBuilder template = new StringBuilder();
        
        int length = 20;
        int curOffLength = length - 4;
        for (int i = 0; i < length; i++) {
            String txt = "";
            
            if (i < clientName.length() && i < curOffLength)
                txt = String.valueOf(clientName.charAt(i));
            else if (i < curOffLength)
                txt = " ";
            else if (clientName.length() < curOffLength)
                txt = " ";
            else if (i < length - 1)
                txt = ".";
            
            // Always add this on the last node
            if (i == length - 1)
                txt = " : ";
            
            template.append(txt);
        }
    
        System.out.println(template + message);
    }
}
