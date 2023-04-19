package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.StringsFr;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public interface Serde<T> {

    /**
     * serializes object
     *
     * @param obj: object to serialize
     * @return corresponding String
     */
    String serialize(T obj);

    /**
     * deserializes object
     *
     * @param str: String to deserialize
     * @return corresponding object
     */
    T deserialize(String str);

    /**
     * @param serialize:   serialization function
     * @param deserialize: deserialization function
     * @param <L>:         Type of object to serialize/deserialize
     * @return corresponding Serde
     */
    static <L> Serde<L> of(Function<L, String> serialize, Function<String, L> deserialize) {
        return new Serde<L>() {
            @Override
            public String serialize(L toS) {
                return serialize.apply(toS);
            }

            @Override
            public L deserialize(String toD) {
                return deserialize.apply(toD);
            }
        };
    }

    /**
     * @param list: list of all values of a listed set of values
     * @param <L>:  type of object to serialize/deserialize
     * @return corresponding serde
     */
    static <L> Serde<L> oneOf(List<L> list) {
        Preconditions.checkArgument(!list.isEmpty());
        return new Serde<L>() {

            @Override
            public String serialize(L toS) {
                int index = list.indexOf(toS);
                return Integer.toString(index);
            }

            @Override
            public L deserialize(String toD) {
                int index = Integer.parseInt(toD);
                return list.get(index);
            }
        };
    }


    /**
     * @param serde:     a serde of type Serde<L>
     * @param separator: separating string
     * @param <L>:       type of serde
     * @return a serde capable of (de)serializing lists of values (de)serialized by the given serde
     */
    static <L> Serde<List<L>> listOf(Serde<L> serde, String separator) {
        return new Serde<List<L>>() {
            @Override
            public String serialize(List<L> list) {
                if (list.isEmpty()) return "";
                else {
                    List<String> joined = new ArrayList<>();
                    for (L l : list) {
                        String toSerialize = serde.serialize(l);
                        joined.add(toSerialize);
                    }
                    return String.join(separator, joined);
                }
            }

            @Override
            public List<L> deserialize(String str) {
                String[] splittedStr = str.split(Pattern.quote(separator), -1);
                List<L> deserialized = new ArrayList<>();
                for (String s : splittedStr) {
                    deserialized.add(serde.deserialize(s));
                }
                return deserialized;
            }
        };
    }


        /**
         *
         * @param serde: a serde of type Serde<L>
         * @param separator: separating string
         * @param <L>: type of serde
         * @return  a serde capable of (de)serializing SortedBags of values (de)serialized by the given serde
         */
        static <L extends Comparable<L>>Serde<SortedBag<L>> bagOf (Serde < L > serde, String separator){
            Serde<List<L>> serdeList = listOf(serde, separator);
            return Serde.of(sortedBag ->  serdeList.serialize(sortedBag.toList()),
                    str -> SortedBag.of(serdeList.deserialize(str)));
        }

}

