package ch.epfl.tchu.game;

/**
 * Interface to represent network's connectivity
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public interface StationConnectivity {
    public abstract boolean connected(Station s1, Station s2);
}
