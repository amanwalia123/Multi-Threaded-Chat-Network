//import java.util.Queue
//import java.util.LinkedList;
public class PeerClient extends Client implements Runnable {

//Queue<String> messages = null;
    Thread t = null;
    //PeerNode reference to remove the  host connection from table
    PeerNode ref = null;

    public PeerClient(String hostName, int port,PeerNode ref ){
        super(hostName,port);
        this.ref =  ref;
    }

    public void run() {
        while(true){
            String message = this.readFromPeer();
            if(message == null) {
                ref.closeConnection(host,false);
                stopClient();

            } else if(message.matches("P2P-DISCONNECT-NOW")){
                ref.closeConnection(host,false);
                stopClient();
            }
            else
                System.out.print(host+":"+message);
        }
    }

    public void stopClient(){
        ref.connections.remove(this.host);
        t.stop();
        closePeerConnection();
    }

}