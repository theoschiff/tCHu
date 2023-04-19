package ch.epfl.tchu.net;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 * enumerates types of message the server can
 * send to the client
 */
public enum MessageId {

    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;

}
