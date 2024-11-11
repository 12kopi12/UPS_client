import java.util.Set;

public class Constants {
    public static final int PLAYER_NAME_LENGTH = 20;
    public static final int TIC_TAC_TOE_SIZE = 5;
    public static final int GAME_NOT_FOUND = 5;
    public static final int NOT_MY_TURN = 6;
    public static final int INVALID_MOVE = 7;
    public static final int FIELD_TAKEN = 8;
    public static Set<Integer> MOVE_BAD_STATUS = Set.of(GAME_NOT_FOUND, NOT_MY_TURN, INVALID_MOVE, FIELD_TAKEN);

    public static final String GAME_STATUS_DRAW = "DRAW";
}
