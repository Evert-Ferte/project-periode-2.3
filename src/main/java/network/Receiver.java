package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver extends Thread  {

    BufferedReader receiver;
    String response;
    Handler handler;
    
    public Receiver (Socket socket, Handler handler) throws IOException {
        receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.handler = handler;
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
