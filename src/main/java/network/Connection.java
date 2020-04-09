package network;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private String ip;
    private int port;
    private Socket socket;
    
    public Connection (String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.socket = new Socket(ip,port);
        }
        catch (IOException ex) { System.err.println(ex.getMessage());}
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {return socket;}
    
    public boolean isConnected() {
        if (socket == null) return false;
        return socket.isConnected();
    }
}
