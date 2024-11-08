import java.io.*;
import java.net.*;
import java.util.Scanner;

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
            out.println("TIC-TAC-TOE;INIT\n");

            // if server response is not "TIC-TAC-TOE;INIT;OK" then show error message and close the connection
            String response = in.readLine();
            if (!response.equals("TIC-TAC-TOE;INIT;OK")) {
                System.out.println("Error: connecting to server");
                controller.showErrorMessage("Unable to connect to the server");
                socket.close();
                return;
            } else {
                System.out.println("INIT OK: Connected to server on port " + port);
            }

            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            System.err.println("Error: connecting to server");
            controller.showErrorMessage("Unable to connect to the server");
        }
    }

    public void setController(TicTacToeController controller) {
        this.controller = controller;
    }

    // Odeslání tahu na server
    public void sendMove(int x, int y) {
        System.out.println("MOVE " + x + " " + y);
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
        String[] parts = response.split(";");
        switch (parts[0]) {

            case "LOGIN": {
                if (parts[1].equals("OK")) {
                    System.out.println("Prijato: LOGIN_OK");
                } else {
                    System.out.println("Prijato: LOGIN_FAIL");
                }
                break;
            }
            case "GAME_STARTED": {
                // todo mozna bude fajn predavat metode updateBoard i boolean myTurn (true pokud je muj tah)
                System.out.println("Prijato: GAME_STARTED");
                controller.getModel().setOpponentPlayer(parts[1]);
                controller.setMyTurn(controller.getModel().getMyPlayer().getName().compareTo(parts[2]) == 0);
                controller.newGame();
                break;
            }
            case "MOVE_OK": {
                System.out.println("Prijato: MOVE_OK");
                controller.setMyTurn(false);
                controller.updateBoard();
                break;
            }
            case "OPP_MOVE": {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                String playerName = parts[3];
                controller.setMyTurn(true);
                controller.updateBoard(x, y, playerName);
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