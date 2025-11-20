import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class OnlineGameManager {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean host;

    private ChessGUI gui;

    public OnlineGameManager(ChessGUI gui) {
        this.gui = gui;
    }

    /** Host a game (WHITE) */
    public void hostGame() {
        new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(5000);
                gui.showMessage("Hosting game... Waiting for opponent.");
                socket = server.accept();
                gui.showMessage("Opponent connected! You are WHITE.");
                host = true;
                setupStreams();
                listenForMoves();
            } catch (Exception e) {
                gui.showMessage("Error hosting: " + e.getMessage());
            }
        }).start();
    }

    /** Join a game (BLACK) */
    public void joinGame(String ip) {
        new Thread(() -> {
            try {
                gui.showMessage("Connecting to host...");
                socket = new Socket(ip, 5000);
                gui.showMessage("Connected! You are BLACK.");
                host = false;
                setupStreams();
                listenForMoves();
            } catch (Exception e) {
                gui.showMessage("Error joining: " + e.getMessage());
            }
        }).start();
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void listenForMoves() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    gui.applyNetworkMove(line.trim());
                }
            } catch (Exception e) {
                gui.showMessage("Connection lost.");
            }
        }).start();
    }

    public void sendMove(String notation) {
        if (out != null) out.println(notation);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean amHost() {
        return host;
    }
}
