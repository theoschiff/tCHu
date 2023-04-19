package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    private static final int DECK_HAS_NO_CARDS = 0;

    /**
     * @param faceUpCards:  List of Card placed face up
     * @param deckSize:     size of the face down deck
     * @param discardsSize: size of the discard
     */
    //TODO: Check
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= DECK_HAS_NO_CARDS);
        Preconditions.checkArgument(discardsSize >= DECK_HAS_NO_CARDS);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * @return total size of all ''public'' cards
     * that are not in the player's hands
     */
    public int totalSize() {
        return this.faceUpCards.size()
                + this.deckSize
                + this.discardsSize;
    }

    /**
     * @return List of the cards facing up
     */

    public List<Card> faceUpCards() {
        return this.faceUpCards;
    }

    /**
     * @param slot: gives an index for a card placement
     * @return card facing up at slot's index given
     * @throws IndexOutOfBoundsException if slot not included between
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * getter for deck's size
     * @return size of the deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * @return true if deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == DECK_HAS_NO_CARDS;
    }

    /**
     * getter for discard size
     * @return size of the discards
     */
    public int discardsSize() {
        return discardsSize;
    }

}
