package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;


    /**
     * Immutable list of all player values, in order of definition
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Number of values in ALL
     */
    public static final int COUNT = ALL.size();

    /**
     * @return the player who's going to play next
     */
    public PlayerId next(){
        return (this == PLAYER_1)? PLAYER_2:PLAYER_1;
    }

}
