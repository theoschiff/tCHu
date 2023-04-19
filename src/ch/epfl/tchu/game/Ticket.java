package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;

    private final String text;

    /**
     * primary constructor, requires 1 parameter
     *
     * @param trips: list of trips
     * @throws IllegalArgumentException if the list of trips is empty
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        String tripStart = trips.get(0)
                .from()
                .name();

        for (Trip t : trips) {
            Preconditions.checkArgument(tripStart.equals(t.from().name()));
        }

        this.trips = List.copyOf(trips);
        text = computeText(tripStart, trips);
    }

    /**
     * 2nd constructor, requires 3 parameters
     *
     * @param from:   station of departure
     * @param to:     station of arrival
     * @param points: ticket's number of points
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * computes the textual representation of a ticket
     *
     * @param tripStart: the start of the trip
     * @param trips:     list of trips
     * @return textual ticket
     */
    private static String computeText(String tripStart, List<Trip> trips) {
        TreeSet<String> allTrips = new TreeSet<>();
        StringBuilder sBuild = new StringBuilder();
        for (Trip w : trips) {
            allTrips.add(w.to().toString());
        }
        sBuild.append(tripStart).append(" - ");
        if (trips.size() > 1) {
            sBuild.append("{");
        }

        for (String s : allTrips) {

            sBuild.append(s).append(" (");
            for (Trip t : trips) {
                if (t.to().name().equals(s)) {
                    sBuild.append(t.points());
                    break;
                }
            }
            sBuild.append("), ");
        }
        sBuild.replace(sBuild.length() - 2, sBuild.length(), "");
        if (trips.size() > 1) {
            sBuild.append("}");
        }
        return sBuild.toString();

    }


    /**
     * @return String, getter for textual representation of a ticket
     */
    public String text() {
        return text;
    }

    /**
     * @return String text,converts in String as explained above
     */
    @Override
    public String toString() {
        return text();
    }

    /**
     * compares two tickets by alphabetical order
     *
     * @param that: the ticket that is compared to this ticket
     * @return an int that indicates if there is difference between
     * both strings of tickets
     */
    @Override
    public int compareTo(Ticket that) {
        return this.toString().compareTo(that.toString());
    }


    /**
     * computes the points of a ticket
     *
     * @param connectivity: connectivity of the trail
     * @return ticket's number of points
     */
    public int points(StationConnectivity connectivity) {
        int points = Integer.MIN_VALUE;
        for (Trip t : trips) {
            points = Math.max(points, t.points(connectivity));
        }
        return points;
    }
}
