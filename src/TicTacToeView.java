import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        setSize(800, 800);
        setLayout(new GridLayout(Constants.TIC_TAC_TOE_SIZE, Constants.TIC_TAC_TOE_SIZE));  // Mřížka 5x5 pro herní desku

//        showLogin();  // Vytvoření tlačítek na hrací ploše

        setVisible(true);  // Zviditelnění okna
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public JPanel getWaitingPanel() {
        return waitingPanel;
    }

    /**
     * Initializes the game board.
     */
    public void initializeBoard() {
        if (waitingPanel != null) {
            remove(waitingPanel);
            waitingPanel = null;
        }
        setLayout(new BorderLayout());

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
//                        controller.setLastMove(controller.getModel().getMyPlayer(), x, y);
                        controller.sentPlayerMove(x, y);
                        buttonClicked = true;
                    }
                });
                gamePanel.add(buttons[i][j]);  // Add the button to the frame
            }
        }

//        JButton someButton = new JButton("BAD_BUTTON");
//        someButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                controller.sentPlayerMove(-1, -2);
//            }
//        });
//        gamePanel.add(someButton);

        add(gamePanel, BorderLayout.CENTER);
        setVisible(true);  // Show the game board
    }

    /**
     * Resets the game board.
     */
    public void resetBoard() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText(" ");  // Vymazání textu (X nebo O)
                buttons[i][j].setEnabled(true);  // Opětovné povolení tlačítka
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
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2, 10, 10));  // Mřížka 4x2 pro formulář přihlášení

        // Formulářová pole
        loginPanel.add(new JLabel("Name:"));
        nameField = new JTextField("Eliska");
        loginPanel.add(nameField);

        loginPanel.add(new JLabel("Server Address:"));
        serverField = new JTextField("localhost");
        loginPanel.add(serverField);

        loginPanel.add(new JLabel("Port:"));
        portField = new JTextField("10000");
        loginPanel.add(portField);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateLogin()) {
                    try {
                        controller.setServerClient(new ServerClient(serverField.getText(), Integer.parseInt(portField.getText()), controller));
                        controller.sentLogin(nameField.getText());
                    } catch (Exception ex) {
                        showInfoMessage("Error: connecting to server failed - try again (check IP address and port)");
                    }
                }
            }
        });

        loginPanel.add(connectButton);
        add(loginPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Shows the waiting panel.
     */
    public void showWaitingPanel() {
        if (loginPanel != null) {
            remove(loginPanel);
        }
        setLayout(new BorderLayout());

        waitingPanel = new JPanel(new BorderLayout());
        waitingPanel.add(new JLabel("Waiting for the opponent...", SwingConstants.CENTER), BorderLayout.CENTER);
        add(waitingPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Shows the result of the game.
     * @param result The result of the game.
     */
    public void showGameResult(String result) {
        JOptionPane.showMessageDialog(gamePanel, result, "GAME RESULT", JOptionPane.INFORMATION_MESSAGE);
        gamePanel.repaint();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        // close application
        System.exit(0);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the game board with the player's move according to the model.
     * @param model The model of the game.
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

    public void updateHeader() {
        if (headerPanel != null) {
            remove(headerPanel);
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
        setVisible(true);
    }
//        char[][] board = model.getBoard();
//        for (int i = 0; i < buttons.length; i++) {
//            for (int j = 0; j < buttons[i].length; j++) {
//                if (board[i][j] != ' ') {
////                    buttons[i][j].remove(j);
//                    buttons[i][j] = new JButton("" + board[i][j]);
//                    System.out.println("Nasel jsem tlacitko: " + board[i][j]);
////                    buttons[i][j].setText(String.valueOf(board[i][j]));
////                    buttons[i][j].;
//                    System.out.println("TLACITKO TEXT: " + buttons[i][j].getText());
//                    buttons[i][j].setEnabled(false);
////                    buttons[i][j].repaint();
//                }
//            }
//        }
//        gamePanel.updateUI();
//        buttons[x][y].setText(String.valueOf(player));  // Nastaví symbol hráče (X nebo O)
//        buttons[x][y].setEnabled(false);  // Deaktivuje tlačítko po provedení tahu
//    }

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
            System.out.println("Doplnuji mezery");
        }
//        controller.getModel().setMyPlayer(new Player(name, serverAddress, Integer.parseInt(port)));
        return true;
    }

//    public static void main(String[] args) {
//        // Pouze pro účely testování UI bez napojení na controller
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new TicTacToeView();
//            }
//        });
//    }
}