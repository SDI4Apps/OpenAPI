/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.pgrouting.types;

import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.LonLat;
import eu.sdi4apps.openapi.types.Status;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.exceptions.GeneralException;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.pgrouting.utils.RoutingSQL;
import java.sql.ResultSet;

/**
 *
 * @author runarbe
 */
public class NodeResponse extends Response {

    public Node data;

    public NodeResponse() {
        super();
        this.action = RoutingOperations.GetNearestNode;
    }

    public void setData(Node data) {
        this.data = data;
        this.count = 1;
        this.setSuccess();
    }

    public static NodeResponse GetByLonLat(double lon, double lat, int radius) {
        NodeResponse nr = new NodeResponse();

        try {
            Database db = Database.Open(Settings.ROUTINGDB);
            
            ResultSet r = db.Query(RoutingSQL.NearestNode(lon, lat, radius));
            int i = 0;
            while (r.next()) {
                double _lat = r.getDouble("lat");
                double _lon = r.getDouble("lon");
                double _degrees = r.getDouble("dist");

                nr.setData(new Node(r.getInt("id"),
                        _lon,
                        _lat,
                        LonLat.degreesToMeters(_lon, _lat, _degrees)));
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

    public static NodeResponse GetById(int nodeId) {
        NodeResponse nr = new NodeResponse();
        System.out.println(RoutingSQL.NodeById(nodeId));
        try {
            Database db = Database.Open(Settings.ROUTINGDB);
            ResultSet r = db.Query(RoutingSQL.NodeById(nodeId));
            int i = 0;
            while (r.next()) {
                nr.setData(
                        new Node(
                                r.getInt("id"),
                                r.getDouble("lon"),
                                r.getDouble("lat"),
                                r.getDouble("dist")
                        )
                );
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
