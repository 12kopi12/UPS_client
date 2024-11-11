import javax.swing.*;
import java.io.*;
import java.net.*;

public class ServerClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private TicTacToeController controller;

    public ServerClient(String serverAddress, int port, TicTacToeController controller) throws IOException {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.controller = controller;

//        out.println("LOGIN;Myname              \n");
//            out.println("TIC-TAC-TOE;INIT\n");

//            // if server response is not "TIC-TAC-TOE;INIT;OK" then show error message and close the connection
//            String response = in.readLine();
//            if (!response.equals("TIC-TAC-TOE;INIT;OK")) {
//                System.out.println("Error: connecting to server");
//                controller.showErrorMessage("Unable to connect to the server");
//                socket.close();
//                return;
//            } else {
//                System.out.println("INIT OK: Connected to server on port " + port);
//            }

            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            System.err.println("Error: connecting to server");
            throw e;
//            controller.showErrorMessage("Unable to connect to the server");
        }
    }

    public void setController(TicTacToeController controller) {
        this.controller = controller;
    }

    // Odeslání tahu na server
    public void sendMove(int x, int y) {
        System.out.println("Sending: MOVE;" + x + ";" + y + "\n");
        out.println("MOVE;" + x + ";" + y + "\n");
    }

    public void sendLogin(String name) {
        System.out.println("LOGIN len = " + name.length());
        System.out.println("Sending: LOGIN;" + name + "\n");
        out.println("LOGIN;" + name + "\n");
    }

    // Poslouchání zpráv od serveru
    public void listenToServer() {
//        System.out.println("cekam na server...");
//        Scanner sc = new Scanner(System.in);
//        String response;
//
//        while ((response = sc.nextLine()) != null) {
//            // Zpracování zprávy od serveru
//            considerResponse(response);
//        }
        String response;
        try {
            while ((response = in.readLine()) != null) {
                considerResponse(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void considerResponse(String response) {
        System.out.println("Prijato: " + response);
        String[] parts = response.split(";");
        switch (parts[0]) {
            case "GAME_STATUS": {
                System.out.println("Prijato: GAME_STATUS");
                SwingUtilities.invokeLater(() -> {
                    if (parts[1].equals(Constants.GAME_STATUS_DRAW)) {
                        controller.showResult("DRAW");
                    } else {
                        controller.showResult(parts[1].equals(controller.getModel().getMyPlayer().getName()) ? "YOU WIN!!!" : "YOU LOSE...");
                    }
                });
                break;
            }
            case "LOGIN": {
                System.out.println("Prijato: LOGIN_OK");
                controller.getModel().setMyPlayer(new Player(parts[1], parts[2].charAt(0)));
                controller.openWaiting();
                break;
            }
            case "START_GAME": {
                System.out.println("Prijato: GAME_STARTED");
                controller.getModel().setOpponentPlayer(parts[1], parts[2].charAt(0));
                controller.setMyTurn(parts[3].charAt(0) == '1');
                controller.newGame();
                break;
            }
            case "MOVE": {
                System.out.println("Prijato: MOVE");
                int status = Integer.parseInt(parts[1]);
                if (Constants.MOVE_BAD_STATUS.contains(status)) {
                    System.out.println("Neplatny tah: " + status);
                    return;
                } else {
                    controller.setMyTurn(false);
                    controller.updateBoard(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), controller.getModel().getMyPlayer());
                    controller.updateHeader();
                }
                break;
            }
            case "OPP_MOVE": {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                controller.setMyTurn(true);
                controller.updateBoard(x, y, controller.getModel().getOpponentPlayer());
                controller.updateHeader();
                controller.myTurn();
                break;
            }
            case "WIN": {
                controller.endGame(parts[1]);
                break;
            }
            default: {
                System.out.println("Neplatna zprava od serveru: " + response);
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