package network;

import java.util.HashMap;
import java.util.Map;

public class Handler {

    //private static MultiPlayerGUI gui;

    static String[] playerlist;
    static String opponent;
    static String game;

    static Sender sender = new Sender(Connection.getConnection().getSocket());




    public static String errHandler (String toHandle) {
        return toHandle;
    }

    public static String helpHandler (String toHandle) {
        return "For help information checkout Protocol.txt on Blackboard";
    }

    public static String gameMatchHalndler (String toHandle) {
        return "newGame";
    }

    public static void turnHalndler(String response) {
        //TODO ask AI to make a move
    }

    public static void winHalndler(String response) {
        //TODO send win sginal to reversi
    }

    public static void lossHalndler(String response) {
        //TODO send loss signal to reversi
    }

    public static void playerlistHalndler(String response) {
        response = stringCleaner(response);
        String[] responseArray = toArray(response);
        playerlist = responseArray;

    }


    public static void gameChallengeHalndler(String response) {
        Map<String, String> result = MessageToMap(response);

        sender.acceptAChallenge(result.get("CHALLENGENUMBER"));
    }

    public static void gameDrawHalndler(String response) {
    }

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

    private static HashMap<String, String> MessageToMap(String response) {
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
