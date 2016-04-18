package eu.sdi4apps.pgrouting.utils;

import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.types.LonLat;

/**
 * 
 * 
 * @author runarbe
 */
public class RoutingSQL {

    /**
     * Get SQL to retrieve a node by its id
     *
     * @param nodeId
     * @return SQL string
     */
    public static String NodeById(int nodeId) {
        return "SELECT id, lon, lat, 0 as dist"
                + " FROM ways_vertices_pgr"
                + " WHERE id = " + nodeId;
    }

    /**
     * Get SQL to retrieve the nearest node to a lat/lon point
     *
     * @param longitude Longitude
     * @param latitude Latitude
     * @param radius Search radius in meters
     * @return SQL string
     */
    public static String NearestNode(double longitude, double latitude, int radius) {

        double radiusInDegrees = radius / LonLat.metersPerDegree(longitude, latitude);
        
        BBox bbox = BBox.createFromPoint(longitude, latitude, radiusInDegrees);

        return "SELECT id, lon, lat, ST_Distance(ST_SetSRID(ST_Point(" + longitude + "," + latitude + "),4326),the_geom) as dist"
                + " FROM ways_vertices_pgr"
                + " WHERE the_geom && " + bbox.wktEnvelope() + " AND ST_PointInsideCircle(the_geom, " + longitude + ", " + latitude + ", " + radiusInDegrees + ")"
                + " ORDER BY 4 ASC"
                + " LIMIT 1";
    }

    /**
     * Get SQL string for cal
     *
     * @param fromNode
     * @param toNode
     * @param bbox
     * @return SQL string
     */
    public static String Dijkstra(int fromNode, int toNode, BBox bbox) {
        return "SELECT a.seq, w.length_m as cost, w.name as streetname, ST_AsGeoJSON(w.the_geom, 7, 1) as geom"
                + " FROM pgr_dijkstra("
                + NodesSubSelect(bbox) + ", " + fromNode + ", " + toNode + ", true, true) a"
                + " LEFT JOIN ways w"
                + " ON (w.gid = a.id2) WHERE the_geom is not null";
    }

    public static String NodesSubSelect(BBox bbox) {
        String sql = "SELECT gid::integer as id, source::integer, target::integer, cost::double precision, reverse_cost::double precision FROM ways";
        if (bbox != null) {
            sql += String.format(" WHERE the_geom && %s", bbox.wktEnvelope());
        }
        return String.format("'%s'", sql);
    }

    public static String ReachableArea() {
        return "";
    }

}
