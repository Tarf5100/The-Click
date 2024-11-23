# The-Click
<h2>Online Multiplayer Clicking Game</h2>

<p><strong>Description:</strong><br>
This project is a Java-based online multiplayer clicking game where players compete in real-time to achieve the highest click count within a set time. It uses a client-server architecture, allowing multiple players to connect, play, and see live score updates with instant feedback on their performance.</p>

<hr>

<h2>Key Features</h2>
<ul>
  <li><strong>Real-Time Multiplayer:</strong> Multiple players compete live, with instant score updates.</li>
  <li><strong>Client-Server Structure:</strong> A centralized server manages connections, game sessions, and score tracking.</li>
  <li><strong>Java Swing GUI:</strong> Provides an intuitive interface for player interactions and live score displays.</li>
</ul>

<hr>

<h2>Technical Highlights</h2>
<ul>
  <li><strong>Java Sockets</strong> for client-server communication</li>
  <li><strong>Data structures</strong> (ArrayLists and HashMaps) for efficient player and score management</li>
  <li><strong>Automatic Game Start</strong> when the minimum number of players is reached</li>
</ul>

<hr>

<h2>Getting Started</h2>

<h3>Prerequisites</h3>
<ul>
  <li>JDK 8 or above</li>
  <li>Maven (if used for managing dependencies)</li>
</ul>

<h3>Running the Server</h3>
<ol>
  <li>Navigate to the project folder.</li>
  <li>Compile and run the <code>GameServer.java</code> file:
    <pre><code>javac GameServer.java
java GameServer
    </code></pre>
  </li>
</ol>

<h3>Running the Client</h3>
<ol>
  <li>Compile and run the <code>ConSer.java</code> file:
    <pre><code>javac ConSer.java
java ConSer
    </code></pre>
  </li>
  <li>In the client window:
    <ul>
      <li>Enter your name.</li>
      <li>Click <strong>Connect</strong> to join the server.</li>
      <li>Once connected, you’ll see other players in the lobby and can <strong>Play</strong> or <strong>Quit</strong>.</li>
    </ul>
  </li>
</ol>

<hr>

<h2>How to Play</h2>
<ul>
  <li><strong>Objective:</strong> Achieve the highest click count within a timed session.</li>
  <li><strong>Actions:</strong> Press the <strong>spacebar</strong> to increase your click count during the game.</li>
  <li><strong>Winning:</strong> The player with the highest clicks when time runs out wins the game.</li>
</ul>

<h3>GUI Components</h3>
<ul>
  <li><strong>Lobby:</strong> Displays the list of connected players.</li>
  <li><strong>Game Window:</strong> Shows countdown timers, live score updates, and click counters.</li>
  <li><strong>Buttons:</strong> <code>Connect</code>, <code>Play</code>, and <code>Quit</code> to manage connections and sessions.</li>
</ul>

<hr>

<h2>Troubleshooting</h2>
<ul>
  <li><strong>Connection Issues:</strong> Ensure the server is running and that the client IP address matches the server’s.</li>
  <li><strong>Full Room Error:</strong> If the lobby is full, wait for a player to leave before joining.</li>
</ul>

<hr>

<h2>Future Enhancements</h2>
<ul>
  <li><strong>Expanded Player Count:</strong> Increase max players per session.</li>
  <li><strong>Improved Graphics:</strong> Enhance the GUI for better player interaction.</li>
  <li><strong>Additional Game Modes:</strong> Introduce varied gameplay for replayability.</li>
</ul>

<hr>

<p>This project provides hands-on experience with network programming, real-time data handling, and GUI design in Java. Enjoy competing with friends!</p>



<h2>Team Members</h2>
<ul>
  <li>Tarfah Bin Moammar</li>
  <li>Amani Alharbi</li>
  <li>Layan Alamri</li>
  <li>Afnan Alzikri</li>
</ul>
