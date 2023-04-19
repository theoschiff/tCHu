package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class GameState extends PublicGameState {


    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;


    /**
     * Constructor method for GameState
     *
     * @param tickets: tickets given
     * @param rng:     used to shuffle the tickets and the deck
     * @return instance of GameState to initialize the game
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        // creates the deck of cards from all the cards
        Deck<Card> deckOfCards = Deck.of(Constants.ALL_CARDS, rng);
        // creates the deck of tickets using the tickets given
        Deck<Ticket> deckOfTickets = Deck.of(tickets, rng);
        // maps a playerState to the player's ID
        Map<PlayerId, PlayerState> playerState = new EnumMap<PlayerId, PlayerState>(PlayerId.class);
        // distributes the top 4 cards of the deck of cards for each player
        for (PlayerId id : PlayerId.ALL) {
            playerState.put(id, PlayerState.initial(deckOfCards.topCards(Constants.INITIAL_CARDS_COUNT)));
            deckOfCards = deckOfCards.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

        // randomly selects the first player to play
        int rand = rng.nextInt(PlayerId.COUNT);
        PlayerId currentPlayer = PlayerId.ALL.get(rand);

        return new GameState(deckOfTickets, CardState.of(deckOfCards), currentPlayer, playerState, null);

    }

    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.tickets = Objects.requireNonNull(tickets);
        this.cardState = Objects.requireNonNull(cardState);
        this.playerState = Map.copyOf(playerState);
    }

    /**
     * @param playerId: player id of the player
     * @return the player state from the player Id
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * @return the player state of the current player
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * @param count: number of cards wanted
     * @return the cards at the top of the deck
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return tickets.topCards(count);
    }

    /**
     * @param count: number of cards wanted
     * @return a new instance without the top tickets
     * @throws IllegalArgumentException if count < 0 or count > deck of tickets' size
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @return the top card of the deck
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * @return a new instance without the top card
     */
    public GameState withoutTopCard() {
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param discardedCards: the discarded cards
     * @return a new instance with more discarded cards added to the discarded card deck
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param rng: randomly generated number
     * @return a new instance with a deck recreated if needed if then deck is empty
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return (!cardState.isDeckEmpty()) ? this
                : new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng),
                currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param playerId:      id of the current player
     * @param chosenTickets: tickets choses
     * @return a new instance with a new deck of tickets with the chosen tickets
     * @throws IllegalArgumentException if the ticket count of the player is equal to 0
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).ticketCount() == 0);
        Map<PlayerId, PlayerState> copyOfPlayerstate = new EnumMap<>(playerState);
        copyOfPlayerstate.replace(playerId, currentPlayerState().withAddedTickets(chosenTickets));
        return new GameState(tickets, cardState, currentPlayerId(), copyOfPlayerstate, lastPlayer());
    }

    /**
     * @param drawnTickets:  the drawn tickets
     * @param chosenTickets: the chosen tickets
     * @return a new instance with a new deck of with the chosen tickets from the drawn tickets
     * @throws IllegalArgumentException if the choses tickets are not included in the drawn tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> copyOfPlayerstate = new EnumMap<>(playerState);
        copyOfPlayerstate.replace(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));
        return new GameState(tickets.withoutTopCards(drawnTickets.size()),
                cardState, currentPlayerId(), copyOfPlayerstate, lastPlayer());
    }

    /**
     * @param slot: gives an index for a card placement
     * @return new instance with the drawn face up card
     * @throws IllegalArgumentException if the player can not draw a card
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> copyOfPlayerstate = new EnumMap<>(playerState);
        CardState card = cardState.withDrawnFaceUpCard(slot);
        copyOfPlayerstate.replace(currentPlayerId(), currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(tickets, card, currentPlayerId(), copyOfPlayerstate, lastPlayer());
    }

    /**
     * @return new instance with drawn card and without the top card of the deck
     * @throws IllegalArgumentException if the player can not draw a card
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> copyOfPlayerstate = new EnumMap<>(playerState);
        copyOfPlayerstate.replace(currentPlayerId(), currentPlayerState().withAddedCard(cardState.topDeckCard()));
        return new GameState(tickets, cardState.withoutTopDeckCard(),
                currentPlayerId(), copyOfPlayerstate, lastPlayer());
    }

    /**
     * @param route: route given
     * @param cards: cards given
     * @return new instance with the route taken using the cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> copyOfPlayerstate = new EnumMap<>(playerState);
        copyOfPlayerstate.replace(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));
        return new GameState(tickets, cardState.withMoreDiscardedCards(cards),
                currentPlayerId(), copyOfPlayerstate, lastPlayer());
    }

    /**
     * @return checks if it is the last turn
     */
    public boolean lastTurnBegins() {
        return playerState.get(currentPlayerId()).carCount() <= 2 && lastPlayer() == null;
    }

    /**
     * @return instance with the last player as the current player if it is the last turn
     */
    public GameState forNextTurn() {
        return (lastTurnBegins())
                ? new GameState(tickets, cardState, currentPlayerId().next(), playerState, currentPlayerId())
                : new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastPlayer());
    }

}
