/**
 * Name : Amanpreet Walia
 * Course : EECS 3214
 * Prism Account : apsw02
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class PeerServer extends Thread{

    //Port number assigned for connection
    int portNum;

    //Server socket object to open socket for server class
    private static ServerSocket serverSocket = null;

    // The client socket.
    private static Socket clientSocket = null;

    //Variable to make server on/Off
    private boolean listening;

    // This server can accept up to maxClientsCount clients' connections.
    private static int maxClientsCount;
    private static PeerClientHandler[] threads = null;

    //Concurrent Map to hold client address and the respective socket
    private static ConcurrentMap<String, Socket> clients = null;


    /************************ Addition of client to server**************************/
    // add a concurrrent map data structure to store <User Name, socket for that name>,whenever a client is added , it is assigned
    // 1. a thread
    // 2. place on concurrent map

    /************************ Removal of client from server**************************/
    //When a thread leaves , its assigned thread is put to null , it is removed from concurrent map and socket for it is closed with respective Sockets.

    //Constructor to initialize Concurrent server class
    public PeerServer(int port, int maxThreads, ConcurrentHashMap<String, Socket> connections) {

        portNum = port;
        listening = true;

        try {
            serverSocket = new ServerSocket(portNum);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        clients = connections;
        this.maxClientsCount = maxThreads;
        threads = new PeerClientHandler[maxClientsCount];
    }

    //Run the Server using this method
    public void run() {

        while (listening) {
            try {
                //Accept connection from client socket
                clientSocket = serverSocket.accept();
                int i = 0;
                synchronized (this) {
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            //Add the client socket and client name to concurrent map
                            clients.put(clientSocket.getInetAddress().toString().substring(1), clientSocket);
                            (threads[i] = new PeerClientHandler(clientSocket, clients, threads, maxClientsCount)).start();
                            break;
                        }
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy.Try later.");
                    os.close();
                    clientSocket.close();
                }

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public PeerClientHandler getHandler(String hostName) {
        for (int i = 0; i < maxClientsCount; i++) {
            if (this.threads[i].getClientName().matches(hostName)) {
                return this.threads[i];
            }
        }
        return null;
    }


}
