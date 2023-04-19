package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.sql.SQLOutput;
import java.util.*;

public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;


    /**
     * Constructor of PlayerState
     *
     * @param tickets: player's tickets
     * @param cards:   player's cards
     * @param routes:  list of player's taken routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);

    }

    /**
     * factory method of PlayerState
     *
     * @param initialCards: player's intial cards
     * @return Player's state with no initial tickets,
     * initial cards, and no initial Routes taken
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        List<Route> emptyRoutes = new ArrayList<>();
        return new PlayerState(SortedBag.of(), initialCards, emptyRoutes);
    }

    /**
     * getter for player's tickets
     *
     * @return Sorted bag of Tickets
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * @param newTickets: tickets we want to add
     *                    to our ticket sorted bag
     * @return new identical Player State with the new tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> unionTickets = tickets.union(newTickets);
        return new PlayerState(unionTickets, cards, routes());
    }

    /**
     * getter for Cards
     *
     * @return Sorted Bag of Cards
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * @param card: Card we want to add
     * @return identical state with the added card
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> addedCard = cards.union(SortedBag.of(card));
        return new PlayerState(tickets, addedCard, routes());
    }

    /**
     * @param additionalCards: Cards we want to add
     * @return identical state with the added cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        SortedBag<Card> uniCards = cards.union(additionalCards);
        return new PlayerState(tickets, uniCards, routes());
    }

    /**
     * Determines if player can claim route
     *
     * @param route: route that the player claims
     * @return true if he has sufficient cars and necessary cards
     */
    public boolean canClaimRoute(Route route) {
        return (carCount() >= route.length() &&
                (!possibleClaimCards(route).isEmpty()));

    }

    /**
     * @param route: route that player wants to take
     * @return list of all possible cards that player can use
     * to claim a route
     * @throws IllegalArgumentException if player doesn't have
     *                                  enough cars and cards to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());

        List<SortedBag<Card>> possClaimCards = new ArrayList<>();
        for (SortedBag<Card> c : route.possibleClaimCards()) {
            if (cards.contains(c)) {
                possClaimCards.add(c);
            }
        }
        return possClaimCards;
    }

    /**
     * @param additionalCardsCount: number of additional cards
     * @param initialCards: cards used to take the route
     * @param drawnCards: cards drawn from the deck
     * @return list of all sets of cards that player
     * can use to claim a tunnel
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {

        Preconditions.checkArgument(additionalCardsCount >= 1
                && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);

        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        SortedBag<Card> cardsToUse = cards.difference(initialCards);

        List<SortedBag<Card>> options = new ArrayList<>();
        Color usedColor = null;

        for (Card c : initialCards) {
            if (c.color() != null) {
                usedColor = c.color();
            }

        }
        if (usedColor != null) {
            for (int i = 0; i <= additionalCardsCount; ++i) {
                options.add(SortedBag.of(i, Card.of(usedColor), additionalCardsCount - i, Card.LOCOMOTIVE));
            }
        } else {
            options.add(SortedBag.of(additionalCardsCount, Card.LOCOMOTIVE));
        }

        options.removeIf(cards -> !cardsToUse.contains(cards));
        options.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;
    }


    /**
     * @param route: route you want to claim
     * @param claimCards: cards player uses to claim route
     * @return identical state as this, with a route added
     * and claim cards removed from deck
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> addedRoute = new ArrayList<>(routes());
        addedRoute.add(route);
        SortedBag<Card> diff = cards.difference(claimCards);
        return new PlayerState(tickets, diff, addedRoute);

    }

    /**
     * @return number of points obtained from player's tickets
     */
    public int ticketPoints() {
        int max = 0;
        for (Route route : routes()) {
            if (max <= Math.max(route.station1().id(), route.station2().id())) {
                max = Math.max(route.station1().id(), route.station2().id());
            }
        }

        StationPartition.Builder stationPart = new StationPartition.Builder(max + 1);
        for (Route r : routes()) {
            stationPart.connect(r.station1(), r.station2());
        }

        StationPartition builtStation = stationPart.build();
        int points = 0;

        for (Ticket t : tickets) {
            points += t.points(builtStation);
        }

        return points;
    }

    /**
     * total number of points
     *
     * @return sum of claimPoints and ticketPoints
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

}
