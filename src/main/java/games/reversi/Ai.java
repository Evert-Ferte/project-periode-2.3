package games.reversi;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Ai {

    static Vector2 aiRandom(ReversiBoard board){
        // TODO - Check if this if statement is needed. AI can be either black or white
//        if(game.isWhiteTurn()){
//        }
        
        Vector2[] avPos = board.getAvailablePositions();
        
        if (avPos.length > 0) {
            Vector2 randomPosition = avPos[(int)(Math.random() * avPos.length)];
            return new Vector2((int)randomPosition.x, (int)randomPosition.y);
        }
        else {
            System.out.println("WARNING! No available positions for AI to choose from!");
        }
        return null;
    }

    static Vector2 aiMiniMax(ReversiBoard board, int depth, boolean player) throws CloneNotSupportedException {
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;

        for (Vector2 position : board.getAvailablePositions()) {
           ReversiBoard cloneBoard = board.clone();
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMaxAlphaBetaPruning(cloneBoard, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, player);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARING! No position found with minimax!");
        }
        return aiRandom(board);
    }
    static Vector2 aiMiniMaxAlphaBetaPruning(ReversiBoard board, int depth, boolean player) throws CloneNotSupportedException {
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;

        for (Vector2 position : board.getAvailablePositions()) {
            ReversiBoard cloneBoard = board.clone();
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMaxAlphaBetaPruning(cloneBoard, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, player);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARING! No position found with minimax!");
        }
        return aiRandom(board);
    }
    static Vector2 aiMiniMaxAlphaBetaPruningRiskRegions(ReversiBoard board, ArrayList<ArrayList<Integer>> riskRegions, int depth, boolean player) throws CloneNotSupportedException {
        //Initial values
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;

        //Looping though positions and getting the score of those positions
        for (Vector2 position : board.getAvailablePositions()) {
            ReversiBoard cloneBoard = board.clone();
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMaxAlphaBetaPruningRiskRegions(cloneBoard, riskRegions, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, player) + riskRegions.get((int)position.y).get((int)position.x);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARING! No position found with minimax!");
        }
        return aiRandom(board);
    }

    private static Integer getStaticEvaluation(ReversiBoard board, int depth) {
        if(depth == 0 || board.isGameFinished()) {
            return null;
        }
        int eval;
        if(board.getScoreWhite() - board.getScoreBlack() == 0){
            eval = 0;
        }else if(board.getWhiteTurn()){
            eval = board.getScoreWhite() - board.getScoreBlack();
        }else{
            eval = board.getScoreBlack() - board.getScoreWhite();
        }
        return eval;
    }
    static ArrayList<ArrayList<Integer>> generateRiskRegions(int size, int cornerValue, int antiCornerValue, int edgeValue, int antiEdgeValue){
        int mapSize = size-1;

        if(mapSize <= 2){
            return null;
        }

        //Generating risk regions
        ArrayList<ArrayList<Integer>> riskRegions = new ArrayList<>();
        for(int y=0; y<=mapSize; y++){
            ArrayList<Integer> col = new ArrayList<>();
            for(int x=0; x<=mapSize; x++){
                //Corner values
                if((x == 0 || x == mapSize) && (y == 0 || y == mapSize)){
                    col.add(cornerValue);
                }else if(((y == 1 || y == mapSize-1) && (x == 0 || x == 1 || x == mapSize-1 || x == mapSize)) || ((x == 1 || x == mapSize-1) && (y == 0 || y == mapSize)) ){
                    //anti corner values
                    col.add(antiCornerValue);
                }else if(((x > 1 && x < mapSize-1) && (y == 0 || y == mapSize) || ((y > 1 && y < mapSize-1) && (x == 0 || x == mapSize)))){
                    // Edge values
                    col.add(edgeValue);
                }else if(((x > 1 && x < mapSize-1) && (y == 1 || y == mapSize-1) || ((y > 1 && y < mapSize-1) && (x == 1 || x == mapSize-1)))){
                    //anti edge values
                    col.add(antiEdgeValue);
                }else{
                    col.add(0);
                }
            }
            riskRegions.add(col);
        }
        System.out.println(riskRegions);
        return riskRegions;
    }

    private static int miniMax(ReversiBoard board, int depth, boolean isMaximizing) throws CloneNotSupportedException {
        Integer eval = getStaticEvaluation(board, depth);
        if (eval != null) return eval;
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();

                int score = miniMax(cloneBoard,depth-1, false);
                optimumScore = Integer.max(score, optimumScore);
            }
        }
        else{
            optimumScore = Integer.MAX_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();
                int score = miniMax(cloneBoard, depth-1,true);
                optimumScore = Integer.min(score, optimumScore);
            }
        }
        return optimumScore;
    }

    private static int miniMaxAlphaBetaPruning(ReversiBoard board, int depth, int alpha, int beta, boolean isMaximizing) throws CloneNotSupportedException {
        Integer eval = getStaticEvaluation(board, depth);
        if (eval != null) return eval;
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();

                int score = miniMaxAlphaBetaPruning(cloneBoard,depth-1, alpha, beta, false);
                optimumScore = Integer.max(score, optimumScore);
                alpha = Integer.max(alpha, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
        }
        else{
            optimumScore = Integer.MAX_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();
                int score = miniMaxAlphaBetaPruning(cloneBoard, depth-1, alpha, beta,true);
                optimumScore = Integer.min(score, optimumScore);
                beta = Integer.min(beta, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
        }
        return optimumScore;
    }


    private static int miniMaxAlphaBetaPruningRiskRegions(ReversiBoard board, ArrayList<ArrayList<Integer>> riskRegions, int depth, int alpha, int beta, boolean isMaximizing) throws CloneNotSupportedException {
        Integer eval = getStaticEvaluation(board, depth);
        if (eval != null) return eval;
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();

                int score = miniMaxAlphaBetaPruningRiskRegions(cloneBoard, riskRegions, depth-1, alpha, beta, false) + riskRegions.get((int)position.y).get((int)position.x);
                optimumScore = Integer.max(score, optimumScore);
                alpha = Integer.max(alpha, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
        }
        else{
            optimumScore = Integer.MAX_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();
                int score = miniMaxAlphaBetaPruningRiskRegions(cloneBoard, riskRegions, depth-1, alpha, beta,true) - riskRegions.get((int)position.y).get((int)position.x);
                optimumScore = Integer.min(score, optimumScore);
                beta = Integer.min(beta, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
        }
        return optimumScore;
    }
}
