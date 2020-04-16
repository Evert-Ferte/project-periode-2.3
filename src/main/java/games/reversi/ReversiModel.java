package games.reversi;

import javafx.application.Platform;
import javafx.scene.control.Button;
import network.ConnectionModel;
import network.HandlerModel;
import network.ReceiverModel;
import network.SenderModel;
import java.io.IOException;
import java.util.*;

public class ReversiModel{
    private static final int mapSize = 8;
    
    //Ai variables
    private static final int depth = 5;
    private String ai = "minimax";              // random, minimax, minimaxRiskRegion

    //risk region values
    private static final int cornerValue = 100;
    private static final int antiCornerValue = -50;
    private static final int antiCornerEdgeValue = -20;
    private static final int edgeValue = 5;
    private static final int edgeCornerValue = 10;
    private static final int antiEdgeValues = -2;

    private static boolean aiPlayer = true;     //true=white, false=black
    private ArrayList<ArrayList<Integer>> riskRegion;
    
    private static final int minAiMoveDelay = 10;
    private static final int maxAiMoveDelay = 20;
    
    // General variables
    private ReversiView view;
    private ReversiBoard board;

    private GameMode gameMode = GameMode.PLAYER_VS_PLAYER;

    public enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI,
        ONLINE
    }
    
    // Networking variables
    private String ip = "localhost";        // server ip: 145.33.225.170
    private int port = 7789;
    private int timeout = 10;
    private ConnectionModel connection;
    
    private String clientName = "D5";
    private SenderModel sender;
    private HandlerModel handler;
    
    /**
     * The constructor. Sets the view reference.
     *
     * @param view The reference to a view, that is part of this MVC model.
     */
    public ReversiModel(ReversiView view) {
        this.view = view;
        this.board = new ReversiBoard(mapSize);
        riskRegion = Ai.generateRiskRegions(mapSize, cornerValue, antiCornerValue, antiCornerEdgeValue, edgeValue, edgeCornerValue, antiEdgeValues);
    }
    
    /**
     * Called when clicking on a game tile. Tries to create a connection with the game server.
     */
    public void startApplication() {
        createConnection();
    }
    
//region Game state functions (on game start, end, win, loss, etc)
    
    /**
     * Call on the start of the game. Sets up the game for use.
     *
     * @param mode In what game mode should the game be started?
     */
    public void gameStart(GameMode mode) {
        resetVariables();

        setGameMode(mode);
        board.placeStartingTiles();
        view.startMatch();

        // If not online, assign the player to the black tiles
        if (mode != GameMode.ONLINE)
            board.setPlayerToWhite(false);
        else
            board.setPlayerTurn(false);
        updateView();
    
        log("Starting new game");
    }
    
    /**
     * Called on the end of a game. Sets the state of the game to won/loss.
     *
     * @param won Has the game been won by the client?
     */
    public void gameEnd(boolean won) {
        if (won) onGameWon();
        else     onGameLost();
    }
    
    /**
     * Called when the game has been won by the client.
     */
    private void onGameWon() {
        log("Game won");
        view.goToMainMenu();
    }
    
    /**
     * Called when the game has been lost by the client.
     */
    private void onGameLost() {
        log("Game lost");
        view.goToMainMenu();
    }
    
//endregion
    
    /**
     * Forfeits the game and brings the user back to the menu.
     */
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
        board.reset();
    }
    
//region Turn handling functions
    
    /**
     * Handles the turns. End the turn and enables the next player to make a move.
     */
    private void turnHandler() {
        turnEnd();
        
        if (gameMode == GameMode.PLAYER_VS_AI)
            if (!board.isPlayerTurn())
                AiMove();
    }
    
    /**
     * Called on the start of a turn.
     */
    public void turnStart() {
        log("Turn started");
    }
    
    /**
     * Called on the end of a turn.
     */
    private void turnEnd() {
        // Update the view on the end of each turn
        boolean gameFinished = board.isGameFinished();

        log("Turn ended, switching turn...");
        board.switchTurn();

        updateView();
    }
    
//endregion
    
    /**
     * Updates all UI components.
     */
    private void updateView() { view.update(); }

// region ai movement

    /**
     * Makes the AI make a move based on the given algorithm.
     */
    public void AiMove() {
        // Return if we are not playing against AI
        if (gameMode == GameMode.PLAYER_VS_PLAYER) return;

        int delay = minAiMoveDelay + new Random().nextInt(maxAiMoveDelay - minAiMoveDelay);

        // Call AiMove after a few milliseconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {
                Platform.runLater(() -> {
                    log("AI planning a move...");
                    Vector2 position = null;
                    if (ai.equals("random")) {
                        position = Ai.aiRandom(board);
                    }
                    if (ai.equals("minimax")) {
                        try {
                            position = Ai.aiMiniMaxAlphaBetaPruning(board, depth, aiPlayer);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ai.equals("minimaxRiskRegion")) {
                        try {
                            position = Ai.aiMiniMaxAlphaBetaPruningRiskRegions(board, riskRegion, depth, aiPlayer);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    if(position != null) clickPosition((int)position.x, (int)position.y);
//                    else log("position null on line 193 in ReversiModel.java");
                    updateView();
                });
            }
        }, delay);
    }

