package eu.sdi4apps.routing.types;

/**
 * Node class
 *
 * @author runarbe
 */
public class Node {

    public int id;

    public double lon;

    public double lat;

    public double dist;

    /**
     * Create a new node
     *
     * @param id
     * @param lon
     * @param lat
     * @param dist
     */
    public Node(int id, double lon, double lat, double dist) {

        this.id = id;

        this.lon = lon;

        this.lat = lat;

        this.dist = dist;

    }

}
