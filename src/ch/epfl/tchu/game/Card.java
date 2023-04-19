package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 * enumerates the types of card
 */
public enum Card {


    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;


    Card(Color color) {
        this.color = color;
    }

    /**
     * Immutable list of all card values, in order of definition
     */
    public static final List<Card> ALL = List.of(Card.values());

    /**
     * Number of values in ALL
     */
    public static final int COUNT = ALL.size();

    /**
     * Immutable List of the color of the cars
     */
    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    /**
     * @param color
     * @return the type of card, given the color
     */
    public static Card of(Color color) {
        for (Card iter : Card.values()) {
            if (iter.color == color) {
                return iter;
            }
        }
        return null;
    }

    /**
     * @return the color of the type of card if wagon, else null
     */
    public Color color() {
        return color;
    }

}
