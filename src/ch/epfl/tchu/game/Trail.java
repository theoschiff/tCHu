package ch.epfl.tchu.game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Trail {

    private final Station station1;
    private final Station station2;
    private final List<Route> routeList;
    private final int length;

    /**
     * Gives the longest trail given a list of routes
     * @param routes: list of routes
     * @return longest trail possible between two stations
     */
    public static Trail longest(List<Route> routes){
        Trail longestTrail = new Trail(new ArrayList<>(), null, null, 0);
        List<Trail> cs = new ArrayList<>();
        //Adds trails to the List
        for(Route r : routes){
            cs.add(new Trail(List.of(r), r.station1(), r.station2(), r.length()));
            cs.add(new Trail(List.of(r), r.station2(), r.station1(), r.length()));
        }
        while(!cs.isEmpty()){
            List<Trail> cs1 = new ArrayList<>();
            for (Trail c: cs){
                List<Route> rs = new ArrayList<>();
                for(Route route : routes){
                    if (!c.routeList.contains(route) && ((c.station2() == route.station1())
                            || c.station2 == route.station2())){
                        rs.add(route);
                    }
                }
                for(Route r : rs){
                    List<Route> rt = new ArrayList<>(c.routeList);
                    rt.add(r);
                    cs1.add(new Trail(rt, c.station1, r.stationOpposite(c.station2),
                            c.length + r.length()));
                }
                if (c.length >= longestTrail.length){
                    longestTrail = c;
                }
            }
            cs = cs1;
        }

        return longestTrail;
    }

    /**
     * Constructor for Trail
     * @param routeList: list containing the list of routes
     * @param station1: first station, of departure
     * @param station2: second station, of arrival
     */
    private Trail(List<Route> routeList, Station station1, Station station2, int length){
        this.station1 = station1;
        this.station2 = station2;
        this.routeList = List.copyOf(routeList);
        this.length = length;

    }


    /**
     * @return the size of the list of routeList
     */
    public int length(){
        return length;
    }

    /**
     * @return the first station, of departure or null depending
     * on the first station
     */
    public Station station1(){
        return (length() !=0) ? station1 : null;
    }

    /**
     * @return the second station, of arrival or null depending
     * on the last station
     */
    public Station station2(){
        return (length() !=0) ? station2 : null;
    }


    /**
     * @return textual representation of a trail
     * iterate over all stations of the trail, keeps latest station
     * puts it in a list of string and
     *
     */
    @Override
    public String toString(){
        List<String> stationsName = new ArrayList<>();
        Station mem = station1;
        stationsName.add(mem.toString());
        for (Route r : routeList){
            stationsName.add(r.stationOpposite(mem).toString());
            mem = r.stationOpposite(mem);
        }
        return String.join(" - ", stationsName) + "(" + length + ")";
    }




}
