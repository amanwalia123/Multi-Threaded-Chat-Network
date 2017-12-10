import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class Client {

    //Host Name of the connected Server
    String host = null;
    //Client socket to connect to respective server
    Socket clientSocket = null;
    //Reader to read the input from server connection
    BufferedReader in = null;
    //Writer to write to server connection
    PrintWriter out = null;
    //When server disconnects from his side, peerNode reference is used remove that host from PeerNode
    PeerNode ref = null;

    // Read data from server side
    public String readFromPeer() {
        try {
            return in.readLine();
        } catch (IOException e) {

            System.out.print("");
        }

        return null;

    }

    // Write data to server side
    public void writeToPeer(String data) {

        out.println(data);
        flush();

    }

    public void flush() {
        out.flush();
    }

    public Client(String hostName, int portNum) {
        host = hostName;
        try {
            System.out.println("Host Name :"+hostName+" Port :"+portNum);
            clientSocket = new Socket(hostName, portNum);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out =  new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closePeerConnection(){
        try{
            //this.in.close();
            //this.out.close();
            this.clientSocket.close();

        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());


        }

    }

}