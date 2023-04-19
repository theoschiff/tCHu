package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discardCards;


    /**
     * factory method for Cardstate
     *
     * @param deck: deck of cards
     * @return a CardState with 5 first cards of the deck, face up,
     * deck contains rest of the cards, discards' empty
     * @throws IllegalArgumentException if size of deck is smaller than the face
     * up cards count
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> topCards = new ArrayList<>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            topCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return new CardState(topCards, deck, SortedBag.of());
    }


    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardCards) {
        super(faceUpCards, deck.size(), discardCards.size());
        this.deck = deck;
        this.discardCards = discardCards;
    }

    /**
     * @param slot: gives an index for a card placement
     * @return an identical set of cards to the receiver (this),
     * except that the face-up index slot card has been replaced by the one at the top of the stockpile,
     * which is removed at the same time
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!isDeckEmpty());

        List<Card> faceUpCardsCopy = new ArrayList<>(faceUpCards());

        faceUpCardsCopy.set(slot, topDeckCard());
        Deck<Card> deckNoTopCard = deck.withoutTopCard();

        return new CardState(faceUpCardsCopy, deckNoTopCard, discardCards);
    }


    /**
     * @return Card at the top of the deck
     * @throws IllegalArgumentException if deck is empty
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * @return identical cardState as this but without deck's top card
     * @throws IllegalArgumentException if deck is empty
     */

    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), discardCards);
    }

    /**
     * @param rng: random variable to shuffle the discarded cards
     * @return identical cardstate with the deck
     * consisting of the discards shuffled and an empty discards deck
     * @throws IllegalArgumentException if deck is empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        SortedBag<Card> shuffleDiscard = SortedBag.of(discardCards);
        Deck<Card> shuffledDeck = Deck.of(shuffleDiscard, rng);
        return new CardState(faceUpCards(), shuffledDeck, SortedBag.of());
    }


    /**
     * @param additionalDiscards: cards added to the discard
     * @return new Card state with added cards in the discard deck
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {

        return new CardState(faceUpCards(), deck, discardCards.union(additionalDiscards));
    }
}
