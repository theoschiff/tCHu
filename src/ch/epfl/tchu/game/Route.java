package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Route {

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;


    /**
     * enumerates the two different levels
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    /**
     * Constructor for Route
     *
     * @param id:       staion's id
     * @param station1: first station, of departure
     * @param station2: second station, of arrival
     * @param length:   length of the Route
     * @param level:    Underground or Overground
     * @param color:    Route's color
     * @throws IllegalArgumentException if station1 is the same as station2
     *                                  or if the route's length is not contained
     *                                  in a given interval, can't exceed 6.
     * @throws NullPointerException     If stations are null or if level is null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH
                && length <= Constants.MAX_ROUTE_LENGTH);

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;

    }

    /**
     * @return the station's identification number
     */
    public String id() {
        return this.id;
    }

    /**
     * getter for station1
     *
     * @return station1
     */
    public Station station1() {
        return this.station1;
    }

    /**
     * getter for station2
     *
     * @return station2
     */
    public Station station2() {
        return this.station2;
    }

    /**
     * getter for route's length
     *
     * @return route's length
     */
    public int length() {
        return this.length;
    }

    /**
     * getter for level
     *
     * @return level
     */
    public Level level() {
        return this.level;
    }

    /**
     * getter for color
     *
     * @return route's color
     */
    public Color color() {
        return this.color;
    }

    /**
     * List of the two stations
     *
     * @return list of the two stations, in order
     */
    public List<Station> stations() {
        List<Station> orderS = new ArrayList<>();
        orderS.add(station1);
        orderS.add(station2);
        return orderS;
    }

    /**
     * @param station: the station that we want the opposite
     * @return station that is not the one given
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station == station1 || station == station2);
        return (station == station1) ? station2 : station1;
    }

    /**
     * @return a list of all sets of card that can be drawn to take a route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        ArrayList<SortedBag<Card>> listCards = new ArrayList<>();
        Card cardOf = Card.of(color);
        if (level().equals(Level.OVERGROUND)) {
            if (color == null) {
                for (Color c : Color.values()) {
                    listCards.add(SortedBag.of(length, Card.of(c)));
                }
            } else {
                listCards.add(SortedBag.of(length, cardOf));
            }

        } else {
            if (color == null) {
                for (int l = 0; l < length; ++l) {
                    for (Color c : Color.values()) {
                        listCards.add(SortedBag.of(length - l, Card.of(c), l, Card.LOCOMOTIVE));
                    }
                }
                listCards.add(SortedBag.of(length, Card.LOCOMOTIVE));

            } else {
                for (int l = 0; l <= length; ++l) {
                    listCards.add(SortedBag.of(length - l, cardOf, l, Card.LOCOMOTIVE));
                }
            }

        }

        return listCards;
    }

    /**
     * @param claimCards: cards used to take
     * @param drawnCards: cards drawn to know the additional cost of the route
     * @throws IllegalArgumentException if the level is OVERGROUND,
     *                                  and if the size of the drawn cards is not equal to 3
     * @return the number of additional cards to play to grab the tunnel
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level() == Level.UNDERGROUND);
        Preconditions.checkArgument(drawnCards.size() == 3);
        Card usedCardOf = claimCards.get(0);
        if (usedCardOf.equals(Card.LOCOMOTIVE)) {
            return drawnCards.countOf(usedCardOf);
        } else {
            return drawnCards.countOf(usedCardOf) + drawnCards.countOf(Card.LOCOMOTIVE);
        }
    }

    /**
     * @return points given the length of the route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}
