package networkprojectphase2;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> waitingRoomPlayers;
    private static final int MAX_PLAYERS = 3;
    private int clickCount = 0;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients, ArrayList<String> waitingRoomPlayers) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.waitingRoomPlayers = waitingRoomPlayers;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

@Override
public void run() {
    try {
        out.println("Enter your username:");
        username = in.readLine();
        broadcastUsernames();
        GameServer.broadcastWaitingRoom(this);

        while (true) {
            String msg = in.readLine();
            if (msg == null) {
                System.out.println(username + " disconnected.");
                break;
            } else if (msg.equalsIgnoreCase("play")) {
                synchronized (waitingRoomPlayers) {
                    if (!waitingRoomPlayers.contains(username) && waitingRoomPlayers.size() < MAX_PLAYERS) {
                        waitingRoomPlayers.add(username);
                        GameServer.broadcastWaitingRoom(this);
                        GameServer.checkForGameStart();
                    }
                }
            } else if (msg.equalsIgnoreCase("exit")) {
                System.out.println(username + " is exiting the game.");
                handlePlayerExit(); // Handle the exit properly
                break; // End the connection for this client
            } else if (msg.startsWith("CLICK")) {
                GameServer.registerClick(username);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            socket.close();
            clients.remove(this);
            synchronized (waitingRoomPlayers) {
                waitingRoomPlayers.remove(username);
            }
            GameServer.broadcastWaitingRoom(this);
            broadcastUsernames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


 public void handlePlayerExit() {
    synchronized (waitingRoomPlayers) {
        waitingRoomPlayers.remove(username);
        GameServer.playerExit(username); 
    }
    broadcastUsernames(); 
}

    public void updateClickCount(int count) {
        this.clickCount = count;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void resetClickCount() {
        this.clickCount = 0;
    }

    public String getUsername() {
        return username;
    }

    private void broadcastUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        for (ClientHandler client : clients) {
            client.out.println("CONNECTED_USERS:" + String.join(",", usernames));
        }
    }

    public void sendWaitingRoomUpdate(String[] players) {
        out.println("WAITING_ROOM_PLAYERS:" + String.join(",", players));
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
