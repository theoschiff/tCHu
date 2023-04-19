package ch.epfl.tchu.other;

import ch.epfl.tchu.game.PlayerId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.epfl.tchu.game.PlayerId.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerIdTest {

        @Test
        void playerIdAllIsDefinedCorrectly() {
            assertEquals(List.of(PlayerId.values()), ALL);
        }

        @Test
        void playerIdCountIsDefinedCorrectly(){
            assertEquals(List.of(PlayerId.values()).size(), COUNT);
        }

        @Test
        void nextWorksCorrectly(){
            assertEquals(PLAYER_1, PLAYER_2.next());
            assertEquals(PLAYER_2, PLAYER_1.next());
        }

}
