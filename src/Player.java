/**
 * Player class
 */
public class Player {
    /** The player's name */
    private String name;

    /** The player's character */
    char playerChar;

    /**
     * Constructor
     * @param name The player's name
     */
    public Player (String name) {
        this.name = name;
    }

    /**
     * Constructor
     * @param name The player's name
     * @param playerChar The player's character
     */
    public Player (String name, char playerChar) {
        this.name = name;
        this.playerChar = playerChar;
    }

    /**
     * Get the player's name
     * @return The player's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the player's character
     * @return The player's character
     */
    public char getPlayerChar() {
        return this.playerChar;
    }

    /**
     * Set the player's character
     * @param playerChar The player's character
     */
    public void setPlayerChar(char playerChar) {
        this.playerChar = playerChar;
    }

}
