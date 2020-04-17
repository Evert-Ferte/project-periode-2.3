package games;

import javafx.scene.Scene;

import java.util.ArrayList;

//TODo make abstract class
public abstract class Board implements Cloneable {
    protected int mapSize = 0;

    protected static final String emptyId = "e";
    protected static final String blackId = "b";
    protected static final String whiteId = "w";

    protected int scoreWhite = 0;
    protected int scoreBlack = 0;

    protected boolean whiteTurn = false;
    protected boolean playerTurn = false;

    protected ArrayList<ArrayList<String>> modelMap;


    public void reset(int mapSize) {
        this.mapSize = mapSize;
        generateModelMap(mapSize);
        setScoreWhite(0);
        setScoreBlack(0);
        setWhiteTurn(false);
        setPlayerTurn(true);
    }

    public abstract boolean isGameFinished();

    /**
     * Returns true of the given position is in the board, and false if not.
     *
     * @param x The X position.
     * @param y The Y position
     * @return Returns if the given position is in the board.
     */
    public boolean isPositionInBoard(int x, int y) { return (x >= 0 && x < mapSize) && (y >= 0 && y < mapSize); }

    public void generateModelMap(int mapSize){
        modelMap = new ArrayList<>();
        for (int y = 0; y < mapSize; y++) {
            modelMap.add(new ArrayList<>());
            for (int x = 0; x < mapSize; x++) {
                modelMap.get(y).add(emptyId);
            }
        }
        System.out.println(modelMap);
    }

    public abstract Vector2[] getAvailablePositions();

    public abstract boolean move(int x, int y);

    public void switchTurn() {
        setWhiteTurn(!isWhiteTurn());
        setPlayerTurn(!isPlayerTurn());
    }

    public boolean isPlayerWhite() { return isWhiteTurn() == isPlayerTurn(); }


    public Board clone() throws CloneNotSupportedException {
        Board cloneBoard = (Board) super.clone();
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        for (ArrayList<String> stringArrayList: modelMap){
            ArrayList<String> newArrayList = new ArrayList<>(stringArrayList);
            arrayList.add(newArrayList);
        }
        cloneBoard.modelMap = arrayList;
        cloneBoard.scoreWhite = scoreWhite;
        cloneBoard.scoreBlack = scoreBlack;
        cloneBoard.whiteTurn = whiteTurn;
        cloneBoard.playerTurn = playerTurn;
        return cloneBoard;
    }

    @Override
    public String toString() {
        return "Board{" +
                "scoreWhite=" + scoreWhite +
                ", scoreBlack=" + scoreBlack +
                ", whiteTurn=" + whiteTurn +
                ", playerTurn=" + playerTurn +
                ", modelMap=" + modelMap +
                '}';
    }
    //Issers
    public boolean isWhiteTurn() { return whiteTurn; }
    public boolean isPlayerTurn() { return playerTurn; }

    //Getters
    public ArrayList<ArrayList<String>> getModelMap(){
        return modelMap;
    }

    public int getScoreWhite() { return scoreWhite; }
    public int getScoreBlack() { return scoreBlack; }

    public boolean getWhiteTurn() { return whiteTurn; }
    public boolean getPlayerTurn(){ return playerTurn; }
    public String getPlayerId(boolean turn){ return turn ? whiteId : blackId; }


    public String getEmptyId(){ return emptyId; }
    public String getWhiteId(){ return whiteId; }
    public String getBlackId(){ return blackId; }

    public int getMapSize() { return mapSize; }


    //Setters
    public void setScoreWhite(int score) { this.scoreWhite = score; }

    public void setScoreBlack(int score) { this.scoreBlack = score; }

    public void setWhiteTurn(boolean turn) { this.whiteTurn = turn; }

    public void setPlayerTurn(boolean turn) { this.playerTurn = turn; }

    /**
     * Assigns the player to black or white.
     *
     * @param playerIsWhite Assign player to white?
     */
    public void setPlayerToWhite(boolean playerIsWhite) {
        setPlayerTurn(!playerIsWhite);
        setWhiteTurn(false);
    }

    //Score Setters
    public void addScoreToBlack() { addScoreToBlack(1); }
    public void addScoreToBlack(int amount) { setScoreBlack(getScoreBlack() + amount); }
    public void subtractScoreFromBlack() { subtractScoreFromBlack(1); }
    public void subtractScoreFromBlack(int amount) { setScoreBlack(getScoreBlack() - amount); }

    public void addScoreToWhite() { addScoreToWhite(1); }
    public void addScoreToWhite(int amount) { setScoreWhite(getScoreWhite() + amount); }
    public void subtractScoreFromWhite() { subtractScoreFromWhite(1); }
    public void subtractScoreFromWhite(int amount) { setScoreWhite(getScoreWhite() - amount); }

    //Converters
    public int convertPositionToIndex(int x, int y) { return mapSize * y + x; }
    public Vector2 convertIndexToPosition(int i) {
        return new Vector2(i % mapSize, (int)Math.floor(i / (float)mapSize));
    }
}
