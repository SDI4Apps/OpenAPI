/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.pgrouting.types;

import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.types.Status;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.pgrouting.utils.RoutingSQL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author runarbe
 */
public class RouteResponse extends Response {

    public List<RouteSegment> data = new ArrayList<>();

    public double distance;

    public RouteResponse() {
        super();
        this.action = RoutingOperations.GetShortestPath;
    }

    public void addSegment(RouteSegment segment) {
        this.data.add(segment);
        this.distance += segment.length;
    }

    public void setData(List<RouteSegment> segments) {
        this.data = segments;
        this.distance = 0;
        for (RouteSegment rs : segments) {
            this.distance += rs.length;
        }
    }

    public static RouteResponse GetDijkstra(int fromId, int toId) {

        RouteResponse res = new RouteResponse();

        try {

            NodeResponse fromNode = NodeResponse.GetById(fromId);
            NodeResponse toNode = NodeResponse.GetById(toId);

            if (fromNode.data != null && toNode.data != null) {

                BBox bbox = BBox.createFromPoints(fromNode.data.lon, fromNode.data.lat, toNode.data.lon, toNode.data.lat);
                bbox.extendBy(2).minSize(10000);

                String sql = RoutingSQL.Dijkstra(fromId, toId, bbox);
                
                Database db = Database.Open(Settings.ROUTINGDB);
                
                ResultSet r = db.Query(sql);

                while (r.next()) {
                    String _geoJson = r.getString("geom");
                    String _streetName = r.getString("streetname");
                    double _distance = r.getDouble("cost");
                    if (_geoJson != null && _distance > 0) {
                        res.addSegment(new RouteSegment(_geoJson, _streetName, _distance));
                    }
                    res.count = res.data.size();
                }
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

}
