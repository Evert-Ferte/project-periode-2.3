package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * This class receives the server response and then passes it to the Handler to be handled
 *
 * @author Zein Bseis
 * @version 3.0
 */
public class Receiver extends Thread  {

    BufferedReader receiver;
    String response;
    Handler handler;

    /**
     * Make a new receiver to be able to start receiving.
     *
     * @param socket to monitor and wait unit a response is received.
     * @param handler to handle received responses.
     * @throws IOException in case socket couldn't connect to the server
     */
    public Receiver (Socket socket, Handler handler) throws IOException {
        receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.handler = handler;
    }

    /**
     * Run receive on its own thread
     */
    @Override
    public void run() {
        try {
            receive();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * with an infinite while loop it, this method reads the server response constancy looking for
     * responses that could be handled by the client
     *
     * @throws IOException in case reading from the socket wasn't successful
     */
    private void receive () throws IOException {
        while (true) {

            response = receiver.readLine();
            if (response.startsWith("ERR")) {handler.errHandler (response);}
            else if (response.startsWith("SVR HELP")) {handler.helpHandler ();}
            else if (response.startsWith("SVR GAME MATCH")) {handler.gameMatchHandler();}
            else if (response.startsWith("SVR GAME YOURTURN")) {handler.turnHandler();}
            else if (response.startsWith("SVR GAME WIN")) {handler.winHandler();}
            else if (response.startsWith("SVR GAME LOSS")) {handler.lossHandler();}
            else if (response.startsWith("SVR PLAYERLIST")) {handler.playerlistHandler(response);}
            else if (response.startsWith("SVR GAME CHALLENGE")) {handler.gameChallengeHandler(response);}
            else if (response.startsWith("SVR GAME DRAW")) {handler.gameDrawHandler(response);}
            else if (response.startsWith("SVR GAME MOVE")) {handler.gameMoveHandler(response);}

        }
    }
}
