import java.io.*;
import java.net.*;

public class ServerClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private TicTacToeController controller;

    private long lastPing;

    private boolean needConnectionMessage = true;

    public ServerClient(String serverAddress, int port, TicTacToeController controller) throws IOException {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.controller = controller;

            this.lastPing = System.currentTimeMillis();

            new Thread(this::listenToServer).start();
            new Thread(this::monitorConnection).start();
        } catch (IOException e) {
            System.err.println("Error: connecting to server");
            throw e;
        }
    }

    public void setController(TicTacToeController controller) {
        this.controller = controller;
    }

    public void sendMove(int x, int y) {
        if (out.checkError()) {
            System.err.println("Error: Connection is not active");
            controller.showErrorMessage("Connection is not active");
            System.exit(0);
        }
        System.out.println("Sending: " + x + ";" + y);
        out.println("MOVE;" + x + ";" + y + "\n");
    }

    public void sendLogin(String name) {
        if (out.checkError()) {
            System.err.println("Error: Connection is not active");
            controller.showErrorMessage("Connection is not active");
            System.exit(0);
        }
        System.out.println("Sending: Login");
        out.println("LOGIN;" + name + "\n");
    }

    public void sendOppDiscResponse(String response) {
        if (out.checkError()) {
            System.err.println("Error: Connection is not active");
            controller.showErrorMessage("Connection is not active");
            System.exit(0);
        }
        System.out.println("Sending: OPPONENT DISCONNECTED response");
        out.println("OPP_DISCONNECTED;" + response + "\n");
    }

    public void sendWantGame() {
        if (out.checkError()) {
            System.err.println("Error: Connection is not active");
            controller.showErrorMessage("Connection is not active");
            System.exit(0);
            return;
        }
        System.out.println("Sending: WANT GAME\n");
        out.println("WANT_GAME;\n");
    }

    public void sendLogout() {
        if (out.checkError()) {
            System.err.println("Error: Connection is not active");
            controller.showErrorMessage("Connection is not active");
            System.exit(0);
        }
        System.out.println("Sending: LOGOUT\n");
        out.println("LOGOUT;\n");
    }

    public void listenToServer() {
        String response;
        try {
            while ((response = in.readLine()) != null) {
                considerResponse(response);
            }
        } catch (IOException e) {
            System.err.println("Error: Connection is not active (listenToServer)");
            controller.showErrorMessage("Connection is not active");
            System.exit(0);
        }
    }

    private void monitorConnection() {
        while (true) {
            if (System.currentTimeMillis() - this.lastPing > Constants.TIMEOUT && this.needConnectionMessage) {
                System.err.println("Error: Connection is not active (monitorConnection)");
                this.needConnectionMessage = false;
                controller.showConnectionError();
            }

            if (System.currentTimeMillis() - this.lastPing > Constants.ZOMBIE_TIMEOUT) {
                System.err.println("Error: Connection is not active - zombie time reached (monitorConnection)");
                controller.showErrorMessage("Connection is not active");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void considerResponse(String response) {
        String[] parts = response.split(";");
        switch (parts[0]) {
            case "GAME_STATUS": {
                System.out.println("Received: GAME_STATUS");
                if (parts[1].equals(Constants.GAME_STATUS_DRAW)) {
                    new Thread(() -> controller.showResult("DRAW")).start();
                } else if (parts[1].equals(Constants.GAME_STATUS_OPP_END)) {
                    new Thread(() -> controller.showResult("OPPONENT DID NOT WANT TO WAIT FOR YOU")).start();
                } else {
                    new Thread(() -> controller.showResult(parts[1].equals(controller.getModel().getMyPlayer().getName()) ? "YOU WIN!!!" : "YOU LOSE...")).start();
                }
                break;
            }
            case "LOGIN": {
                System.out.println("Received: LOGIN_OK");
                controller.getModel().setMyPlayer(new Player(parts[1]));
                break;
            }
            case "WANT_GAME": {
                System.out.println("Received: WANT_GAME");
                controller.getModel().getMyPlayer().setPlayerChar(parts[1].charAt(0));
                controller.openWaiting();
                break;
            }
            case "START_GAME": {
                System.out.println("Received: GAME_STARTED");
                controller.getModel().setOpponentPlayer(parts[1], parts[2].charAt(0));
                controller.setMyTurn(parts[3].charAt(0) == '1');
                controller.newGame();
                break;
            }
            case "MOVE": {
                System.out.println("Received: MOVE");
                int status = Integer.parseInt(parts[1]);
                if (Constants.MOVE_BAD_STATUS.contains(status)) {
                    System.out.println("Invalid move: " + status);
                    return;
                }
                controller.setMyTurn(false);
                controller.updateBoard(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), controller.getModel().getMyPlayer());
                controller.updateHeader();
                break;
            }
            case "OPP_MOVE": {
                System.out.println("Received: OPP_MOVE");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                controller.setMyTurn(true);
                controller.updateBoard(x, y, controller.getModel().getOpponentPlayer());
                controller.updateHeader();
                break;
            }
            case "PING": {
                System.out.println("Received: PING");

                this.lastPing = System.currentTimeMillis();
                this.needConnectionMessage = true;

                out.println("PONG;\n");
                break;
            }
            case "OPP_DISCONNECTED": {
                System.out.println("Received: OPP_DISCONNECTED");
                controller.setMyTurn(false);
                controller.repaintBoard();
                new Thread(() -> controller.showOpponentDisconnected()).start();
                break;
            }
            case "RECONNECT": {
                System.out.println("Received: RECONNECT");
                controller.setMyTurn(parts[2].equals(controller.getModel().getMyPlayer().getName()));
                controller.getModel().updateBoard(parts[1]);
                controller.getModel().setOpponentPlayer(parts[3], parts[4].charAt(0));
                controller.repaintBoard();
                controller.updateHeader();
                break;
            }
            default: {
                System.out.println("Invalid server message -> close connection" + response);
                controller.showErrorMessage("Invalid server message");
                System.exit(0);
            }
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}