package network;

import games.reversi.ReversiModel;
import games.reversi.Vector2;
import java.util.HashMap;
import java.util.Map;

public class Handler {
    public String[] playerlist;
    private String opponent;
    private String gameName;
    
    private boolean inMatch = false;

//    Sender sender = new Sender(Connection.getConnection().getSocket());
    private ReversiModel game;

    public Handler(ReversiModel game) {
        this.game = game;
    }

    public String errHandler (String toHandle) {
        return toHandle;
    }

    public String helpHandler (String toHandle) {
        return "For help information checkout Protocol.txt on Blackboard";
    }

    public String gameMatchHandler(String response) {
        inMatch = true;
        
        game.log("starting...");
        game.gameStart(ReversiModel.GameMode.ONLINE);
        
        return "newGame";
    }

    public void gameMoveHandler(String response) {
        HashMap<String, String> map = responseToMap(response);
        String player = map.get("PLAYER");
        String move = map.get("MOVE");
        game.log("opponent("+player+") moved("+move+")");
        
        if (!player.equals(game.getClientName())) {
            Vector2 pos = game.convertIndexToPosition(Integer.parseInt(move));
            game.clickPosition((int)pos.x, (int)pos.y);
        }
    }

    public void turnHandler(String response) {
        game.log("your turn...");
        
        if (game.getGameMode() != ReversiModel.GameMode.ONLINE) return;
        
        game.turnStart();
        game.AiMove();
    }

    public void winHandler(String response) { game.gameEnd(true); }
    public void lossHandler(String response) { game.gameEnd(false); }

    public void playerlistHandler(String response) {
        response = stringCleaner(response);
        String[] responseArray = toArray(response);
        playerlist = responseArray;
    }
    
    /**
     * When receiving a challenge, this handler is called.
     * @param response the response message.
     */
    public void gameChallengeHandler(String response) {
        if (inMatch) return;
        
        Map<String, String> result = responseToMap(response);
        
        String challenger = result.get("CHALLENGER");
        String challengeNr = result.get("CHALLENGENUMBER");
        
        game.log("challenge received("+challengeNr+"), from "+challenger+"");
        
        game.acceptChallenge(challengeNr);
        
        game.challengeReceived(challenger, challengeNr);
    }

    public void gameDrawHandler(String response) { }

    private String stringCleaner(String dirty) {
        dirty = dirty.substring(dirty.indexOf("[") + 1,dirty.indexOf("]"));
        dirty = dirty.replace("\"","");
//        dirty = dirty.replace(",","");
        return dirty;
    }
    public String[] toArray (String string) {
        String[] anArray = string.split(",");
        return anArray;
    }

    private HashMap<String, String> responseToMap(String response) {
        String server_msg = response.substring(response.indexOf('{') + 1, response.indexOf('}'));
        HashMap<String, String> map = new HashMap<String, String>();
        String[] list = server_msg.split(",");
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
            String[] keyvalue = list[i].split(":");
            map.put(keyvalue[0].trim(), keyvalue[1].replace("\"", "").trim());
        }
        return map;
    }


}
