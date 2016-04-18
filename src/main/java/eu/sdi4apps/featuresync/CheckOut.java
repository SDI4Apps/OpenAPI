/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.featuresync;

import eu.sdi4apps.featuresync.types.CheckOutResponse;
import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.openapi.utils.ResultSetUtil;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author runarbe
 */
public class CheckOut {

    public static CheckOutResponse Do(String layerId, String idField, BBox bbox) {

        CheckOutResponse r = new CheckOutResponse();
        String sql = "";
        try {
            sql = String.format("SELECT ST_AsGeoJSON(the_geom, 8, 1) as the_geom, gid FROM ways WHERE the_geom && %s", bbox.wktEnvelope());
            Database db = Database.Open(Settings.ROUTINGDB);
            ResultSet rows = db.Query(sql);
            r.GeoJSON = ResultSetUtil.ToJson(rows);
            db.Close();
        } catch (SQLException ex) {
            r.messages.add("Error selecting data: " + sql);
            r.setError(ex.toString());
        } catch (ClassNotFoundException ex) {
            r.messages.add("Could not find class: ");
            r.setError(ex.toString());
        } catch (Exception ex) {
            r.setError("Something else went wrong");
            r.messages.add(ex.toString());
        }
        return r;
    }

}
