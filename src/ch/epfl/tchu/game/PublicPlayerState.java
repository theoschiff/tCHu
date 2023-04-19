package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * Constructor of Public Player State
     *
     * @param ticketCount:number of tickets
     * @param cardCount:         number of cards
     * @param routes:            route that the player has taken
     * @throws IllegalArgumentException if the count of the tickets and the cards is
     *                                  bigger or equal to 0
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {

        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);

        int count = 0;
        int consPoints = 0;
        for (Route r : routes) {
            count += r.length();
            consPoints += r.claimPoints();
        }

        this.carCount = Constants.INITIAL_CAR_COUNT - count;
        this.claimPoints = consPoints;
    }


    /**
     * getter for number of tickets
     *
     * @return player's number of ticket
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * getter for number of cards
     *
     * @return player's number of cards
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * getter for routes' list
     *
     * @return player's taken routes
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * getter for number of wagons
     *
     * @return player's number of wagons
     */
    public int carCount() {
        return carCount;
    }

    /**
     * @return the claim points of the player
     */
    public int claimPoints() {
        return claimPoints;
    }
}
