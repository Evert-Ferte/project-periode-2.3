package games;

import games.reversi.ReversiBoard;

import java.util.ArrayList;
import java.util.Arrays;


public class Ai {
    /**
     * The random ai wil pick a random available position from the board using Math.random()
     *
     * @param board The board That the ai can use to check positions and move.
     */
    public static Vector2 aiRandom(Board board){
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

    /**
     * For every possible move that can be made miniMax() is called with that move.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     */
    static Vector2 aiMiniMax(Board board, int depth) throws CloneNotSupportedException {
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;

        for (Vector2 position : board.getAvailablePositions()) {
           Board cloneBoard = board.clone();
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMax(cloneBoard, depth, false);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARNING! No position found with minimax!");
        }
        return aiRandom(board);
    }

    /**
     * For every possible move that can be made miniMaxAlphaBetaPruning() is called with that move.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     */
    public static Vector2 aiMiniMaxAlphaBetaPruning(Board board, int depth) throws CloneNotSupportedException {
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;

        for (Vector2 position : board.getAvailablePositions()) {
            Board cloneBoard = board.clone();
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMaxAlphaBetaPruning(cloneBoard, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARNING! No position found with minimax!");
        }
        return aiRandom(board);
    }

    /**
     * For every possible move that can be made miniMax() is called with that move.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     * @param riskRegions An Two dimensional array with values of certain positions.
     */
    public static Vector2 aiMiniMaxAlphaBetaPruningRiskRegions(ReversiBoard board, ArrayList<ArrayList<Integer>> riskRegions, int depth) throws CloneNotSupportedException {
        //Initial values
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;

        //Looping though positions and getting the score of those positions
        for (Vector2 position : board.getAvailablePositions()) {
            ReversiBoard cloneBoard = board.clone();
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMaxAlphaBetaPruningRiskRegions(cloneBoard, riskRegions, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, false) + riskRegions.get((int)position.y).get((int)position.x);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARNING! No position found with minimax!");
        }
        return aiRandom(board);
    }
    /**
     * The static evaluation of the board is returned when the game is finished of the depth is 0.
     * The evaluation is dependant on who's turn it is.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     */
    private static Integer getStaticEvaluation(Board board, int depth) {
        if(depth == 0 || board.isGameFinished()) {
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
        return null;
    }

    /**
     * Generates the values of positions on the board.
     *
     * @param size The size of the board.
     * @param cornerValue The value of the corners.
     * @param antiCornerValue The values of the spaces next to the corners.
     * @param antiCornerEdgeValue The values of the spaces on the edge of the corners.
     * @param edgeValue The values of the edges
     * @param edgeCornerValue The values of the edge ends.
     * @param antiEdgeValue The values of the spaces before the edges.
     */
    public static ArrayList<ArrayList<Integer>> generateRiskRegions(int size, int cornerValue, int antiCornerValue, int antiCornerEdgeValue, int edgeValue, int edgeCornerValue, int antiEdgeValue){
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
                }else if(((y == 1 || y == mapSize-1) && (x == 0 || x == mapSize)) || ((x == 1 || x == mapSize-1) && (y == 0 || y == mapSize))){
                    //anti corner edge values
                    col.add(antiCornerEdgeValue);
                }else if(((y == 1 || y == mapSize-1) && (x == 1 || x == mapSize-1))){
                    //anti corner values
                    col.add(antiCornerValue);
                }else if((((x > 2 && x < mapSize-2) && (y == 0 || y == mapSize)) || ((y > 2 && y < mapSize-2) && (x == 0 || x == mapSize)))){
                    // Edge values
                    col.add(edgeValue);
                }else if((((x == 2 || x == mapSize-2) && (y == 0 || y == mapSize)) || ((y == 2 && y == mapSize-2) || (x == 0 || x == mapSize)))){
                    // Edge corner values
                    col.add(edgeCornerValue);
                }else if((((x > 1 && x < mapSize-1) && (y == 1 || y == mapSize-1)) || ((y > 1 && y < mapSize-1) && (x == 1 || x == mapSize-1)))){
                    //anti edge values
                    col.add(antiEdgeValue);
                }else{
                    col.add(0);
                }
            }
            riskRegions.add(col);
        }
        for (ArrayList<Integer> list : riskRegions) {
            System.out.println(Arrays.toString(list.toArray()));
        }
        return riskRegions;
    }

    /**
     * For every possible move that can be made miniMax() calls itself.
     * The optimum score for the player is calculated depending on if it is maximizing.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     * @param isMaximizing The boolean for if the person who is playing is maximizing or minimizing the game.
     */
    private static int miniMax(Board board, int depth, boolean isMaximizing) throws CloneNotSupportedException {
        Integer eval = getStaticEvaluation(board, depth);
        if (eval != null) return eval;
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                Board cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();

                int score = miniMax(cloneBoard,depth-1, false);
                optimumScore = Integer.max(score, optimumScore);
            }
        }
        else{
            optimumScore = Integer.MAX_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                Board cloneBoard = board.clone();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();
                int score = miniMax(cloneBoard, depth-1,true);
                optimumScore = Integer.min(score, optimumScore);
            }
        }
        return optimumScore;
    }

    /**
     * For every possible move that can be made miniMaxAlphaBetaPruning() calls itself.
     * Alpha and beta are used to compare branches inorder to save future computation depending on if the best move is already found.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     * @param alpha The first stored value of a branch.
     * @param beta  The second stored value of a branch.
     * @param isMaximizing The boolean for if the person who is playing is maximizing or minimizing the game.
     */
    private static int miniMaxAlphaBetaPruning(Board board, int depth, int alpha, int beta, boolean isMaximizing) throws CloneNotSupportedException {
        Integer eval = getStaticEvaluation(board, depth);
        if (eval != null) return eval;
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                Board cloneBoard = board.clone();
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
                Board cloneBoard = board.clone();
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

    /**
     * For every possible move that can be made miniMaxAlphaBetaPruning() calls itself.
     * Alpha and beta are used to compare branches inorder to save future computation depending on if the best move is already found.
     *
     * @param board The board That the ai can use to check positions and move.
     * @param depth The depth that the algorithm will go with recursion.
     * @param alpha The first stored value of a branch.
     * @param beta  The second stored value of a branch.
     * @param isMaximizing The boolean for if the person who is playing is maximizing or minimizing the game.
     * @param riskRegions An Two dimensional array with values of certain positions.
     */
    private static int miniMaxAlphaBetaPruningRiskRegions(ReversiBoard board, ArrayList<ArrayList<Integer>> riskRegions, int depth, int alpha, int beta, boolean isMaximizing) throws CloneNotSupportedException {
        Integer eval = getStaticEvaluation(board, depth);
        if (eval != null) return eval;
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = (ReversiBoard) board.clone();
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
                ReversiBoard cloneBoard = (ReversiBoard) board.clone();
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
