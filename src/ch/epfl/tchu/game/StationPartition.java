package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] subSet;



    /**
     * Check whether two stations are connected
     * @param s1: Station1
     * @param s2: Station2
     * @return true if two stations are connected
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= subSet.length || s2.id() >= subSet.length){
            return s1.id() == s2.id();
        }
        return (subSet[s1.id()] == subSet[s2.id()]);
    }

    private StationPartition(int[] subSet){
        this.subSet = subSet.clone();
    }

    public static final class Builder{

        private final int[] intTab;

        /**
         * Constructor for builder
         * @param stationCount: int representing the values of stations
         * @throws IllegalArgumentException if stationCount is negative
         */
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount >= 0);
            this.intTab = new int[stationCount];
            for( int i = 0 ; i < stationCount ; ++i){
                intTab[i] = i;
            }
        }

        /**
         * @param s1: Station1
         * @param s2: Station1
         * @return builder with the joint stations
         */
        public Builder connect(Station s1, Station s2){
            intTab[representative(s2.id())] = representative(s1.id());
            return this;
        }

        /**
         * builds from the deep partition
         * @return stations' flattened partition
         */
        public StationPartition build(){
            for(int i = 0; i < intTab.length; ++i){
                intTab[i] = representative(i);
            }
            return new StationPartition(intTab);
        }

        private int representative(int idStation){
            int id = idStation;
            while(intTab[id] != id){
                id = intTab[id];
            }
            return id;
        }
    }
}
