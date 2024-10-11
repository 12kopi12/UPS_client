import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ServerClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private TicTacToeController controller;

    public ServerClient(String serverAddress, int port) throws IOException {
//        socket = new Socket(serverAddress, port);
//        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        out = new PrintWriter(socket.getOutputStream(), true);
//
//        // Nové vlákno pro příjem zpráv ze serveru
//        new Thread(this::listenToServer).start();
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
        System.out.println("cekam na server...");
        Scanner sc = new Scanner(System.in);
        String response;

        while ((response = sc.nextLine()) != null) {
            // Zpracování zprávy od serveru
            considerResponse(response);
        }
//        String response;
//        try {
//            while ((response = in.readLine()) != null) {
//                // Zpracování zprávy od serveru
//                String[] parts = response.split(" ");
//                if (parts[0].equals("MOVE_OK")) {
//                    int x = Integer.parseInt(parts[1]);
//                    int y = Integer.parseInt(parts[2]);
//                    char player = parts[3].charAt(0);
//                    controller.updateFromServer(x, y, player);
//                } else if (parts[0].equals("WIN") || parts[0].equals("DRAW")) {
//                    controller.endGame(parts[0]);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void considerResponse(String response) {
        String[] parts = response.split(" ");
        switch (parts[0]) {
            case "GAME_STARTED": {
                // todo mozna bude fajn predavat metode updateBoard i boolean myTurn (true pokud je muj tah)
                System.out.println("Prijato: GAME_STARTED");
                controller.getModel().setOpponentPlayer(parts[1]);
                controller.setMyTurn(controller.getModel().getMyPlayer().getName().compareTo(parts[2]) < 0);
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