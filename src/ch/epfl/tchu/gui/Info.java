package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Info {

    private final String playerName;

    /**
     * Constructor for Info
     *
     * @param playerName: name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @param card: the card given
     * @param count: the number of the card given
     * @return the color of the card in french, with a "s" at the end of
     * the word if it is plural
     */
    public static String cardName(Card card, int count) {
        Preconditions.checkArgument(card != null);
        Color cardColor = card.color();
        String cardName = "";
        if(cardColor == null){
            cardName += StringsFr.LOCOMOTIVE_CARD;
            cardName += StringsFr.plural(count);
            return cardName;
        }
        switch (cardColor) {
            case BLACK:
                cardName += StringsFr.BLACK_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case VIOLET:
                cardName += StringsFr.VIOLET_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case BLUE:
                cardName += StringsFr.BLUE_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case GREEN:
                cardName += StringsFr.GREEN_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case YELLOW:
                cardName += StringsFr.YELLOW_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case ORANGE:
                cardName += StringsFr.ORANGE_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case RED:
                cardName += StringsFr.RED_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            case WHITE:
                cardName += StringsFr.WHITE_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
            default:
                cardName += StringsFr.LOCOMOTIVE_CARD;
                cardName += StringsFr.plural(count);
                return cardName;
        }
    }

    /**
     *
     * @param playerNames: a list of the players who have the same number of points
     * @param points: the number of points that the players share
     * @return a string that announces that the players given have drawn the game
     */
    public static String draw(List<String> playerNames, int points) {
        return  String.format(StringsFr.DRAW, playersOf(playerNames), points);
    }


    /**
     *
     * @return a string announcing who plays first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     *
     * @param count: the number of tickets kept
     * @return a string telling the players the number of tickets kept by the player
     */
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     *
     * @return a string announcing which player can play
     */
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     *
     * @param count: number of tickets drawn
     * @return a string that announces the number of tickets drawn by the player
     */
    public String drewTickets(int count){
        String plural = (count == 1)? "" : "s";
        return String.format(StringsFr.DREW_TICKETS, playerName, count, plural);
    }

    /**
     *
     * @return a string that announces the player that drew a card from the deck
     */
    public String drewBlindCard(){
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     *
     * @param card: the card that the player
     * @return a string that announces the color of the card drawn by the player
     */
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     *
     * @param route: the route that the player has claimed
     * @param initialCards: the cards used to take possession of the route
     * @return a string that announces the route claimed and the cards used to claim it
     */
    public String claimedRoute(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, stationsOf(route), cardsOf(initialCards));
    }

    /**
     *
     * @param route: the route that the player wants to claim
     * @param initialCards: the cards that the player is going to use to take possession
     *                    of the route
     * @return a string that announces the player that is attempting to take the route
     * with the set of cards given
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, stationsOf(route), cardsOf(initialCards));
    }

    /**
     *
     * @param drawnCards: the cards drawn
     * @param additionalCost: the additional cost
     * @return a string that announces the additional cards and if the player needs an additional cost
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String str = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardsOf(drawnCards));
        return (additionalCost > 0)? str
                + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost))
                : str + StringsFr.NO_ADDITIONAL_COST;
    }

    /**
     *
     * @param route: the route that the player did not claim
     * @return a string that announces the route that the player could not acquire or did not want to
     * acquire
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, stationsOf(route));
    }

    /**
     *
     * @param carCount:the number of cars that the player has left
     * @return a string that announces that the last turn has begun and says the number of cars the
     * player has left
     */
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     *
     * @param longestTrail: longest trail of the game
     * @return a string that announces the player that receives the bonus and says that first station
     * and the last station
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        return String.format(StringsFr.GETS_BONUS, playerName, stationsOf(longestTrail));
    }

    /**
     *
     * @param points: points of the player that won
     * @param loserPoints: points of the player that lost
     * @return a string that announces the player that won the game and his points and compares his
     * points to the points of the player that lost
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, playerName, points,StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    private static String stationsOf(Route route){
        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    private static String stationsOf(Trail trail){
        return trail.station1().name() + StringsFr.EN_DASH_SEPARATOR + trail.station2().name();
    }

    private static String cardsOf(SortedBag<Card> cards){

        SortedBag<Card> cardSet = SortedBag.of(cards);

        List<String> str = new ArrayList<>();
        for(Card c: cardSet.toSet()){
            int n = cardSet.countOf(c);
            str.add(n + " " + cardName(c, n));
        }

        if(str.size() == 1){
            return str.get(0);
        }

        String string = String.join(", ", str.subList(0, str.size()-1));
        string += StringsFr.AND_SEPARATOR + str.get(str.size()-1);

        return string;


    }

    private static String playersOf(List<String> playerNames){
        String string = "";
        List<String> playerNamesWithoutLast = new ArrayList<>(playerNames);
        playerNamesWithoutLast
                .remove(playerNamesWithoutLast
                        .get(playerNamesWithoutLast.size()-1));
        string += String.join(", ", playerNamesWithoutLast);
        string += StringsFr.AND_SEPARATOR + playerNames.get(playerNames.size()-1);
        return string;
    }


}
