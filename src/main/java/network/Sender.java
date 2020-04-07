package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class Sender {

    private PrintWriter sender;

    public Sender (Socket socket) {
        try {
            sender = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e) {System.err.println(e.getMessage());}

    }

    public void login (String playerName) { sender.println("login "+playerName); }

    public void getGameList () {sender.println(("get gamelist")); }

    public void getPlayerlist () { sender.println("get playerlist"); }

    public void challenge (String challengerName, String game) { sender.println("challenge " + "\"" + challengerName + "\"" + " " + "\"" + game + "\""); }

    public void acceptAChallenge(String challengeNumber){ sender.println("challenge accept " + challengeNumber); }

    public void listHelp(){ sender.println("help"); }

    public void forfeitAGame(){ sender.println("forfeit"); }

    public void subscribeGame(String game) { sender.println("subscribe " + game); }

    public void sendMove(int move){ sender.println("move " + move); }

}
