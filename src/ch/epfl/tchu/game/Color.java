package ch.epfl.tchu.game;

import java.util.List;

/**
 *  @author Sebastian Maier (327504)
 *  @author Theo Schifferli (326468)
 *  enumerates the different colors
 */
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * Immutable List of the colors
     */
    public static final List<Color> ALL = List.of(Color.values());

    /**
     * Number of values in ALL
     */
    public static final int COUNT = ALL.size();


}
