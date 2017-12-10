# ChatServer
This project implements distributed system messaging system in Java where multiple clients can connect to central known server and initiate peer to peer messaging connection after getting network information of other users.

## Getting Started
Download or clone this repository to your local system.
### Prerequisites
You need to have java installed on your machine.
### Installing
Download this project to your machine.
Move to the folder using
```
cd Multi-Threaded-Chat-Network
```
#### Compiling & running Centralized Server Side
```
cd Server
javac ConcServer.java ClientHandler.java MyPair.java

```
Run it using
```
java ConcServer <Port Number> <No. of threads for Clients>
```
#### Compiling & running Peer Side

```
cd Peer
javac Client.java MainClient.java PeerClient.java PeerClientHandler.java
PeerNode.java PeerServer.java
```
Run it using
```
java MainClient <centralized Server IP Address> <Local ServerIP Address>
```

### Working

Following messaging protocol is implemented:
```
JOIN : Join the centralized Server for Discovery. Followed by asking for a unique name
LIST : List the available peers connected to centralized Server
LEAVE : Disconnect from centralized server as well as all connected peers
P2P CONNECT nickname: Connect to peer on centralized server with corresponding nickname assigned.
P2P SEND<HOST IP ADRESS>: message: Send message to the respective peer on host IP address, this will fail
if peer connection is not established
P2P DISCONNECT <HOST IP ADRESS>: Disconnect from the peer, this will fail if there is no connection to peer.
```
Refer to [Documentation](Report.pdf) for more information.
## Authors
* **Amanpreet Walia** 
## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

