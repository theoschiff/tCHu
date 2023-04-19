package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
class SerdeTest {
    @Test
    void testStrings(){
        List<String> strings = List.of("a", "b", "c", "d");
    }


    @Test
    void serializeandDeserializeWork() {
        Serde<Integer> intSerde = Serde.of(
                i -> Integer.toString(i),
                Integer::parseInt);
        assertEquals("2021", intSerde.serialize(2021));
        assertEquals(2021, intSerde.deserialize("2021"));
    }

    @Test
    void ofWorksNormally() {
    }

    @Test
    void oneOfWorksCorrectly() {
        Serde<Color> color = Serde.oneOf(Color.ALL);
        Serde<List<Color>> listOfColor = Serde.listOf(color, "+");
        Serde<SortedBag<Color>> bagOfColor = Serde.bagOf(color, "+");
        assertEquals("INIT_PLAYERS", MessageId.INIT_PLAYERS.name());
    }

    @Test
    void listOfWorksFine() {
        Serde<Color> color = Serde.oneOf(Color.ALL);
        Serde<List<Color>> listOfColor = Serde.listOf(color, "+");
        Serde<SortedBag<Color>> bagOfColor = Serde.bagOf(color, "+");
        assertEquals(Color.BLACK, color.deserialize("0"));
        //assertEquals(1111,listOfColor);
        //assertEquals(2222, bagOfColor);
    }

    @Test
    void bagOfisGucci() {
    }

    @Test
    void testTheSplitMethOfString(){
        String hello = "Hello i am a little bitch";
        String[] hey = hello.split(Pattern.quote(" "), -1);
        for(String s : hey){
            System.out.println(s);
        }
    }
}