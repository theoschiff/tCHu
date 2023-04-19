package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cardDeck;

    /**
     * factory method for deck
     *
     * @param cards: Sorted bags of cards of type C
     * @param rng:   generates random numbers to shuffle the cards
     * @param <C>:   Type of Generic Function
     * @return a Deck of Cards with same number of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> cardD = new ArrayList<>(cards.toList());
        Collections.shuffle(cardD, rng);
        return new Deck<>(cardD);
    }

    private Deck(List<C> cardDeck) {
        this.cardDeck = cardDeck;
    }


    /**
     * getter for deck's size
     *
     * @return size of the deck
     */
    public int size() {
        return cardDeck.size();
    }


    /**
     * @return true if deck is empty
     */
    public boolean isEmpty() {
        return cardDeck.isEmpty();
    }


    /**
     * @return Deck's Top Card
     * @throws IllegalArgumentException if deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!cardDeck.isEmpty());
        return cardDeck.get(0);
    }

    /**
     * @return identical deck as (this) but without the top card
     * @throws IllegalArgumentException if deck is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!cardDeck.isEmpty());
        List<C> copyDeck = new ArrayList<>(cardDeck);
        copyDeck.remove(0);
        return new Deck<>(copyDeck);
    }

    /**
     * @param count: number of cards at the top of the deck
     * @return a SortedBag with the number of cards
     * at the top of the deck
     * @throws IllegalArgumentException if count < 0 or count > deck's size
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());

        List<C> topCardsList = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            topCardsList.add(cardDeck.get(i));
        }
        return SortedBag.of(topCardsList);
    }

    /**
     * @param count: number of cards at the top of the deck
     * @return identical deck as this but without top count cards
     * @throws IllegalArgumentException if count < 0 or count > deck's size
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        List<C> subDeck = cardDeck.subList(count, size());
        return new Deck<>(subDeck);
    }
}
