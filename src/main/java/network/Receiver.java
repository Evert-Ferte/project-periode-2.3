package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver extends Thread  {

    BufferedReader receiver;
    String response;

    public Receiver (Socket socket) throws IOException {
        receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            receive();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void receive () throws IOException {
        while (true) {

            response = receiver.readLine();
            if (response.startsWith("ERR")) {Handler.errHandler (response);}
            else if (response.startsWith("SVR HELP")) {Handler.helpHandler (response);}
            else if (response.startsWith("SVR GAME MATCH")) {Handler.gameMatchHalndler (response);}
            else if (response.startsWith("SVR GAME YOURTURN")) {Handler.turnHalndler (response);}
            else if (response.startsWith("SVR GAME WIN")) {Handler.winHalndler (response);}
            else if (response.startsWith("SVR GAME LOSS")) {Handler.lossHalndler (response);}
            else if (response.startsWith("SVR PLAYERLIST")) {Handler.playerlistHalndler (response);}
            else if (response.startsWith("SVR GAME CHALLENGE")) {Handler.gameChallengeHalndler (response);}
            else if (response.startsWith("SVR GAME DRAW")) {Handler.gameDrawHalndler (response);}

        }
    }
}
