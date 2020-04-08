package network;

import games.Game;
import games.reversi.ReversiModel;
import games.reversi.Vector2;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Handler {

    //private static MultiPlayerGUI gui;

    public static String[] playerlist;
    static String opponent;
    static String gameName;
    
    private static boolean inMatch = false;

//    static Sender sender = new Sender(Connection.getConnection().getSocket());
    private static ReversiModel game;

    public Handler(ReversiModel game) {
        this.game = game;
    }

    public static String errHandler (String toHandle) {
        return toHandle;
    }

    public static String helpHandler (String toHandle) {
        return "For help information checkout Protocol.txt on Blackboard";
    }

    public static String gameMatchHandler(String response) {
        inMatch = true;
    
        System.out.println("start a new game");
        
        game.softReset();
        game.setOnlineMatch(true);
        game.startMatch();
        
        return "newGame";
    }

    public static void gameMoveHandler(String response) {
        HashMap<String, String> map = responseToMap(response);
        String player = map.get("PLAYER");
        String move = map.get("MOVE");
        System.out.println("opponent("+player+") moved("+move+")");
        
        if (!player.equals(game.getName())) {
            
            Vector2 pos = game.convertIndexToPosition(Integer.parseInt(move));
            game.clickPosition((int)pos.x, (int)pos.y);
        }
        //TODO Send the details move to the game board
    }

    public static void turnHandler(String response) {
        System.out.println("your turn...");
        //TODO ask AI to make a move
    }

    public static void winHandler(String response) {
        System.out.println("game won");
        game.gameWon(true);
        //TODO send win sginal to reversi
    }

    public static void lossHandler(String response) {
        System.out.println("game lost");
        game.gameWon(false);
        //TODO send loss signal to reversi
    }

    public static void playerlistHandler(String response) {
        response = stringCleaner(response);
        String[] responseArray = toArray(response);
        playerlist = responseArray;
    }
    
    /**
     * When receiving a challenge, this handler is called.
     * @param response the response message.
     */
    public static void gameChallengeHandler(String response) {
        if (inMatch) return;
        
        Map<String, String> result = responseToMap(response);
        
        String challenger = result.get("CHALLENGER");
        String challengeNr = result.get("CHALLENGENUMBER");
        
        System.out.println("challenge received("+challengeNr+"), from "+challenger+"");
        
        game.challengeReceived(challenger, challengeNr);
    }

    public static void gameDrawHandler(String response) { }

    private static String stringCleaner(String dirty) {
        dirty = dirty.substring(dirty.indexOf("[") + 1,dirty.indexOf("]"));
        dirty = dirty.replace("\"","");
        dirty = dirty.replace(",","");
        return dirty;
    }
    public static String[] toArray (String string) {
        String[] anArray = string.split(" ");
        return anArray;
    }

    private static HashMap<String, String> responseToMap(String response) {
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
