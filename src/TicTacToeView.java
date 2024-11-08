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
    private final JButton[][] buttons = new JButton[Main.TIC_TAC_TOE_SIZE][Main.TIC_TAC_TOE_SIZE];

    /**
     * The controller for the game.
     */
    private TicTacToeController controller;

    /**
     * The text field for the player's name (login)
     */
    private JPanel loginPanel;

    /**
     * JPanel for the game
     */
    private JPanel gamePanel;

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
        setLayout(new GridLayout(Main.TIC_TAC_TOE_SIZE, Main.TIC_TAC_TOE_SIZE));  // Mřížka 5x5 pro herní desku

//        showLogin();  // Vytvoření tlačítek na hrací ploše

        setVisible(true);  // Zviditelnění okna
    }

    public void showLogin() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2, 10, 10));  // Mřížka 4x2 pro formulář přihlášení

        // Formulářová pole
        loginPanel.add(new JLabel("Name:"));
        nameField = new JTextField("Eli");
        loginPanel.add(nameField);

        loginPanel.add(new JLabel("Server Address:"));
        serverField = new JTextField("MOje");
        loginPanel.add(serverField);

        loginPanel.add(new JLabel("Port:"));
        portField = new JTextField("123");
        loginPanel.add(portField);

        Player myPlayer;

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if (validateLogin()) {
                     controller.setMyPlayer(nameField.getText(), serverField.getText(), Integer.parseInt(portField.getText()));
                     System.out.println("REGISTROVAN");
                }
            }
        });

        loginPanel.add(connectButton);
        add(loginPanel, BorderLayout.CENTER);
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
//        controller.getModel().setMyPlayer(new Player(name, serverAddress, Integer.parseInt(port)));
        return true;
    }

    /**
     * Initializes the game board.
     */
    public void initializeBoard() {
        remove(loginPanel);  // Odstranění formuláře přihlášení
        gamePanel = new JPanel();
        for (int i = 0; i < Main.TIC_TAC_TOE_SIZE; i++) {
            for (int j = 0; j < Main.TIC_TAC_TOE_SIZE; j++) {
                buttons[i][j] = new JButton();  // all buttons are empty at the beginning
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                final int x = j;
                final int y = i;

                // Listener for the button
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        controller.setLastMove(controller.getModel().getMyPlayer(), x, y);
                        controller.sentPlayerMove(x, y);  // Handle the player's move
                        buttonClicked = true;
                    }
                });
                add(buttons[i][j]);  // Add the button to the frame
            }
        }
        setVisible(true);  // Show the game board
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
     * Sets the controller for the game.
     * @param controller The controller for the game.
     */
    public void setController(TicTacToeController controller) {
        this.controller = controller;
    }

    /**
     * Shows the result of the game.
     * @param result The result of the game.
     */
    public void showGameResult(String result) {
        JOptionPane.showMessageDialog(this, result, "Výsledek hry", JOptionPane.INFORMATION_MESSAGE);
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

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        // close application
        System.exit(0);
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