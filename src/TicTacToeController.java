/**
 * Controller for TicTacToe game (MVC pattern).
 */
public class TicTacToeController implements Runnable{
    /** The model of the game. */
    private final TicTacToeModel model;

    /** Class for Graphical User Interface. */
    private final TicTacToeView view;

    /** The client for communication with the server.*/
    private ServerClient serverClient;

//    /**
//     * The last move of the player.
//     */
//    private final LastMove lastMove = new LastMove();


    private boolean myTurn = false;

    public Thread controllerThread;
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
        controllerThread = new Thread(this);
        controllerThread.start();
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

    public ServerClient getServerClient() {
        return serverClient;
    }
//    public void updateBoard(){
//        model.updateBoard(lastMove.getX(),lastMove.getY(), lastMove.getLastPlayer());
//        view.updateBoard(model, myTurn);
//    }

//    public void setLastMove(Player player, int x, int y) {
//        this.lastMove.setLastPlayer(player);
//        this.lastMove.setX(x);
//        this.lastMove.setY(y);
//    }

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

    public void openWaiting() {
        view.showWaitingPanel();
    }

    public void openLogin() {
        view.showLoginPanel();
        while(model.getMyPlayer() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Starts a new game.
     */
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

    public void sendPlayerMove(int x, int y) {
//        if (model.getCell(x, y) == ' ' && !model.isGameOver()) {
            // Odeslání tahu na server
            serverClient.sendMove(x, y);
//            serverClient.listenToServer();
//        }
    }
    public void sendOppDiscResponse(String response) {
        serverClient.sendOppDiscResponse(response);
    }

    public void sendLogin(String playerName) {
        serverClient.sendLogin(playerName);
    }

    public void sendLogout() {
        serverClient.sendLogout();
    }

    public void sendWantGame() {
        serverClient.sendWantGame();
    }

    public void showResult(String result) {
        view.showGameResult(result);
    }

    public void showOpponentDisconnected() {
        view.showOpponentDisconnected();
    }

    /**
     * Updates the game board with the player's move.
     * @param x The x-coordinate of the move.
     * @param y The y-coordinate of the move.
     * @param player The player's name.
     */
    public void updateBoard(int x, int y, Player player) {
//        char playerChar = playerName.equals(model.getMyPlayer().getName()) ? TicTacToeModel.MY_CHAR : TicTacToeModel.OPONENT_CHAR;
        model.updateBoard(x, y, player.getPlayerChar());
        view.updateBoard(model, myTurn);
    }

    public void repaintBoard() {
        view.updateBoard(model, myTurn);
    }

    public void updateHeader() {
        view.updateHeader();
    }


    public void endGame(String result) {
        model.setGameOver(true);
        // Zobrazit výsledek hry
    }

    public void setMyPlayer(String name, char playerChar) {
        this.model.setMyPlayer(new Player(name, playerChar));
    }

    public void showErrorMessage(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void run() {
        openLogin();
    }

//    public void setMyPlayer(Player player) {
//        this.myPlayer = player;
//    }
//
//    public void setOpponentPlayer(Player player) {
//        this.opponentPlayer = player;
//    }
}