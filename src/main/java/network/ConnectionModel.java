package network;

import java.io.IOException;
import java.net.Socket;

/**
 * This class makes a connections instance which enables the client to
 * connect to the server.
 * the type of server to connect to (Local or remote) is decided through the IP address.
 *
 * @author Zein Bseis
 * @version 3.0
 *
 */
public class ConnectionModel {
    private String ip;
    private int port;
    private Socket socket;

    /**
     * Make new connection instance.
     *
     * @param ip The IP address of the server which the client has to connect to.
     * @param port The Port associated with the IP address.
     * @exception IOException if the the connection with the server couldn't be established.
     */
    public ConnectionModel (String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.socket = new Socket(ip,port);
        }
        catch (IOException ex) { System.err.println(ex.getMessage());}
    }

    /**
     * Get the Ip address of the server.
     *
     * @return ip address of the server.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Change the IP address of a specific connection instance.
     * This method only exists for testing purposes.
     *
     * @param ip New IP address
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get the Port number for the connection instance.
     * This method only exists for testing purposes.
     *
     * @return Port number of the server
     */
    public int getPort() {
        return port;
    }

    /**
     * Change the port number for a connection instance.
     * This method only exists for testing purposes.
     *
     * @param port new Port number.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * After the connection is established information can be exchanged between the server
     * and the client through a socket.
     *
     * @return A Socket with an established connection.
     */
    public Socket getSocket() {return socket;}

    /**
     * Tests the connection status through testing the socket status, and returns true
     * if the connection was established successfully.
     *
     * @return Boolean indicating the status of the connection.
     */
    public boolean isConnected() {
        if (socket == null) return false;
        return socket.isConnected();
    }

    /**
     * Terminate the connection with the server by closing the socket.
     */
    public void terminate() {
        // Return if not connected
        if (!isConnected()) return;
        
        // Close the socket connection
        try {
            socket.close();
        } catch (IOException ignored) { }
    }
}
