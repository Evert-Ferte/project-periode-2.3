package games.tictactoe;

import games.Board;
import games.Vector2;

import java.util.ArrayList;

public class TicTacToeBoard extends Board {
    private int mapSize = 3;

    public TicTacToeBoard(int mapSize) {
        this.mapSize = mapSize;
        generateModelMap(mapSize);
    }

    @Override
    public void reset() {
        generateModelMap(mapSize);
        scoreBlack = 0;
        scoreWhite = 0;
        setWhiteTurn(false);
        setPlayerTurn(true);
    }

    @Override
    public boolean isGameFinished() {
        return checkWinner(whiteId) || checkWinner(blackId) || (getAvailablePositions().length <= 0);
    }
    private boolean checkWinner(String player){
        //Diagonal wins.
        if ((modelMap.get(0).get(0).equals(modelMap.get(1).get(1)) && modelMap.get(0).get(0).equals(modelMap.get(2).get(2)) && modelMap.get(0).get(0).equals(player)) ||
                (modelMap.get(2).get(0).equals(modelMap.get(1).get(1)) && modelMap.get(2).get(0).equals(modelMap.get(0).get(2)) && modelMap.get(2).get(0).equals(player))) {
            return true;
        }
        //Vertical and horizontal wins.
        for (int i = 0; i < mapSize; ++i) {
            if (((modelMap.get(i).get(0).equals(modelMap.get(i).get(1)) && modelMap.get(i).get(0).equals(modelMap.get(i).get(2)) && modelMap.get(i).get(0).equals(player))
                    || (modelMap.get(0).get(i).equals(modelMap.get(1).get(i)) && modelMap.get(0).get(i).equals(modelMap.get(2).get(i)) && modelMap.get(0).get(i).equals(player)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Vector2[] getAvailablePositions() {
        ArrayList<Vector2> pos = new ArrayList<>();
        for(int y = 0; y < mapSize; ++y){
            for(int x = 0; x < mapSize; ++x){
                if(modelMap.get(y).get(x).equals(emptyId)){
                    pos.add(new Vector2(x,y));
                }
            }
        }

        return pos.toArray(new Vector2[0]);
    }

    @Override
    public boolean move(int x, int y) {
        if (modelMap.get(y).get(x).equals(emptyId)){
            modelMap.get(y).set(x,getPlayerId(whiteTurn));
            return true;
        }

        return false;

    }

    @Override
    public TicTacToeBoard clone() throws CloneNotSupportedException {
        TicTacToeBoard cloneBoard = (TicTacToeBoard) super.clone();
        return cloneBoard;
    }

    @Override
    public int getMapSize(){
        return mapSize;
    }
}
