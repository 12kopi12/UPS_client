/**
 * Controller for TicTacToe game (MVC pattern).
 */
public class TicTacToeController implements Runnable {
    /**
     * The model of the game.
     */
    private final TicTacToeModel model;

    /**
     * Class for Graphical User Interface.
     */
    private final TicTacToeView view;

    /**
     * The client for communication with the server.
     */
    private ServerClient serverClient;

    /**
     * Flag to check if it is the player's turn.
     */
    private boolean myTurn = false;

    /**
     * The controller thread.
     */
    public Thread controllerThread;

    /**
     * Class constructor
     */
    public TicTacToeController() {
        this.model = new TicTacToeModel();
        this.view = new TicTacToeView(this);
        view.setController(this);
        controllerThread = new Thread(this);
        controllerThread.start();
    }

    /**
     * Setter for the myTurn flag.
     * @param myTurn The flag to set.
     */
    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    /**
     * Getter for the myTurn flag.
     * @return The myTurn flag.
     */
    public boolean getMyTurn() {
        return myTurn;
    }

    /**
     * Getter for the serverClient.
     * @return The serverClient.
     */
    public ServerClient getServerClient() {
        return serverClient;
    }

    /**
     * Getter for the model.
     * @return The model of the game.
     */
    public TicTacToeModel getModel() {
        return model;
    }

    /**
     * Setter for the serverClient.
     * @param serverClient The serverClient to set.
     */
    public void setServerClient(ServerClient serverClient) {
        this.serverClient = serverClient;
    }

    /**
     * Opens the waiting panel.
     */
    public void openWaiting() {
        view.showWaitingPanel();
    }

    /**
     * Opens the login panel.
     */
    public void openLogin() {
        view.showLoginPanel();
        while (model.getMyPlayer() == null) {
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

    /**
     * Sends the player's move to the server.
     * @param x The x-coordinate of the move.
     * @param y The y-coordinate of the move.
     */
    public void sendPlayerMove(int x, int y) {
        serverClient.sendMove(x, y);
    }

    /**
     * Sends the opponent disconnected response to the server.
     * @param response The response to the opponent disconnection.
     */
    public void sendOppDiscResponse(String response) {
        serverClient.sendOppDiscResponse(response);
    }

    /**
     * Sends the login message to the server.
     * @param playerName The name of the player.
     */
    public void sendLogin(String playerName) {
        serverClient.sendLogin(playerName);
    }

    /**
     * Sends the logout message to the server.
     */
    public void sendLogout() {
        serverClient.sendLogout();
    }

    /**
     * Sends the want game message to the server.
     */
    public void sendWantGame() {
        serverClient.sendWantGame();
    }

    /**
     * Shows the game result.
     * @param result The result of the game.
     */
    public void showResult(String result) {
        view.showGameResult(result);
    }

    /**
     * Shows the opponent disconnected window.
     */
    public void showOpponentDisconnected() {
        view.showOpponentDisconnected();
    }

    /**
     * Shows the connection error window.
     */
    public void showConnectionError() {
        view.showConnectionError();
    }

    /**
     * Updates the game board with the player's move.
     *
     * @param x      The x-coordinate of the move.
     * @param y      The y-coordinate of the move.
     * @param player The player's name.
     */
    public void updateBoard(int x, int y, Player player) {
        model.updateBoard(x, y, player.getPlayerChar());
        view.updateBoard(model, myTurn);
    }

    /**
     * Repaints the game board according to the model.
     */
    public void repaintBoard() {
        view.updateBoard(model, myTurn);
    }

    /**
     * Updates the header of the game (player names and turn)
     */
    public void updateHeader() {
        view.updateHeader();
    }

    /**
     * Shows an error message window.
     * @param message The error message.
     */
    public void showErrorMessage(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void run() {
        openLogin();
    }
}