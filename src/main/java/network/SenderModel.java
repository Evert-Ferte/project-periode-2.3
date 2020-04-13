package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Sends commands to the server in the form of plain text through a specific socket
 * it uses a PrintWriter that flushes output automatically
 *
 * @author Zein Bseis
 * @version 3.0
 */
public class SenderModel {

    private PrintWriter sender;

    /**
     * make a new Sender instance
     *
     * @param socket to send information on.
     * @exception IOException in case command couldn't be send to the server successfully
     */
    public SenderModel (Socket socket) {
        try {
            sender = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e) {System.err.println(e.getMessage());}
    }

    /**
     * Login to the server using a specific username or player name
     *
     * @param playerName to login to the server with.
     */
    public void login (String playerName) { sender.println("login "+playerName); }

    /**
     * get the list of available games on the server
     */
    public void getGameList () {sender.println(("get gamelist")); }

    /**
     * get the list of available player on the server
     */
    public void getPlayerlist () { sender.println("get playerlist"); }

    /**
     * to challenge a specific player on the server using their player name
     *
     * @param challengerName to challenge
     * @param game which game you would like to challenge the player in
     */
    public void challenge (String challengerName, String game) { sender.println("challenge " + "\"" + challengerName + "\"" + " " + "\"" + game + "\""); }

    /**
     * Accept a challenge coming from another player
     *
     * @param challengeNumber which you want to accept
     */
    public void acceptAChallenge(String challengeNumber){ sender.println("challenge accept " + challengeNumber); }

    /**
     * ask the server for available commands
     */
    public void listHelp(){ sender.println("help"); }

    /**
     * forfeit a game
     *
     */
    public void forfeitAGame(){ sender.println("forfeit"); }

    /**
     * subscribe to a game which means you will be automatically put in a match
     * with anyone also subscribed to the same game
     *
     * @param game which you want to subscribe to
     */
    public void subscribeGame(String game) { sender.println("subscribe " + game); }

    /**
     * send a move to the server
     *
     * @param move an integer that indicates the details of your move
     */
    public void sendMove(int move){ sender.println("move " + move); }

}
