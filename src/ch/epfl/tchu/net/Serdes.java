package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;
import ch.epfl.tchu.SortedBag;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public class Serdes {

    private static final String VIRGULE = ",";
    private static final String POINTVIRGULE = ";";
    private static final String DEUXPOINTS = ":";

    private Serdes() {
    }

    /**
     * Serde for the integers
     */
    public static final Serde<Integer> INTEGER = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     * Serde for the strings
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            Serdes::encoder,
            Serdes::decoder);

    /**
     * Serde for the player id
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde for the turn kinds
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde for the cards
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Serde for the routs
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Serde for the tickets
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Serde for a list of strings
     */
    public static final Serde<List<String>> LIST_OF_STRING = Serde.listOf(STRING_SERDE, VIRGULE);

    /**
     * Serde for a list of cards
     */
    public static final Serde<List<Card>> LIST_OF_CARD = Serde.listOf(CARD_SERDE, VIRGULE);

    /**
     * Serde for a list of routs
     */
    public static final Serde<List<Route>> LIST_OF_ROUTE = Serde.listOf(ROUTE_SERDE, VIRGULE);

    /**
     * Serde for a SortedBag of cards
     */
    public static final Serde<SortedBag<Card>> BAG_OF_CARD = Serde.bagOf(CARD_SERDE, VIRGULE);

    /**
     * Serde for a SortedBag of tickets
     */
    public static final Serde<SortedBag<Ticket>> BAG_OF_TICKET = Serde.bagOf(TICKET_SERDE, VIRGULE);

    /**
     * Serde for a list of SortedBag of cards
     */
    public static final Serde<List<SortedBag<Card>>> LIST_OF_BAG_OF_CARD = Serde.listOf(BAG_OF_CARD, POINTVIRGULE);

    /**
     * Serde for a PublicCardState
     */
    public static final Serde<PublicCardState> PCS_SERDE = Serde.of(
            publicCardState ->
                    String.join(POINTVIRGULE, LIST_OF_CARD.serialize(publicCardState.faceUpCards()),
                            INTEGER.serialize(publicCardState.deckSize()),
                            INTEGER.serialize(publicCardState.discardsSize()))
            ,
            str -> {
                String[] strings = str.split(Pattern.quote(POINTVIRGULE), -1);
                return new PublicCardState(LIST_OF_CARD.deserialize(strings[0]),
                        INTEGER.deserialize(strings[1]),
                        INTEGER.deserialize(strings[2]));
            }
    );

    /**
     * Serde for a PublicPlayerState
     */
    public static final Serde<PublicPlayerState> PPS_SERDE = Serde.of(
            publicPlayerState ->
                    String.join(POINTVIRGULE, INTEGER.serialize(publicPlayerState.ticketCount()),
                            INTEGER.serialize(publicPlayerState.cardCount()),
                            LIST_OF_ROUTE.serialize(publicPlayerState.routes()))
            ,
            str -> {
                String[] strings = str.split(Pattern.quote(POINTVIRGULE), -1);
                return new PublicPlayerState(INTEGER.deserialize(strings[0]),
                        INTEGER.deserialize(strings[1]),
                        LIST_OF_ROUTE.deserialize(strings[2]));
            }
    );

    /**
     * Serde for a PlayerState
     */
    public static final Serde<PlayerState> PS_SERDE = Serde.of(
            playerState ->
                    String.join(POINTVIRGULE, BAG_OF_TICKET.serialize(playerState.tickets()),
                            BAG_OF_CARD.serialize(playerState.cards()),
                            LIST_OF_ROUTE.serialize(playerState.routes()))
            ,
            str -> {
                String[] strings = str.split(Pattern.quote(POINTVIRGULE), -1);
                return new PlayerState(BAG_OF_TICKET.deserialize(strings[0]),
                        BAG_OF_CARD.deserialize(strings[1]),
                        LIST_OF_ROUTE.deserialize(strings[2]));
            }
    );

    /**
     * Serde for a PublicGameState
     */
    public static final Serde<PublicGameState> PGS_SERDE = Serde.of(
            publicGameState ->
                    String.join(DEUXPOINTS, INTEGER.serialize(publicGameState.ticketsCount()),
                            PCS_SERDE.serialize(publicGameState.cardState()),
                            PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()),
                            PPS_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)),
                            PPS_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)),
                            PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()))
            ,
            str -> {
                String[] strings = str.split(Pattern.quote(DEUXPOINTS), -1);
                return new PublicGameState(INTEGER.deserialize(strings[0]),
                        PCS_SERDE.deserialize(strings[1]),
                        PLAYER_ID_SERDE.deserialize(strings[2]),
                        Map.of(PlayerId.PLAYER_1, PPS_SERDE.deserialize(strings[3]),
                                PlayerId.PLAYER_2, PPS_SERDE.deserialize(strings[4]))
                        ,
                        PLAYER_ID_SERDE.deserialize(strings[5]));
            }
    );


    private static String encoder(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    private static String decoder(String str) {
        byte[] dec = Base64.getDecoder().decode(str);
        return new String(dec);
    }
}
