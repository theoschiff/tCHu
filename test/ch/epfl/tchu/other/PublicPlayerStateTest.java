package ch.epfl.tchu.other;


import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PublicPlayerState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicPlayerStateTest {
    @Test
    void constructorFailsWithIllegalParam() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(-1, 0, List.of());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(0, -1, List.of());
        });
    }

    @Test
    void gettersWorksCorrectly() {
        PublicPlayerState pPS = new PublicPlayerState(3, 5, List.of(ChMap.routes().get(0))); // length 4

        assertEquals(3, pPS.ticketCount());
        assertEquals(5, pPS.cardCount());
        assertEquals(36, pPS.carCount());
        assertEquals(7, pPS.claimPoints());
        assertEquals(List.of(ChMap.routes().get(0)), pPS.routes());
    }
}
