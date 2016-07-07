/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.routing;

import eu.sdi4apps.geojson.Feature;
import eu.sdi4apps.geojson.Geometry;
import eu.sdi4apps.geojson.Polygon;
import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.exceptions.GeneralException;
import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.types.DataResponse;
import eu.sdi4apps.openapi.types.LonLat;
import eu.sdi4apps.openapi.types.Status;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.openapi.utils.RequestUtils;
import eu.sdi4apps.openapi.utils.JsonSerializer;
import eu.sdi4apps.openapi.utils.Logger;
import eu.sdi4apps.openapi.utils.ResultSetUtil;
import eu.sdi4apps.routing.types.Node;
import eu.sdi4apps.routing.types.NodeResponse;
import eu.sdi4apps.routing.types.ReachableAreaResponse;
import eu.sdi4apps.routing.types.RouteResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author runarbe
 */
public class RouteCalculator {

    /**
     * Calculate the optimal route fro a number of points
     *
     * @param req
     * @return
     */
    public static DataResponse GetOptimalRoute(HttpServletRequest req) {
        DataResponse r = new DataResponse();

        try {

            r.parameters = req.getQueryString();

            String[] nodeIdStrings = req.getParameterValues("nodeIds[]");

            if (nodeIdStrings == null) {
                r.missingParam("nodeIds[]");
            }

            Map op = RequestUtils.GetParams(req, r, new String[]{"start", "finish"});

            if (!r.status.equals(Status.success)) {
                return r;
            }

            Integer[] nodeIds = new Integer[nodeIdStrings.length];
            int i = 0;
            for (String nodeIdString : nodeIdStrings) {
                nodeIds[i] = NumberUtils.toInt(nodeIdString);
                i++;
            }

            Database db = Database.Open(Settings.ROUTINGDB);

            PreparedStatement pst = db.Prepare(RoutingSQL.OptimizeNodeOrder(nodeIds, NumberUtils.toInt(op.get("start").toString()), NumberUtils.toInt(op.get("finish").toString())));

            ResultSet rs = pst.executeQuery();

            List ls = ResultSetUtil.ToHashMap(rs);

            r.setData(ls, true);
            r.count = ls.size();

            db.Close();

        } catch (Exception e) {
            r.setError("An error occurred during calculation of optimal route: " + e.toString());
        } finally {

        }

        return r;
    }

    /**
     * Calculate reachable area using pgRouting driving distance algorithm and
     * alpha shape function
     *
     * @param req
     * @return - A response object with the embedded area encoded as a GeoJSON
     * feature
     */
    public static ReachableAreaResponse GetReachableArea(HttpServletRequest req) {

        ReachableAreaResponse res = new ReachableAreaResponse();

        res.parameters = req.getQueryString();

        Integer fromNode = RequestUtils.GetInt(req, "fromNode", null);
        Integer distance = RequestUtils.GetInt(req, "distance", 1000);
        Double alpha = 0.001;

        if (fromNode == null) {
            res.missingParam("fromNode");
            return res;
        }

        ResultSet rs = null;

        try {
            Database db = Database.Open(Settings.ROUTINGDB);
            PreparedStatement pst = db.Prepare(RoutingSQL.ReachableArea(fromNode, distance, alpha));
            if (pst == null) {
                throw new Exception("Could not create prepared statement");
            }
            boolean isResult = pst.execute();
            int counter = 0;
            do {
                rs = pst.getResultSet();
                if (rs != null) {
                    while (rs.next()) {
                        String featureJson = rs.getString(1);
                        res.feature.geometry = (Geometry) JsonSerializer.Deserialize(featureJson, Polygon.class);
                    }
                }
                isResult = pst.getMoreResults();
                Logger.Log("has more results", isResult);
            } while (isResult);

        } catch (Exception e) {
            res.setError("An error occurred while calculating reachable area. Hint: " + e.toString());
        }
        return res;
    }

    /**
     * Calculate shortest route using pgRouting Dijkstra algorithm
     *
     * @param fromId
     * @param toId
     * @return
     */
    public static RouteResponse GetShortestRoute(int fromId, int toId) {

        RouteResponse res = new RouteResponse();

        try {

            NodeResponse fromNode = GetNodeById(fromId);
            NodeResponse toNode = GetNodeById(toId);

            if (fromNode.data != null && toNode.data != null) {

                BBox bbox = BBox.createFromPoints(fromNode.data.lon, fromNode.data.lat, toNode.data.lon, toNode.data.lat);
                bbox.extendBy(2).minSize(25000);

                //String sql = RoutingSQL.Dijkstra(fromId, toId, bbox);
                String sql = RoutingSQL.Dijkstra(fromId, toId, bbox);

                Database db = Database.Open(Settings.ROUTINGDB);

                ResultSet r = db.Query(sql);

                while (r.next()) {
                    String _geoJson = r.getString("geom");
                    String _streetName = r.getString("streetname");
                    double _distance = r.getDouble("cost");
                    Feature f = new Feature();
                    f.geometry = (Geometry) JsonSerializer.Deserialize(_geoJson, Geometry.class);
                    LinkedHashMap<String, Object> p = new LinkedHashMap<>();
                    p.put("distance", _distance);
                    p.put("streetname", _streetName);
                    f.properties = p;
                    if (_geoJson != null && _distance > 0) {
                        res.addSegment(f, _distance);
                    }
                }
                res.count = res.data.size();

                if (res.count == 0) {
                    res.status = Status.empty;
                } else {
                    res.status = Status.success;
                }
                db.Close();
            } else {
                res.setError(fromNode.messages.toString());
            }
        } catch (Exception ex) {
            res.setError(ex.getMessage());
        }
        return res;
    }

    public static NodeResponse GetNodeByLonLat(double lon, double lat, int radius) {
        NodeResponse nr = new NodeResponse();
        try {
            Database db = Database.Open(Settings.ROUTINGDB);
            
            System.out.println(Settings.ROUTINGDB.dbName);
            ResultSet r = db.Query(RoutingSQL.NearestNode(lon, lat, radius));
            int i = 0;
            while (r.next()) {
                double _lat = r.getDouble("lat");
                double _lon = r.getDouble("lon");
                double _degrees = r.getDouble("dist");
                nr.setData(new Node(r.getInt("id"), _lon, _lat, LonLat.degreesToMeters(_lon, _lat, _degrees)));
                i++;
                break;
            }
            if (i == 0) {
                throw new GeneralException(String.format("No node within %s meters of lat: %s, lon: %s", radius, lat, lon));
            } else {
                nr.status = Status.success;
            }
            db.Close();
        } catch (Exception ex) {
            nr.setError(ex.getMessage());
        }
        return nr;
    }

    public static NodeResponse GetNodeById(int nodeId) {
        NodeResponse nr = new NodeResponse();
        try {
            Database db = Database.Open(Settings.ROUTINGDB);
            ResultSet r = db.Query(RoutingSQL.NodeById(nodeId));
            int i = 0;
            while (r.next()) {
                nr.setData(new Node(r.getInt("id"), r.getDouble("lon"), r.getDouble("lat"), r.getDouble("dist")));
                i++;
                break;
            }
            if (i == 0) {
                nr.status = Status.empty;
                throw new GeneralException(String.format("No node with id: %s", nodeId));
            } else {
                nr.status = Status.success;
            }
            db.Close();
        } catch (Exception ex) {
            nr.setError(ex.getMessage());
        }
        return nr;
    }

}
