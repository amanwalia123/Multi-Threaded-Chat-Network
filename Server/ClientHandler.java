
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

class ClientHandler extends Thread {

    //Name of the client connected
    private String clientName = null;

    //DataStream for Input/Output
    private DataInputStream in = null;
    private PrintStream out = null;

    //Socket for client connected to this thread object
    private Socket clientSocket = null;

    //Data Structure to keep track of all the connected clients
    private ConcurrentMap<String, MyPair<Socket, Integer>> connectedClients = null;

    //Connected Client threads
    private static ClientHandler[] clientThreads = null;

    //Maximum Number of clients that can be connected
    private int maxClientsCount;

    //Constructor for Client Handler
    public ClientHandler(Socket _clientSocket, ConcurrentMap<String, MyPair<Socket, Integer>> clients, ClientHandler[] threads,
                         int maxClientsCount) {
        this.clientSocket = _clientSocket;
        this.connectedClients = clients;
        this.clientThreads = threads;
        this.maxClientsCount = maxClientsCount;
    }

    public void run() {
        try {
                /*
                * Create input and output streams for this client.
                */
            in = new DataInputStream(clientSocket.getInputStream());
            out = new PrintStream(clientSocket.getOutputStream());

            while (true) {
                //Reading input from Client Side
                String input = in.readLine();
                String resp = null;
                String name = null;
                String peerServerport = null;
                String peerName = null;


                if (input != null) {
                    System.out.print(
                            "\nReceived " + input + " from  " + clientSocket.getInetAddress().toString().substring(1));
                    if (input.matches("JOIN")) {
                        if (this.clientName == null) {
                            out.println("Enter a unique name,port to identify yourself");
                            resp = in.readLine();

                            while ((resp == null) || (resp.isEmpty())) {
                                out.println("Name,Port cannot be null or Empty!");
                                resp = in.readLine();
                            }
                            name = resp.split(",")[0];
                            peerServerport = resp.split(",")[1];

                            while (connectedClients.containsKey(name) || (name == null) || (name.isEmpty())) {
                                if ((name == null) || (name.isEmpty())) {
                                    out.println("Name cannot be null or Empty!,Enter a unique name,port number");
                                } else
                                    out.println("Name exists in database,Enter a unique name,number");
                                resp = in.readLine();
                                name = resp.split(",")[0];
                                peerServerport = resp.split(",")[1];
                            }

                            int port = Integer.parseInt(peerServerport);

                            MyPair peerInfo = new MyPair(clientSocket, peerServerport);


                            //Add the client socket and client name to concurrent map
                            connectedClients.put(name, peerInfo);
                            this.clientName = name;
                            //Check if client has been successfully added or not
                            if (connectedClients.containsKey(name))
                                out.println("Client Successfully connected to server by name: " + this.clientName);
                            else
                                out.println("Unable to connect client to server due to unknown reasons");
                        } else
                            out.println("You are already connected!!");

                    } else if (input.matches("LEAVE")) {
                        //close the socket for this client and put thread to null
                        if (this.clientName != null) {
                            if (connectedClients.containsKey(this.clientName)) {
                                connectedClients.remove(this.clientName);
                                break;
                            }
                        } else {
                            out.println("You have to join first");
                        }

                    } else if (input.matches("LIST")) {

                        if (this.clientName != null) {

                            if (connectedClients.containsKey(this.clientName)) {
                                StringBuilder response = new StringBuilder("List of Clients Connected: ");
                                int num = 1;
                                //append IP address of clients to this string
                                for (String names : connectedClients.keySet()) {
                                    response.append(" " + num + "." + names + "("
                                            + connectedClients.get(names).getFirst().getInetAddress().toString().substring(1)
                                            + ",Port# "
                                            + connectedClients.get(names).getSecond()
                                            + ")");
                                    num++;
                                }
                                out.println(response.toString());
                            }
                        } else
                            out.println("Unable to list connected clients, You have to JOIN first");
                    } else if (input.matches("P2P CONNECT .*")) {

                        if (this.clientName != null) {

                            if (connectedClients.containsKey(this.clientName)) {
                                peerName = input.split(" ")[2];
                                if (connectedClients.containsKey(peerName)) {
                                    if (this.clientName.matches(peerName)) {
                                        out.println("ERROR: Illegal connecting to yourself!!");
                                    } else {

                                        StringBuilder peerInfo = new StringBuilder();
                                        peerInfo.append("FOUND: ");
                                        peerInfo.append(connectedClients.get(peerName).getFirst().getInetAddress().toString().substring(1));
                                        peerInfo.append(",");
                                        peerInfo.append(connectedClients.get(peerName).getSecond());
                                        out.println(peerInfo);
                                    }

                                } else {
                                    out.println("ERROR: No such peer exists!!");
                                }
                            }
                        } else
                            out.println("Unable to establish P2P connetion to clients, You have to JOIN first");
                    } else {
                        String usage = "Illegal Input." + "Usage as follows: " + "JOIN : Join the server,"
                                + "LIST : List the connected clients," + "LEAVE: Leave the server";
                        out.println(usage);

                    }
                }
            }
            try {
                //Make this thread empty to be used by other clients
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (clientThreads[i] == this) {
                            clientThreads[i] = null;
                        }
                    }
                }
                //Close input and output stream and close socket
                this.out.close();
                this.in.close();
                this.clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing the socket and streams");
            }
        } catch (IOException e) {

            System.out.println(e.getMessage());
            System.exit(-1);

        }
    }

    public String getClientName() {
        String name = new String(this.clientName);
        return name;
    }

}