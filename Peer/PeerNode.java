
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PeerNode {

    /* Server half of peer node , used for accepting connections
     * Always in running mode with starting of corresponding peer Node
     */
    PeerServer server = null;
    /*
    * Clients half of peer node,
    * when the main client application(corresponding to this)
    * wants to connect to any server,a TCP connection
    * is created and the corresponding socket is added to this map
    */
    // Map <IP Address,Clients Objects >
    Map<String, PeerClient> conn_clients = null;
    /*
     Map containing IP address and Socket connections both client sockets and server sockets
     */
    ConcurrentHashMap<String, Socket> connections = null;

    public PeerNode(int serverPort){
        conn_clients = new HashMap<String,PeerClient>();
        connections = new ConcurrentHashMap<String,Socket>();
        server =      new PeerServer(serverPort,10,connections);
        server.start();
    }

    /*
     * This method is used to connect this peer with other peers, a TCP connection is setup between this peerNode(acting as Client)
     * and other as Server, if a connection exists from other side already , this will not do anything
     */
    public void addConnection(String host, int port) {
        if(!conn_clients.containsKey(host)&&(!connections.containsKey(host))){
            PeerClient client = new PeerClient(host, port,this);
            client.t = new Thread(client);
            client.t.start();
            conn_clients.put(host,client);
            System.out.println("Successfully connected with client: "+ host+" on port: "+port);
        }
        else{
            System.out.println("Error connection already exists, Illegal reconnecting!!");
        }
    }

    /*
     * This method sends message to the respictive IP address, it automatically determines in which way peer is connected(as Client or Server)
     * and use it to convey the message
     */
    public void sendMessage(String hostName, String message) {
        //This peer is connected as Server to the corresponding peer
        if(conn_clients.containsKey(hostName)){
            PeerClient toMesClient = conn_clients.get(hostName);
            toMesClient.writeToPeer(message);
        }
        //This peer is connected as Client to the corresponding peer
        else if(connections.containsKey(hostName)){

            // Do the server sending stuff here
            server.getHandler(hostName).sendMessage(message);
        }
        else{
            System.out.println("Error: No such connection exists with the hostname");
        }
    }
    /*
     * This method close connection from the peer side calling it according to the way peer is connected
     */
    public void closeConnection(String hostName,boolean recursion){
        if(conn_clients.containsKey(hostName)){
            PeerClient cl = conn_clients.remove(hostName);
            if (recursion) {
                cl.writeToPeer("P2P-DISCONNECT-NOW");
            }
            cl.stopClient();
        }
        //This peer is connected as Client to the corresponding peer
        else if(connections.containsKey(hostName)){
            server.getHandler(hostName).closeConnection();
        }
        else{
            System.out.println("Error: No such connection exists with the hostname");
        }
    }

    public void closeEveryThing(){
        for(String key :conn_clients.keySet()){
            closeConnection(key,false);
        }
        for (String key:connections.keySet()){
            closeConnection(key,false);
        }

    }
}
