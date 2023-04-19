package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.List;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructor of Public Game State
     *
     * @param ticketsCount:    ticket deck's size
     * @param cardState:       cards' public state
     * @param currentPlayerId: current player
     * @param playerState:     public state of the player
     * @param lastPlayer:      last player's state, can be null
     * @throws IllegalArgumentException if ticketsCount negative,
     *                                  and if size of playerstate is different
     * @throws NullPointerException     if other attributes except last player are null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState,
                           PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState,
                           PlayerId lastPlayer) {

        Preconditions.checkArgument(ticketsCount >= 0);

        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * getter of ticketsCount
     *
     * @return number of tickets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * @return true if it is possible to draw a ticket
     */
    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    /**
     * getter for public state of cards
     *
     * @return public state of cards
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * tells if the player can draw cards
     *
     * @return true if it is possible to draw cards,
     * if the pile and the discard contain at least 5 cards
     */
    public boolean canDrawCards() {
        return (cardState.deckSize()
                + cardState.discardsSize()) >= 5;
    }

    /**
     * getter for current player's id
     *
     * @return current player's id
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * @param playerId: player's id
     * @return the public part of the state of the given identity player
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * getter for the current player state
     *
     * @return the public part of the current player’s state
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * @return totality of the roads that one or other of the players has taken
     */
    public List<Route> claimedRoutes() {
        List<Route> playersRoutes = new ArrayList<>();

        playersRoutes.addAll(playerState.get(PlayerId.PLAYER_1).routes());
        playersRoutes.addAll(playerState.get(PlayerId.PLAYER_2).routes());

        return playersRoutes;
    }

    /**
     * @return the identity of the last player,
     * or null if not yet known because the last turn has not started
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
