package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Station {

    private final int id;

    private final String name;

    /**
     * constructor of station
     *
     * @param id,   number of the station
     * @param name, name of the station
     * @throws IllegalArgumentException if the id of the player is not
     *                                  a positive integer
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * getter for station's id
     *
     * @return id of the station
     */
    public int id() {
        return this.id;
    }

    /**
     * getter for station's name
     *
     * @return name of the station
     */
    public String name() {
        return this.name;
    }

    /**
     * @return in a string the name of the station
     */
    @Override
    public String toString() {
        return name();
    }
}

