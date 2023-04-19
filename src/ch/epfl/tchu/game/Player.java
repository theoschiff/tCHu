package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public interface Player {

    public enum TurnKind {

        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        public static final List<TurnKind> ALL = List.of(TurnKind.values());

    }

    /**
     * called at the beginning of a game to communicate player's own ID
     * and other player's id including his
     *
     * @param ownId:       player's own ID
     * @param playerNames: other player's id including his
     */
    public abstract void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * to communicate an info to a player
     *
     * @param info: given information
     */
    public abstract void receiveInfo(String info);

    /**
     * called each time the game state has changed,
     * to inform the player of the public component of the new state
     *
     * @param newState: new state
     * @param ownState: player's state
     */
    public abstract void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * called at the start of the game to inform
     * the player of the five tickets that have been distributed
     *
     * @param tickets: 5 tickets distributes
     */
    public abstract void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * called at the start of the game to ask the player
     * which of the tickets he was initially given
     */
    public abstract SortedBag<Ticket> chooseInitialTickets();

    /**
     * called at the start of a player’s turn,
     * to find out what kind of action he wants to perform during this turn
     */
    public abstract TurnKind nextTurn();

    /**
     * called when the player has decided to draw additional tickets during the game,
     * to inform him of the tickets drawn and to know which ones he keeps
     *
     * @param options: supplementary drawn tickets
     */
    public abstract SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * called when the player has decided to draw wagon/locomotive cards,
     * in order to know where he wants to draw them from:
     * from one of the slots containing a face-up card
     * (in which case the value returns is between 0 and 4 inclusive),
     * or from the stockpile — in which case the value returned is Constants. DECK_SLOT
     */
    public abstract int drawSlot();

    /**
     * called when the player has decided (to try to) take over a road,
     * to find out which road it is
     */
    public abstract Route claimedRoute();

    /**
     * called when the player has decided to (try to) take over a road,
     * to find out which card(s) he initially wants to use for this
     */
    public abstract SortedBag<Card> initialClaimCards();

    /**
     * called when the player has decided to try to take over a tunnel and additional cards are needed,
     * in order to know which card (s) he wishes to use for this purpose,
     * the possibilities having been passed to him as an argument;
     * if the returned multiset is empty,
     * it means that the player does not want (or cannot) choose one of these possibilities.
     *
     * @param options: possibilities of additional necessary cards
     */
    public abstract SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);


}
