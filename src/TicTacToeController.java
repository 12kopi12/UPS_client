/**
 * Controller for TicTacToe game (MVC pattern).
 */
public class TicTacToeController {
    /** The model of the game. */
    private final TicTacToeModel model;

    /** Class for Graphical User Interface. */
    private final TicTacToeView view;

    /** The client for communication with the server.*/
    private ServerClient serverClient;

    private final LastMove lastMove = new LastMove();

    private boolean myTurn = false;

    /**
     * Class constructor
     */
    public TicTacToeController() {
        this.model = new TicTacToeModel();
        // todo pak zakomentovat
//        Player p2 = new Player("Bob", "localhost", 1234);
//        model.setOpponentPlayer(p2);
        // todo konec komentu
        this.view = new TicTacToeView(this);
        view.setController(this);

    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public boolean getMyTurn() {
        return myTurn;
    }

    public void updateBoard(){
        model.updateBoard(lastMove.getX(),lastMove.getY(), lastMove.getLastPlayer());
        view.updateBoard(model, myTurn);
    }

    public void setLastMove(Player player, int x, int y) {
        this.lastMove.setLastPlayer(player);
        this.lastMove.setX(x);
        this.lastMove.setY(y);
    }

    /**
     * Getter for the model.
     * @return The model of the game.
     */
    public TicTacToeModel getModel() {
        return model;
    }

    public void setServerClient(ServerClient serverClient) {
        this.serverClient = serverClient;
    }

    public void openLogin() {
        view.showLogin();
        while(model.getMyPlayer() == null || model.getMyPlayer().equals(null)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void newGame() {
        view.initializeBoard();
        model.resetBoard();
        view.updateBoard(model, myTurn);
    }

    public void myTurn() {
        while (!view.buttonClicked) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        view.buttonClicked = false;
    }

    public void sentPlayerMove(int x, int y) {
        if (model.getCell(x, y) == ' ' && !model.isGameOver()) {
            // Odeslání tahu na server
            serverClient.sendMove(x, y);
//            serverClient.listenToServer();
        }
    }

    /**
     * Updates the game board with the player's move.
     * @param x The x-coordinate of the move.
     * @param y The y-coordinate of the move.
     * @param playerName The player's name.
     */
    public void updateBoard(int x, int y, String playerName) {
        Player player = playerName.equals(model.getMyPlayer().getName()) ? model.getMyPlayer() : model.getOpponentPlayer();
//        char playerChar = playerName.equals(model.getMyPlayer().getName()) ? TicTacToeModel.MY_CHAR : TicTacToeModel.OPONENT_CHAR;
        model.updateBoard(x, y, player);
        view.updateBoard(model, myTurn);
    }


    public void endGame(String result) {
        model.setGameOver(true);
        // Zobrazit výsledek hry
    }

    public void setMyPlayer(String name, String serverAdress, int port) {
        this.model.setMyPlayer(new Player(name, serverAdress, port));
    }

    public void showErrorMessage(String message) {
        view.showErrorMessage(message);
    }

//    public void setMyPlayer(Player player) {
//        this.myPlayer = player;
//    }
//
//    public void setOpponentPlayer(Player player) {
//        this.opponentPlayer = player;
//    }
}