import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Class for graphical user interface of Tic-tac-toe game.
 */
public class TicTacToeView extends JFrame {
    /**
     * Array of buttons for the game board.
     */
    private final JButton[][] buttons = new JButton[Constants.TIC_TAC_TOE_SIZE][Constants.TIC_TAC_TOE_SIZE];

    /**
     * The controller for the game.
     */
    private TicTacToeController controller;

    /**
     * The text field for the player's name (login)
     */
    private JPanel loginPanel;

    /**
     * JPanel for waiting for the opponent
     */
    private JPanel waitingPanel;

    /**
     * JPanel for the game
     */
    private JPanel gamePanel;

    /**
     * JPanel for the header
     */
    private JPanel headerPanel;

    private JTextField nameField, serverField, portField;

    public boolean buttonClicked = false;


    /**
     * Constructor of the TicTacToeView class.
     */
    public TicTacToeView(TicTacToeController controller) {
        this.controller = controller;
        setTitle("Tic-tac-toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 800);

        setVisible(true);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (controller.getServerClient() != null) {
                if (!wantClose()) {
                    return;
                }
            }
        }
        super.processWindowEvent(e);
    }

    /**
     * Initializes the game board.
     */
    public void initializeBoard() {
        if (waitingPanel != null) {
            remove(waitingPanel);
        }
        if (loginPanel != null) {
            remove(loginPanel);
        }

        updateHeader();

        gamePanel = new JPanel();
        // show game board
        gamePanel.setLayout(new GridLayout(Constants.TIC_TAC_TOE_SIZE, Constants.TIC_TAC_TOE_SIZE));  // Mřížka 5x5 pro herní desku
        for (int i = 0; i < Constants.TIC_TAC_TOE_SIZE; i++) {
            for (int j = 0; j < Constants.TIC_TAC_TOE_SIZE; j++) {
                buttons[i][j] = new JButton();  // all buttons are empty at the beginning
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                final int x = j;
                final int y = i;

                // Listener for the button
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        controller.sendPlayerMove(x, y);
                        buttonClicked = true;
                    }
                });
                gamePanel.add(buttons[i][j]);  // Add the button to the frame
            }
        }

        add(gamePanel, BorderLayout.CENTER);
        setVisible(true);  // Show the game board
    }

    /**
     * Resets the game board.
     */
    public void resetBoard() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText(" ");
                buttons[i][j].setEnabled(true);
            }
        }
        repaint();
    }

    /**
     * Sets the controller for the game.
     * @param controller The controller for the game.
     */
    public void setController(TicTacToeController controller) {
        this.controller = controller;
    }

    /**
     * Shows the login form.
     */
    public void showLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());

        // JPanel for login form
        JPanel logForm = new JPanel();
        int width = (int) (getWidth() * 0.25);
        int height = (int) (getHeight() * 0.33);

        // set layout for login form
        logForm.setLayout(new GridLayout(3, 2, 10, 20));
        logForm.setBorder(BorderFactory.createEmptyBorder(height, width, height, width));

        // Form fields
        logForm.add(new JLabel("Name:"));
        nameField = new JTextField("Player");
        logForm.add(nameField);

        logForm.add(new JLabel("Server Address:"));
        serverField = new JTextField("147.228.67.105");
        logForm.add(serverField);

        logForm.add(new JLabel("Port:"));
        portField = new JTextField("10000");
        logForm.add(portField);

        // JPanel for button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Centrovaný layout pro tlačítko
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        JButton connectButton = new JButton("Connect");

        connectButton.addActionListener(e -> {
            nameField.setText(nameField.getText().trim());
            portField.setText(portField.getText().trim());
            serverField.setText(serverField.getText().trim());
            if (validateLogin()) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            controller.setServerClient(new ServerClient(serverField.getText(), Integer.parseInt(portField.getText()), controller));
                            controller.sendLogin(nameField.getText());
                            controller.sendWantGame();
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> showInfoMessage("Error: connecting to server failed - try again (check IP address and port)"));
                        }
                        return null;
                    }
                }.execute();
            }
        });

        buttonPanel.add(connectButton);

        loginPanel.add(logForm, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(loginPanel, BorderLayout.CENTER);
//        revalidate();
//        repaint();
        setVisible(true);
    }

    /**
     * Shows the waiting panel.
     */
    public void showWaitingPanel() {
        if (loginPanel != null) {
            remove(loginPanel);
        }
        if (gamePanel != null) {
            remove(gamePanel);
        }
        if (headerPanel != null) {
            remove(headerPanel);
        }

        waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.add(new JLabel("<html><span style='font-size:20px'>Waiting for the opponent...</span></html>", SwingConstants.CENTER), BorderLayout.CENTER);
        add(waitingPanel, BorderLayout.CENTER);
        waitingPanel.repaint();

        setVisible(true);
    }

    /**
     * Shows the result of the game.
     *
     * @param result The result of the game.
     */
    public void showGameResult(String result) {
        int response = JOptionPane.showOptionDialog(gamePanel, result, "GAME RESULT", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"New Game", "Quit Game"}, "New Game");
        if (response == 0) {
            controller.sendWantGame();
        } else {
            controller.sendLogout();
            System.exit(0);
        }

        gamePanel.revalidate();
        gamePanel.repaint();
    }

    /**
     * Shows the Option dialog for message that the opponent has disconnected.
     */
    public void showOpponentDisconnected() {
        int response = JOptionPane.showOptionDialog(gamePanel, "Do you want to wait for opponent?", "Opponent disconnected", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No"}, "Yes");
        if (response == 0) {
            controller.sendOppDiscResponse("WAIT");
        } else {
            controller.sendOppDiscResponse("NOT_WAIT");
        }
    }

    /**
     * Shows the Option dialog in case that user want to leave from existing connection
     */
    public boolean wantClose() {
        int response = JOptionPane.showOptionDialog(gamePanel, "Do you really want to leave?", "", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No"}, "No");
        if (response == 0) {
            controller.sendLogout();
            return true;
        }
        return false;
    }

    /**
     * Shows the Option dialog in case of connection error.
     */
    public void showConnectionError() {
        int response = JOptionPane.showOptionDialog(gamePanel, "Do you want to try to reconnect?", "Connection error", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No"}, "Yes");
        if (response == 1) {
            System.exit(0);
        }
    }

    /**
     * Shows the error message in message dialog.
     * @param message The message to be shown.
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        // close application
        System.exit(0);
    }

    /**
     * Shows the info message in message dialog.
     * @param message The message to be shown.
     */
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the game board with the player's move according to the model.
     *
     * @param model       The model of the game.
     * @param isClickable True if the button is clickable, false otherwise.
     */
    public void updateBoard(TicTacToeModel model, boolean isClickable) {
        char[][] board = model.getBoard();
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText(String.valueOf(board[i][j]));
                buttons[i][j].setEnabled(isClickable);  // Enable the button if it is empty
                System.out.print(" " + (board[i][j] == ' ' ? "N" : board[i][j]));
            }
            System.out.println();
        }
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    /**
     * Updates the header of the game (player names and turn)
     */
    public void updateHeader() {
        if (headerPanel != null) {
            remove(headerPanel);
            headerPanel = null;
        }

        headerPanel = new JPanel(new GridLayout(2, 3));

        JLabel header1 = new JLabel("PLAYER", SwingConstants.CENTER);
        header1.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(header1);

        JLabel header2 = new JLabel("CURRENT_PLAYER", SwingConstants.CENTER);
        header2.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(header2);

        JLabel header3 = new JLabel("OPPONENT", SwingConstants.CENTER);
        header3.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(header3);

        // show header (names of the players)
        JLabel player1 = new JLabel(controller.getModel().getMyPlayer().getName().trim(), SwingConstants.CENTER);
        player1.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(player1);

        JLabel currentPlayer = new JLabel(controller.getMyTurn() ? controller.getModel().getMyPlayer().getName().trim() : controller.getModel().getOpponentPlayer().getName().trim(), SwingConstants.CENTER);
        currentPlayer.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(currentPlayer);

        JLabel player2 = new JLabel(controller.getModel().getOpponentPlayer().getName().trim(), SwingConstants.CENTER);
        player2.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(player2);

        add(headerPanel, BorderLayout.NORTH);
        headerPanel.revalidate();
        headerPanel.repaint();
        setVisible(true);
    }

    /**
     * Handles the login of the player.
     */
    private boolean validateLogin() {
        String name = nameField.getText();
        String serverAddress = serverField.getText();
        String port = portField.getText();

        if (name.isEmpty() || serverAddress.isEmpty() || port.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Port must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (name.length() > Constants.PLAYER_NAME_LENGTH) {
            JOptionPane.showMessageDialog(this, "Name is too long!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        while (nameField.getText().length() < Constants.PLAYER_NAME_LENGTH) {
            nameField.setText(nameField.getText() + " ");
        }
        return true;
    }
}