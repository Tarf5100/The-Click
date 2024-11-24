package networkprojectphase2;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameServer {
    private static final int PORT = 9090;
    private static final int MAX_PLAYERS = 3;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ArrayList<String> waitingRoomPlayers = new ArrayList<>();
    private static Timer gameStartTimer;
    private static boolean gameStarted = false;
    private static Map<String, Integer> playerClicks = new HashMap<>();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean gameRunning = false;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port: " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, clients, waitingRoomPlayers);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    public static synchronized void broadcastWaitingRoom(ClientHandler initiatingClient) {
        String[] waitingPlayers = waitingRoomPlayers.toArray(new String[0]);
        System.out.println("Broadcasting waiting room players: " + Arrays.toString(waitingPlayers));

        for (ClientHandler client : clients) {
            client.sendWaitingRoomUpdate(waitingPlayers);
        }
        if (initiatingClient != null) {
            initiatingClient.sendWaitingRoomUpdate(waitingPlayers);
        }
    }

    public static synchronized void notifyPlayerLeft(String username) {
        waitingRoomPlayers.remove(username);
        broadcastWaitingRoom(null);

        System.out.println(username + " has left the game.");
        for (ClientHandler client : clients) {
            client.sendMessage("PLAYER_LEFT:" + username);
        }

        if (waitingRoomPlayers.size() < 2) {
            System.out.println("Not enough players to continue. Waiting for more players.");
        }
    }

    public static synchronized void startGame() {
        if (gameRunning || waitingRoomPlayers.size() < 2) return;

        gameRunning = true;
        gameStarted = true;

        String[] players = waitingRoomPlayers.toArray(new String[0]);
        broadcastMessage("START_GAME:" + String.join(",", players));

        System.out.println("Game started with players: " + Arrays.toString(players));
        
        scheduler.schedule(GameServer::endGame, 15 , java.util.concurrent.TimeUnit.SECONDS);
    }

    public static synchronized void checkForGameStart() {
        if (waitingRoomPlayers.size() == 3 && !gameStarted) {
            startGame(); 
        } else if (waitingRoomPlayers.size() == 2 && !gameStarted) {
            gameStartTimer = new Timer();
            gameStartTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (waitingRoomPlayers.size() >= 2 && !gameStarted) {
                        startGame();
                    }
                }
            }, 30000);
        }
    }

    public static synchronized void broadcastMessage(String message) {
        System.out.println("Broadcasting message: " + message);
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.println("Failed to send message to a client: " + e.getMessage());
            }
        }
    }

    public static synchronized void registerClick(String username) {
        playerClicks.put(username, playerClicks.getOrDefault(username, 0) + 1);
        broadcastScores();
    }

 private static void broadcastScores() {
    StringBuilder scores = new StringBuilder();
    for (Map.Entry<String, Integer> entry : playerClicks.entrySet()) {
        scores.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
    }
    System.out.println("Broadcasting Scores: " + scores);
    for (ClientHandler client : clients) {
        client.sendMessage("SCORES:" + scores.toString());
    }
}


    private static void endGame() {
        gameRunning = false;
        gameStarted = false;

        String winner = playerClicks.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " with " + entry.getValue() + " clicks")
                .orElse("No winner");

        broadcastMessage("GAME_OVER:Winner: " + winner);
        System.out.println("Game over. Winner: " + winner);

       
        waitingRoomPlayers.clear();
        playerClicks.clear();
    }
public static synchronized void playerExit(String username) {
    waitingRoomPlayers.remove(username); 
    int score = playerClicks.getOrDefault(username, 0); 
    playerClicks.remove(username); 
    broadcastMessage("PLAYER_LEFT:" + username + ":" + score); // Include score in message

    System.out.println(username + " has left the game with score: " + score);

    if (waitingRoomPlayers.size() < 2) {
        endGameEarly("Not enough players to continue.");
    } else {
        broadcastScores(); 
    }
}


private static void endGameEarly(String reason) {
    gameRunning = false;
    gameStarted = false;
    broadcastMessage("GAME_OVER:" + reason);

    
    waitingRoomPlayers.clear();
    playerClicks.clear();
}




}
