/*
 * Assignment 1
 * Name : Amanpreet S. Walia
 * Course : EECS-3214
 * Prism Account : apsw02
 */

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

class PeerClientHandler extends Thread {

    //DataStream for Input/Output
    private DataInputStream in = null;
    private PrintStream out = null;

    //Socket for client connected to this thread object
    private Socket clientSocket = null;

    //Data Structure to keep track of all the connected clients
    private ConcurrentMap<String, Socket> connectedClients = null;

    //Connected Client threads
    private static PeerClientHandler[] clientThreads = null;

    //Maximum Number of clients that can be connected
    private int maxClientsCount;

    private boolean loopRun;

    //Constructor for Client Handler
    public PeerClientHandler(Socket _clientSocket, ConcurrentMap<String, Socket> clients, PeerClientHandler[] threads,
                             int maxClientsCount) throws IOException {
        this.clientSocket = _clientSocket;
        this.connectedClients = clients;
        this.clientThreads = threads;
        this.maxClientsCount = maxClientsCount;
        in = new DataInputStream(clientSocket.getInputStream());
        out = new PrintStream(clientSocket.getOutputStream());
        loopRun = true;
    }

    //Always Listening for response ,as soon as it gets response , it displays it on screen
    public void run() {
        while (loopRun) {
            //Reading input from Client Side
            try {
                String input = in.readLine();
                if (input != null) {
                    if(input.matches("P2P-DISCONNECT-NOW")){
                        this.closeConnection();
                        break;
                    }
                    else{
                        System.out.print(clientSocket.getInetAddress().toString().substring(1)+": " +input );
                    }

                }

            } catch (IOException e) {
                System.out.print("");
            }
        }

    }

    //Send message on the this socket
    public void sendMessage(String message) {
        out.println(message);
    }

    //close input , output and this socket , set this handler to null so that it can be reused
    public void closeConnection() {

        connectedClients.remove(this.clientSocket.getInetAddress().toString().substring(1));
        loopRun = false;
        try {
            //Make this thread empty to be used by other clients
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (clientThreads[i] == this) {
                        clientThreads[i] = null;
                    }
                }
            }

            this.clientSocket.close();
            //Close input and output stream and close socket
            this.out.close();
            this.in.close();

        } catch (IOException ex) {
            System.out.println("Error closing the socket and streams");
        }
    }

    public String getClientName() {
        String name = new String(clientSocket.getInetAddress().toString().substring(1));
        return name;
    }

}
