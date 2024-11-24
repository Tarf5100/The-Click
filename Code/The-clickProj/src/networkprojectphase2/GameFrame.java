package networkprojectphase2;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GameFrame extends JFrame {
    private GameClient client;
    private JTextArea scoreArea;
    private JLabel timerLabel;
    private JButton clickButton, quitButton;
    private Map<String, Integer> playerScores = new HashMap<>();
    private Timer countdownTimer;
    private int timeRemaining = 15; // Assuming a 15-second game duration

    public GameFrame(GameClient client, String[] players) {
        this.client = client;
        setTitle("Game Frame");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 44, 52));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(56, 62, 70));
        headerPanel.setBorder(new LineBorder(new Color(88, 101, 242), 2, true));
        JLabel headerLabel = new JLabel("Game in Progress");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        scoreArea = createStyledTextArea("Scores");
        JScrollPane scoreScrollPane = new JScrollPane(scoreArea);
        scoreScrollPane.setBorder(new LineBorder(new Color(98, 114, 164), 2, true));
        contentPanel.add(scoreScrollPane, BorderLayout.CENTER);

        // Timer Label
        timerLabel = new JLabel("Time Remaining: " + timeRemaining + "s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(new Color(169, 169, 169)); // Set timer text color to gray
        timerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        contentPanel.add(timerLabel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(56, 62, 70));
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        clickButton = new JButton("Click Me!");
        styleButton(clickButton, new Color(98, 114, 164), Color.WHITE);
        clickButton.setPreferredSize(new Dimension(120, 40));
        clickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendClickUpdate();
            }
        });

        quitButton = new JButton("Quit");
        styleButton(quitButton, Color.RED, Color.WHITE);
        quitButton.setPreferredSize(new Dimension(120, 40));
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleQuit();
            }
        });

        footerPanel.add(clickButton);
        footerPanel.add(quitButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        for (String player : players) {
            playerScores.put(player, 0);
        }
        updateScoreArea();

        // Start the timer
        startCountdownTimer();
    }

    private JTextArea createStyledTextArea(String title) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(new Color(40, 44, 52));
        textArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(88, 101, 242), 2, true),
                title,
                0,
                0,
                new Font("Arial", Font.BOLD, 14),
                new Color(255, 255, 255)
        ));
        return textArea;
    }

    private void styleButton(JButton button, Color backgroundColor, Color foregroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(88, 101, 242), 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void updateScores(String scores) {
        SwingUtilities.invokeLater(() -> {
            String[] entries = scores.split("\n");
            for (String entry : entries) {
                String[] parts = entry.split(": ");
                if (parts.length == 2) {
                    String player = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    playerScores.put(player, score);
                }
            }
            updateScoreArea();
        });
    }

    private void updateScoreArea() {
        StringBuilder displayText = new StringBuilder("Scores:\n");
        for (Map.Entry<String, Integer> entry : playerScores.entrySet()) {
            displayText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        scoreArea.setText(displayText.toString());
    }

    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Time Remaining: " + timeRemaining + "s");
                if (timeRemaining <= 0) {
                    countdownTimer.stop();
                }
            }
        });
        countdownTimer.start();
    }

    public void showGameOver(String result) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Game Over! " + result + "\nClick OK to return to the connection room.",
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"OK"},
                    "OK"
            );

            if (choice == JOptionPane.OK_OPTION) {
                dispose();
                client.createConnectFrame();
            }
        });
    }

    private void handleQuit() {
        client.sendMessage("exit");
        dispose();
        client.createConnectFrame();
    }

    public void handlePlayerLeft(String player, int score) {
        if (playerScores.containsKey(player)) {
            playerScores.remove(player); // Remove player from scores map
            updateScoreArea(); // Refresh the scores area

            // Show a message to other players about who left and their score
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        player + " has left the game with a score of " + score + ".",
                        "Player Left",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });

            System.out.println(player + " removed from UI with score: " + score);
        }
    }
}
