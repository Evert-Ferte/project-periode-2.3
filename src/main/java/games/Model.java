package games;

public abstract class Model {
    protected int mapSize;


    protected static final int minAiMoveDelay = 10;//180;
    protected static final int maxAiMoveDelay = 20;//800;

    protected GameMode gameMode = GameMode.PLAYER_VS_PLAYER;

    protected String clientName = "The Paper (d5)";


    protected abstract void startApplication();

    protected abstract void gameStart(GameMode mode);

    protected abstract void gameEnd(boolean won) ;

    protected abstract void onGameWon();

    protected abstract void onGameLost() ;

    protected abstract void forfeitGame();

    protected abstract void resetVariables();

    protected abstract void turnHandler();

    protected abstract void turnStart();

    protected abstract void turnEnd();

    protected abstract void updateView();

    protected abstract void AiMove();

    protected abstract void clickPosition(int x, int y);

    public void setGameMode(GameMode mode) {
        gameMode = mode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI,
        ONLINE
    }

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
