package games.reversi;

class Ai {

    static Vector2 aiRandom(ReversiBoard game){
        // TODO - Check if this if statement is needed. AI can be either black or white
//        if(game.isWhiteTurn()){
//        }
        
        Vector2[] avPos = game.getAvailablePositions();
        
        if (avPos.length > 0) {
            Vector2 randomPosition = avPos[(int)(Math.random() * avPos.length)];
            return new Vector2(randomPosition.x, randomPosition.y);
        }
        else {
            System.out.println("WARNING! No available positions for AI to choose from!");
        }
        return null;
    }
/*
    static void aiMiniMax(ReversiModel game, String player, int depth) throws CloneNotSupportedException{
//        if(!originalGame.isWhiteTurn()){
//            return;
//        }
        
        int optimumScore = Integer.MIN_VALUE;
        Vector2 bestMove = null;
        for (Vector2 position : game.getAvailablePositions(game.getModelMap())) {
            ReversiModel cloneGame = (ReversiModel) originalGame.clone();
            System.out.println("cloned");
            cloneGame.clickPosition((int)position.x, (int)position.y);
            int score = miniMax(cloneGame, player, depth,Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > optimumScore) {
                optimumScore = score;
                bestMove = position;
            }
        }
        System.out.println(bestMove);
        originalGame.clickPosition((int)bestMove.x, (int)bestMove.y);
    }

    private static int miniMax(ReversiModel game, String player, int depth, int alpha, int beta, boolean isMaximizing)throws CloneNotSupportedException{
        if(depth == 0 || game.isGameFinished()) {
            if (player.equals("White")) {
                if(game.getScoreWhite() > game.getScoreBlack()){
                    return 1;
                }else{
                    return -1;
                }
            }
            else if (player.equals("Black")) {
                if(game.getScoreBlack() > game.getScoreWhite()) {
                    return 1;
                }else{
                    return -1;
                }
            }else{
                return 0;
            }
        }
        if (isMaximizing){
            int optimumScore = Integer.MIN_VALUE;
            for (Vector2 position : game.getAvailablePositions()) {
                ReversiModel cloneGame = (ReversiModel) game.clone();
                System.out.println("cloned");
                cloneGame.clickPosition((int)position.x, (int)position.y);
                int score = miniMax(cloneGame, player, depth-1, alpha, beta, false);
                optimumScore = Integer.max(score, optimumScore);
                alpha = Integer.max(alpha, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
            System.out.println("maxi: "+ optimumScore);
            return optimumScore;
        }
        else{
            int optimumScore = Integer.MAX_VALUE;
            for (Vector2 position : game.getAvailablePositions()) {
                ReversiModel cloneGame = (ReversiModel) game.clone();
                System.out.println("cloned");
                cloneGame.clickPosition((int)position.x, (int)position.y);
                int score = miniMax(cloneGame, player,depth-1, alpha, beta,true);
                optimumScore = Integer.min(score, optimumScore);
                beta = Integer.min(beta, optimumScore);
                if(beta <= alpha){
                    break;
                }
            }
            System.out.println("mini: " + optimumScore);
            return optimumScore;
        }
    }

 */
}
