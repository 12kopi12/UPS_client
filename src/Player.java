public class Player {
    private String serverAddress;
    private String name;
//    private char playerChar;
    private int port;

    public Player (String name, String serverAddress, int port) {
        this.name = name;
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

//    public char getPlayerChar() {
//        return this.playerChar;
//    }

//    public void setPlayerChar(char playerChar) {
//        this.playerChar = playerChar;
//    }

}
