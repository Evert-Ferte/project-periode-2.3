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

    //Ai variables
    private static final String ai = "random";
    private static final int depth = 5;
    //risk region values
    private static final int cornerValue = 15;
    private static final int antiCornerValue = -14;
    private static final int edgeValue = 10;
    private static final int antiEdgeValues = -9;

    private static boolean aiPlayer = true; //true=white, false=black
    private ArrayList<ArrayList<Integer>> riskRegion;



    private static final int minAiMoveDelay = 10;//180;
    private static final int maxAiMoveDelay = 20;//800;
    
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
    private static final String ip = /*"145.33.225.170";*/ "localhost";
    private static final int port = 7789;
    private Connection connection;
    
    private String clientName = "client_1";
    private Sender sender;
    private Handler handler;
    
    /**
     * The constructor. Sets the view reference.
     *
     * @param view The reference to a view, that is part of this MVC model.
     */
    public ReversiModel(ReversiView view) {
        this.view = view;
        this.board = new ReversiBoard(mapSize);
        riskRegion = Ai.generateRiskRegions(mapSize, cornerValue, antiCornerValue, edgeValue , antiEdgeValues); //mapSize, cornerValue, antiCornerValue, edgeValue.

    }
    
    /**
     * Called when clicking on a game tile. Tries to create a connection with the game server.
     */
    public void startApplication() {
        createConnection();
    }
    
    public void gameStart(GameMode mode) {
        log("restart");
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
    }
    public void gameEnd(boolean won) {
        // TODO - do general game end stuff here, and call onGameWon() or onGameLost()

        if (won) onGameWon();
        else     onGameLost();
        
//        updateView();
        
        // TODO - close game here
    }
    private void onGameWon() {
        // TODO - specific game won stuff here
        
        log("game won");
        view.goToMainMenu();
    }
    private void onGameLost() {
        // TODO - specific game lost stuff here
        
        log("game lost");
        view.goToMainMenu();
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
        board.reset();
    }
    
//region Turn handling functions
    
    private void turnHandler() {
        turnEnd();
        
        if (gameMode == GameMode.PLAYER_VS_AI)
            if (!board.isPlayerTurn())
                AiMove();
        
        log("next turn");
        log("isPlayerTurn: " + board.isPlayerTurn());
    }
    public void turnStart() {
    
    }
    int counter = 0;
    private void turnEnd() {
        // Update the view on the end of each turn
        boolean gameFinished = board.isGameFinished();

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
     * Desc here...
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
                    log("ai moving");
                    Vector2 position = null;
                    if (ai.equals("random")){
                        position = Ai.aiRandom(board);
                    }
                    if (ai.equals("minimax")){
                        try {
                            position = Ai.aiMiniMaxAlphaBetaPruningRiskRegions(board, riskRegion, depth, aiPlayer);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(position != null) clickPosition((int)position.x, (int)position.y);
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
        if (board.isGameFinished()) return;
        if(board.move(x,y)){
            // Send the move to the server (only if this command came from our client (it is our turn) )
            if (board.isPlayerTurn() && gameMode == GameMode.ONLINE)
                sender.sendMove(board.convertPositionToIndex(x, y));

            turnHandler();
        }
        updateView();
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
        
        log("challenging player: " + btn.getId().trim());
        sender.challenge(btn.getId(), "Reversi");
        
//        boolean challenged = true;
//        String id = null;
//
//        try {
//            int i = Integer.parseInt(btn.getId());
//            id = btn.getId();
//            challenged = false;
//        }
//        catch (NumberFormatException ignored) { }
//
//        if (challenged) {
//            log("challenging player:");
//            log("player: " + btn.getId());
//            sender.challenge(btn.getId(), "Reversi");
//        }
//        else {
//            log("accept challenge");
//            sender.acceptAChallenge(id);
//        }
    }
    
    /**
     * Accept a challenge from someon else.
     * @param nr Challenge number.
     */
    public void acceptChallenge(String nr) {
        if (!connection.isConnected()) return;
        
        log("challenge " + nr + " accepted");
        sender.acceptAChallenge(nr);
    }
    
    public void challengeReceived(String challenger, String nr) {
        log("receiving challenge...");
        
        if (!connection.isConnected()) return;
        
        acceptChallenge(nr);
        
//        view.challengeReceived(challenger, nr);
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
    
        System.out.println("online players: " + players.toString());
        
        return players.toArray(new String[0]);
    }
    
    public void refreshPlayerList() {
        System.out.println("getting player list");
        if (!connection.isConnected()) return;
        view.refreshPlayerList(getPlayerList());
    }

//endregion

//region Getters and setters

    private void setGameMode(GameMode mode) {
        gameMode = mode;
    }

    public String getClientName() { return clientName; }
    public void setClientName(String name) { this.clientName = name; }
    
    public GameMode getGameMode() { return gameMode; }

    public ReversiBoard getBoard(){return board;}
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
