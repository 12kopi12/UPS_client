import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static final int TIC_TAC_TOE_SIZE = 5;
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        // todo 1. controller -> volá newGame
        Player p2 = new Player("Bob", "localhost", 1234);

        TicTacToeController controller1 = new TicTacToeController();
        try {
            ServerClient client = new ServerClient("localhost", 10000, controller1);
            controller1.setServerClient(client);
            controller1.openLogin();
            client.listenToServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // todo 

//        Player p = new Player(sc.nextLine(), sc.nextInt());
//        TicTacToeController controller = new TicTacToeController(new TicTacToeModel(p), new TicTacToeView(), new GameClient());
    }
}