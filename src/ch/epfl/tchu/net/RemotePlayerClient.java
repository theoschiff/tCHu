package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public class RemotePlayerClient {

    private final BufferedReader r;
    private final BufferedWriter w;
    private final Player player;

    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;

        try {
            Socket gameSocket = new Socket(name, port);
            r = new BufferedReader(new InputStreamReader(gameSocket.getInputStream(),
                                    US_ASCII));
            w = new BufferedWriter(new OutputStreamWriter(gameSocket.getOutputStream(),
                                    US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }

    public void run(){
        while(readL() != null){
            //TODO: do we need to call nextTurn()?
            Player.TurnKind turnKind = player.nextTurn();
            String message = readL();
            String[] split = message.split(Pattern.quote(" "), -1);
            MessageId mess = MessageId.valueOf(split[0]);
            //TODO: Switch of mess or switch of Turnkind?
            switch (mess){
                case INIT_PLAYERS:
                    //RemotePlayerProxy.initPlayers();
            }
        }
    }
    private String readL() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
