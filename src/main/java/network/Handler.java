package network;

import games.Game;
import games.reversi.ReversiModel;

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

    public static String gameMatchHalndler (String response) {
        inMatch = true;
    
        System.out.println("start a new game");
        game.startMatch();
        
        return "newGame";
    }

    public static void gameMoveHalndler(String response) {
        HashMap<String, String> map = responseToMap(response);
        String player = map.get("PLAYER");
        String move = map.get("MOVE");
        //TODO Send the details move to the game board
    }

    public static void turnHalndler(String response) {
        System.out.println("your turn...");
        //TODO ask AI to make a move
    }

    public static void winHalndler(String response) {
        System.out.println("game won");
        //TODO send win sginal to reversi
    }

    public static void lossHalndler(String response) {
        System.out.println("game lost");
        //TODO send loss signal to reversi
    }

    public static void playerlistHalndler(String response) {
        response = stringCleaner(response);
        String[] responseArray = toArray(response);
        playerlist = responseArray;
    }
    
    /**
     * When receiving a challenge, this handler is called.
     * @param response the response message.
     */
    public static void gameChallengeHalndler(String response) {
        if (inMatch) return;
        
        Map<String, String> result = responseToMap(response);
        
        String challenger = result.get("CHALLENGER");
        String challengeNr = result.get("CHALLENGENUMBER");
        
        System.out.println("challenge received("+challengeNr+"), from "+challenger+"");
        
        game.challengeReceived(challenger, challengeNr);
    }

    public static void gameDrawHalndler(String response) { }

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
