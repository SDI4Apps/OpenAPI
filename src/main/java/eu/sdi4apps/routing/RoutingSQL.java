package eu.sdi4apps.routing;

import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.types.LonLat;
import org.apache.commons.lang.StringUtils;

/**
 * Class with SQL methods
 */
public class RoutingSQL {

    /**
     * SQL to retrieve a node by id
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
     * SQL to retrieve node by proximity to node
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
     * SQL to retrieve shortest path using the Dijkstra algorithm
     *
     * @param fromNode
     * @param toNode
     * @param bbox
     * @return SQL string
     */
    public static String Dijkstra(int fromNode, int toNode, BBox bbox) {
        String sql = "SELECT a.seq, (w.km * 1000) as cost, w.osm_name as streetname, ST_AsGeoJSON(w.geom_way, 7, 1) as geom"
                + " FROM pgr_dijkstra("
                + NodesSubSelect(bbox) + ", " + fromNode + ", " + toNode + ", false, false) a"
                + " LEFT JOIN ways w"
                + " ON (w.id = a.id2) WHERE w.geom_way is not null";
        System.out.println(sql);
        return sql;
    }

    /**
     * SQL to retrieve subset of nodes to operate on
     *
     * @param bbox
     * @return
     */
    public static String NodesSubSelect(BBox bbox) {
        String sql = "SELECT id::integer as id, source::integer, target::integer, cost::double precision, reverse_cost::double precision FROM ways";
        if (bbox != null) {
            sql += String.format(" WHERE geom_way && %s", bbox.wktEnvelope());
        }
        return String.format("'%s'", sql);
    }

    /**
     * SQL to calculate reachable area from a start node
     *
     * @param fromNode
     * @param distance
     * @param alpha
     * @return
     */
    public static String ReachableArea(int fromNode, int distance, double alpha) {
        return "CREATE TEMPORARY TABLE test ON COMMIT PRESERVE ROWS AS (SELECT * FROM ways_vertices_pgr v"
                + " JOIN (SELECT * FROM pgr_drivingdistance("
                + "'SELECT id::integer as id, source::integer, target::integer, (km*1000)::double precision as cost, (km*1000)::double precision as reverse_cost FROM ways'::text,"
                + fromNode + "::bigint,"
                + distance + "::double precision,"
                + "false,"
                + "false)) r ON (v.id = r.id1));"
                + "SELECT st_asgeojson(pgr_pointsaspolygon('SELECT id::integer, lon::float8 as x, lat::float8 as y FROM test WHERE cost < " + distance + "', " + alpha + "));";
    }

    /**
     * Return the optimal order for a set of points to be visited
     *
     * @param nodeIds
     * @param startId
     * @param finishId
     * @return
     */
    public static String OptimizeNodeOrder(Integer[] nodeIds, Integer startId, Integer finishId) {
        return "SELECT id2 as id FROM pgr_tsp "
                + "( 'SELECT id::integer, lon::float as x, lat::float as y "
                + "FROM ways_vertices_pgr "
                + "WHERE id in (" + StringUtils.join(nodeIds, ", ") + ") "
                + "ORDER BY id', " + startId + ", " + finishId + ")";
    }

}
