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



public class ConcServer {

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
    private static ClientHandler[] threads = null;

    //Concurrent Map to hold client address and the respective socket
    private static ConcurrentMap<String,MyPair<Socket,Integer>> clients = null;



    /************************ Addition of client to server**************************/
    // add a concurrrent map data structure to store <User Name, socket for that name>,whenever a client is added , it is assigned
    // 1. a thread
    // 2. place on concurrent map

    /************************ Removal of client from server**************************/
    //When a thread leaves , its assigned thread is put to null , it is removed from concurrent map and socket for it is closed with respective Sockets.

    //Constructor to initialize Concurrent server class
    public ConcServer(int port, int maxThreads) {

        portNum = port;
        listening = true;

        try
        {
            serverSocket = new ServerSocket(portNum);

        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        clients = new ConcurrentHashMap<String, MyPair<Socket,Integer>>();
        this.maxClientsCount = maxThreads;
        threads = new ClientHandler[maxClientsCount];
    }

    //Run the Server using this method
    public void runServer() {

        while (listening) {
            try {
                //Accept connection from client socket
                clientSocket = serverSocket.accept();
                int i = 0;
                synchronized (this){
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new ClientHandler(clientSocket,clients,threads,maxClientsCount)).start();
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

            } catch (IOException e)
            {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Concurrent Server");

        if (args.length != 2) {
            System.err.println("Usage: java ConcServer <port number> <No. of Threads> ");
            System.exit(1);
        }
        //Read port number and number of client threads
        int portNumber = Integer.parseInt(args[0]);
        int numThreads = Integer.parseInt(args[1]);

        System.out.print("Server starting with Port: "+portNumber+" Maximum Number of Client Threads: "+numThreads);
        ConcServer server = new ConcServer(portNumber,numThreads);

        //Run the Server
        server.runServer();


    }

}
