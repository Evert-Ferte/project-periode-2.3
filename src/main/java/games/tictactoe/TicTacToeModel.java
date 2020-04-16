package games.tictactoe;

import games.Ai;
import games.Model;
import games.Vector2;
import javafx.application.Platform;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TicTacToeModel extends Model {
    private static final int mapSize = 3;

    //Ai variables
    private static final String ai = "minimaxAlphaBeta"; //  "random", "minimaxAlphaBeta", "minimaxRiskRegion"
    private static final int depth = 5;

    private boolean aiPlayer = true; //true=white, false=black

    //General variables
    private TicTacToeView view;
    private TicTacToeBoard board;

    private GameMode gameMode = GameMode.PLAYER_VS_PLAYER;
    private String clientName;

    public TicTacToeModel(TicTacToeView view) {
        this.view = view;
        this.board = new TicTacToeBoard(mapSize);
    }

    @Override
    public void startApplication() {

    }

    @Override
    public void gameStart(GameMode mode) {
        resetVariables();

        setGameMode(mode);
        view.startMatch();

        // If not online, assign the player to the black tiles
        if (mode != GameMode.ONLINE)
            board.setPlayerToWhite(false);
        else
            board.setPlayerTurn(false);
        updateView();
    }

    @Override
    public void gameEnd(boolean won) {
        // TODO - do general game end stuff here, and call onGameWon() or onGameLost()

        if (won) onGameWon();
        else     onGameLost();
        // TODO - close game here
    }

    @Override
    public void onGameWon() {
        // TODO - specific game won stuff here

        log("game won");
        view.goToMainMenu();
    }

    @Override
    public void onGameLost() {
        log("game lost");
        view.goToMainMenu();
    }

    @Override
    public void forfeitGame() {
        gameEnd(false);
    }

    @Override
    public void resetVariables() {
        board.reset();
    }

    @Override
    public void turnHandler() {
        turnEnd();
        if (gameMode == GameMode.PLAYER_VS_AI)
            if (!board.isPlayerTurn())
                AiMove();

        log("next turn");
        log("isPlayerTurn: " + board.isPlayerTurn());
    }

    @Override
    public void turnStart() {

    }

    @Override
    public void turnEnd() {
        // Update the view on the end of each turn
        board.switchTurn();
        updateView();
    }

    @Override
    public void updateView() {
        view.update();
    }

    @Override
    public void AiMove() {
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
                    if (ai.equals("minimaxAlphaBeta")){
                        try {
                            position = Ai.aiMiniMaxAlphaBetaPruning(board, depth, aiPlayer);
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

    @Override
    public void clickPosition(int x, int y) {
        if (board.isGameFinished()) return;
        if(board.move(x,y)){
            System.out.println("handel");
            turnHandler();
        }
        updateView();
    }


    public String getClientName() { return clientName; }
    public void setClientName(String name) { this.clientName = name; }

    public void setAiPlayer(boolean player){aiPlayer = player;}

    public TicTacToeBoard getBoard(){return board;}

}
