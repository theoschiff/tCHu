package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * @param from:   station of departure
     * @param to:     station of arrival
     * @param points: number of points
     */
    public Trip(Station from, Station to, int points) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);
        this.points = points;
    }

    /**
     * @param from:   list of stations that we take as first station
     * @param to:     list of stations that we take as the last station
     * @param points: the points the trip must equal to
     * @return a list of all possible trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        List<Trip> trips = new ArrayList<>();
        for (Station l : from) {
            for (Station s : to) {
                trips.add(new Trip(l, s, points));
            }
        }
        return trips;
    }

    /**
     * @return station of departure
     */
    public Station from() {
        return this.from;
    }

    /**
     * @return station of arrival
     */
    public Station to() {
        return this.to;
    }

    /**
     * @return number of points
     */
    public int points() {
        return this.points;
    }

    /**
     * computes the number of points
     *
     * @param connectivity: link between stations
     * @return number of points
     */
    public int points(StationConnectivity connectivity) {

        if (connectivity.connected(from, to)) {
            return points();
        } else {
            return -points();
        }
    }
}

