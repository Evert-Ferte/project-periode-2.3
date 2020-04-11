package games.reversi;

import java.security.cert.CertificateNotYetValidException;

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

    static Vector2 aiMiniMax(ReversiBoard board, int depth, boolean player){
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;
        ReversiBoard cloneBoard;
        System.out.println(board.getModelMap());
        for (Vector2 position : board.getAvailablePositions()) {
           cloneBoard = board.clone();
            //System.out.println("Board cloned");
           // System.out.println(board.toString()+ "\n");
            cloneBoard.move((int)position.x, (int)position.y);
            cloneBoard.switchTurn();
            int score = miniMax(cloneBoard, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, player);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        System.out.println(bestMove);
        System.out.println(board.getModelMap());
        if(bestMove != null) {
            return new Vector2((int) bestMove.x, (int) bestMove.y);
        } else{
            System.out.println("WARING! No position found with minimax!");
        }
        return aiRandom(board);
    }

    private static int miniMax(ReversiBoard board, int depth, int alpha, int beta, boolean isMaximizing){
        //System.out.println("-------cloneBoard-------\n"+ board.toString()+ "\n------------------------");
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
        int optimumScore;
        if (isMaximizing){
            optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                //System.out.println("cloned");
                //System.out.println(position.toString());
                cloneBoard.switchTurn();
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();

                int score = miniMax(cloneBoard,depth-1, alpha, beta, false);
                optimumScore = Integer.max(score, optimumScore);
                alpha = Integer.max(alpha, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
            //System.out.println("maxi: "+ optimumScore);
        }
        else{
            optimumScore = Integer.MAX_VALUE;
            for (Vector2 position : board.getAvailablePositions()) {
                ReversiBoard cloneBoard = board.clone();
                //System.out.println("cloned");
                cloneBoard.move((int)position.x, (int)position.y);
                cloneBoard.switchTurn();
                int score = miniMax(cloneBoard, depth-1, alpha, beta,true);
                optimumScore = Integer.min(score, optimumScore);
                beta = Integer.min(beta, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
            //System.out.println("mini: " + optimumScore);
        }
        return optimumScore;
    }
}
