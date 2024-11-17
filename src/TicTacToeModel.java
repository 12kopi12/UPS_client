/**
 * This class represents the model of the TicTacToe game.
 */
public class TicTacToeModel {
    /**
     * The game board.
     */
    private final char[][] board;

    /**
     * The player on this PC.
     */
    private Player myPlayer;

    /**
     * The opponent player.
     */
    private Player opponentPlayer;

    /**
     * Status of the game.
     */
    private boolean gameOver;

    /**
     * The character representing the player on this PC.
     */
    public static final char MY_CHAR = 'X';

    /**
     * The character representing the opponent player.
     */
    public static final char OPONENT_CHAR = 'O';

    /**
     * Constructor of the TicTacToeModel class.
     */
    public TicTacToeModel() {
        this.board = new char[Constants.TIC_TAC_TOE_SIZE][Constants.TIC_TAC_TOE_SIZE];
        resetBoard();
    }

    /**
     * Resets the game board.
     */
    public void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' ';
            }
        }
        gameOver = false;
    }

    /**
     * Updates the game board with the response from the server.
     * @param response The response from the server. (e.g. "XOXOXOXO O X")
     */
    public void updateBoard(String response) {
        for (int i = 0; i < Constants.TIC_TAC_TOE_SIZE; i++) {
            for (int j = 0; j < Constants.TIC_TAC_TOE_SIZE; j++) {
                board[i][j] = response.charAt(i * Constants.TIC_TAC_TOE_SIZE + j);
            }
        }
    }

    /**
     * Updates the game board with the player's move.
     * @param x The x-coordinate of the move.
     * @param y The y-coordinate of the move.
     * @param player_char The player's character.
     */
    public void updateBoard(int x, int y, char player_char) {
        if (board[y][x] == ' ') {
            board[y][x] = player_char;
        }
    }

    /**
     * Returns the character at the specified position on the game board.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @return The character at the specified position.
     */
    public char getCell(int x, int y) {
        return board[y][x];
    }

    /**
     * Returns the player on this PC.
     * @return The player on this PC.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Sets the status of the game.
     * @param gameOver The status of the game.
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Sets the opponent player.
     * @param opponentName The opponent player.
     */
    public void setOpponentPlayer(String opponentName, char playerChar) {
        this.opponentPlayer = new Player(opponentName, playerChar);
    }

    /**
     * Sets the player on this PC.
     * @param player The player on this PC.
     */
    public void setMyPlayer(Player player) {
        this.myPlayer = player;
    }

    /**
     * Returns the player on this PC.
     * @return The player on this PC.
     */
    public Player getMyPlayer() {
        return myPlayer;
    }

    /**
     * Returns the opponent player.
     * @return The opponent player.
     */
    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    /**
     * Returns the game board.
     * @return The game board.
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Returns the status of the game.
     * @return The status of the game.
     */
    public boolean getGameOver() {
        return gameOver;
    }
}