// endregion
    
    /**
     * Places a tile on the board (if given a valid position), and flips tiles in trapped by the
     * current active player.
     *
     * @param x X position where the tile will be placed.
     * @param y Y position where the tile will be placed.
     */

    public void clickPosition(int x, int y) {
        if (board.isGameFinished()) {
            log("Game finished, no more available spots");
            return;
        }
        if(board.move(x,y)) {
            // Send the move to the server (only if this command came from our client (it is our turn) )
            if (board.isPlayerTurn() && gameMode == GameMode.ONLINE)
                sender.sendMove(board.convertPositionToIndex(x, y));

            log("Successfully placed tile on ("+x+", "+y+")");
            
            turnHandler();
        }
        else {
            log("("+x+", "+y+") NOT A VALID POSITION!");
        }
        updateView();
    }

//region Networking functions
    
    /**
     * Tries to create a connection with the game server.
     */
    private void createConnection() {
        // Close the previous connection (if there is one)
        if (connection != null)
            connection.terminate();
        
        // Create a new connection
        connection = new ConnectionModel(ip, port);
        
        // Create a sender, receiver and handler if a connection was made
        if (connection.isConnected()) {
            handler = new HandlerModel(this);
            
            try {
                ReceiverModel receiver = new ReceiverModel(connection.getSocket(), handler);
                receiver.start();
                
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ignored) { }
            } catch (IOException ignored) { }
            
            sender = new SenderModel(connection.getSocket());
            sender.login(clientName);
            sender.getPlayerlist();
        }
    }
    
    /**
     * Closes the current active connection (if there is one).
     */
    public void closeConnection() {
        connection.terminate();
    }
    
    /**
     * Challenges a player to a game of reversi.
     *
     * @param btn The pressed challenge button.
     */
    public void challengePlayer(Button btn) {
        if (!connection.isConnected()) return;
        
        log("Challenging player: " + btn.getId().trim());
        sender.challenge(btn.getId(), "Reversi");
    }
    
    /**
     * Accept a challenge from someone else.
     *
     * @param nr Challenge number.
     */
    public void acceptChallenge(String nr) {
        if (!connection.isConnected()) return;
        
        sender.acceptAChallenge(nr);
        log("Challenge " + nr + " accepted");
    }
    
    /**
     * Called when a challenge has been received by another user.
     *
     * @param challenger The use we have received the challenge from.
     * @param nr The number of the received challenge.
     */
    public void challengeReceived(String challenger, String nr) {
        log("Challenge received(" + nr + "), from " + challenger);
        
        if (!connection.isConnected()) return;
        
        acceptChallenge(nr);
    }
    
    /**
     * Get a list of active players on the current active connection.
     *
     * @return Returns a list of player names.
     */
    public String[] getPlayerList() {
        if (connection.isConnected()) sender.getPlayerlist();
        
        // Wait for 16 milliseconds to enable the network to get the player list
        try { Thread.sleep(16); }
        catch (InterruptedException ignored) { }
        
        // Set the list of all online players
        String[] allPlayers;
        if (connection.isConnected())
            allPlayers = handler.playerlist == null ? new String[0] : handler.playerlist;
        else allPlayers = new String[0];
        
        ArrayList<String> players = new ArrayList<>();
        
        // Remove this client from the player list.
        for (String p : allPlayers) {
            p = p.trim();
            if (!p.equals(clientName.trim()))
                players.add(p);
        }
        
        return players.toArray(new String[0]);
    }
    
    /**
     * Refreshes the list of online players on the active connection.
     */
    public void refreshPlayerList() {
        if (!connection.isConnected()) return;
        view.refreshPlayerList(getPlayerList());
    }

//endregion

//region Getters and setters

    public GameMode getGameMode() { return gameMode; }
    private void setGameMode(GameMode mode) { gameMode = mode; }

    public String getClientName() { return clientName; }
    public void setClientName(String name) { this.clientName = name; }
    
    public ReversiBoard getBoard(){return board;}
    
    public String getAi() { return ai; }
    public void setAi(String aiType) {
        aiType = aiType.toLowerCase();
        
        // Set the ai type to the given ai type, if no match was found, set the ai type to minimax.
        switch (aiType) {
            case "minimaxRiskRegion":
            case "random":
                ai = aiType;
                break;
            default:
                ai = "minimax";
        }
    }
    
    public String getIp() { return ip; }
    public void setIp(String ip) {
        this.ip = ip;
        createConnection();
    }
    
    public int getPort() { return port; }
    public void setPort(int port) {
        this.port = port;
        createConnection();
    }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
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
        
        // Prevent the name from overflowing the name part of the log message
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
        
        // Log the given message with the template
        System.out.println(template + message);
    }
}
