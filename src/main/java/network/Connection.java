package network;

import java.io.IOException;
import java.net.Socket;

public class Connection {

    String ip;
    int port;
    Socket socket;
    static Connection connection;

    public Connection (String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.socket = new Socket(ip,port);
        }
        catch (IOException ex) { System.err.println(ex.getMessage());}

        connection = new Connection(ip,port);
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

    public static Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
