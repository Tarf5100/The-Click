package networkprojectphase2;

import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean playClicked = false;
    private String username;
    private connectedName connectedName;
    private WaitingRoom WaitingRoom;
    private GameFrame gameFrame;

    public static void main(String[] args) {
        new GameClient().createConnectFrame();
    }

    public void createConnectFrame() {
        new ConnectFrame(this);
    }

    public void connect(String username) {
        this.username = username;
        try {
            socket = new Socket("localhost", 9090);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);

            createPlayersFrame();

            new Thread(this::listenToServer).start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createPlayersFrame() {
        connectedName = new connectedName(this);
    }

    public void createWaitingRoomFrame() {
        if (WaitingRoom == null) {
            WaitingRoom = new WaitingRoom(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

public void listenToServer() {
    try {
        String msg;
        while ((msg = in.readLine()) != null) {
            if (msg.startsWith("CONNECTED_USERS:")) {
                String[] usernames = msg.substring(16).split(",");
                if (connectedName != null) {
                    connectedName.updatePlayersArea(usernames);
                }
            } else if (msg.startsWith("WAITING_ROOM_PLAYERS:")) {
                String[] waitingPlayers = msg.substring(20).split(",");
                if (WaitingRoom == null && userClickedPlay()) {
                    createWaitingRoomFrame();
                }
                if (WaitingRoom != null) {
                    WaitingRoom.updateWaitingRoomArea(waitingPlayers);
                }
            } else if (msg.startsWith("START_GAME")) {
                String[] players = msg.substring(11).split(",");
                SwingUtilities.invokeLater(() -> {
                    gameFrame = new GameFrame(this, players);
                    gameFrame.setVisible(true);
                    if (WaitingRoom != null) {
                        WaitingRoom.dispose();
                    }
                });
            } else if (msg.startsWith("SCORES:")) {
                String scores = msg.substring(7);
                SwingUtilities.invokeLater(() -> {
                    if (gameFrame != null) {
                        gameFrame.updateScores(scores);
                    }
                });
            } else if (msg.startsWith("PLAYER_LEFT:")) {
                String[] parts = msg.substring(12).split(":");
                if (parts.length == 2) {
                    String playerLeft = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    SwingUtilities.invokeLater(() -> {
                        if (gameFrame != null) {
                            gameFrame.handlePlayerLeft(playerLeft, score);
                        }
                    });
                }
            } else if (msg.startsWith("GAME_OVER:")) {
                String reason = msg.substring(10);
                SwingUtilities.invokeLater(() -> {
                    if (gameFrame != null) {
                        gameFrame.showGameOver(reason);
                    }
                });
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}




    public void sendClickUpdate() {
        sendMessage("CLICK");
    }

    public boolean userClickedPlay() {
        return playClicked;
    }

    public void sendPlayMessage() {
        playClicked = true;
        sendMessage("play");
    }
}
