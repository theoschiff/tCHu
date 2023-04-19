package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 * represents a proxy
 */
public class RemotePlayerProxy implements Player {

    private final BufferedReader r;
    private final BufferedWriter w;
    /**
     * constructor of RemotePlayerProxy
     * @param socket: socket used for the proxy
     */
    public RemotePlayerProxy(Socket socket) throws IOException {
         r = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                                US_ASCII));

         w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                                US_ASCII));
    }






    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames){

        Collection<String> playersValues = playerNames.values();
        List<String> playerNamesList = List.copyOf(playersValues);
        String initPlayers = String.join(" ", MessageId.INIT_PLAYERS.name(),
                Serdes.PLAYER_ID_SERDE.serialize(ownId),
                Serdes.LIST_OF_STRING.serialize(playerNamesList)) + "\n";
        write(initPlayers);
    }

    @Override
    public void receiveInfo(String info) {
        String receiveInfo = String.join(" ", MessageId.RECEIVE_INFO.name(),
                Serdes.STRING_SERDE.serialize(info)) + "\n";
        write(receiveInfo);
        //TODO: \n in the write or in the string?

    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String updateState = String.join(" ", MessageId.UPDATE_STATE.name() ,
                Serdes.PGS_SERDE.serialize(newState),
                Serdes.PS_SERDE.serialize(ownState)) + "\n";
        write(updateState);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String setInitialTicketChoice = String.join(" ", MessageId.SET_INITIAL_TICKETS.name(),
                Serdes.BAG_OF_TICKET.serialize(tickets)) + "\n";
        write(setInitialTicketChoice);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets(){
        //TODO: is it just this?
        write(MessageId.CHOOSE_INITIAL_TICKETS.name() + "\n");
        String initialTicket = readL();
        return Serdes.BAG_OF_TICKET.deserialize(initialTicket);
    }

    @Override
    public TurnKind nextTurn() {
        //TODO: do we need to write at the beginning of the method?
        write(MessageId.NEXT_TURN.name() + "\n");
        String nextTurn = readL();
        //String[] split = nextTurn.split(Pattern.quote(" "), -1);
        return Serdes.TURN_KIND_SERDE.deserialize(nextTurn);

    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        write(MessageId.CHOOSE_TICKETS.name()  + "\n");
        String chooseTickets = readL();
        return Serdes.BAG_OF_TICKET.deserialize(chooseTickets);
    }

    @Override
    public int drawSlot() {
        write(MessageId.DRAW_SLOT.name() + "\n");
        String drawSlot = readL();
        return Serdes.INTEGER.deserialize(drawSlot);
    }

    @Override
    public Route claimedRoute() {
        write(MessageId.ROUTE.name() + "\n");
        String claimedRoute = readL();
        return Serdes.ROUTE_SERDE.deserialize(claimedRoute);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        write(MessageId.CARDS.name() + "\n");
        String initialClaimCards = readL();
        return Serdes.BAG_OF_CARD.deserialize(initialClaimCards);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        write(MessageId.CHOOSE_ADDITIONAL_CARDS.name() + "\n");
        String chooseAdditionalCards = readL();
        return Serdes.BAG_OF_CARD.deserialize(chooseAdditionalCards);
    }

    private void write(String str){
        try {
            w.write(str);
            //TODO: Do we Need To Flush At the End?
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readL(){
        try{
            return r.readLine();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

}
