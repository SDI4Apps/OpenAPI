/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.customdata;

import com.sun.net.httpserver.Authenticator;
import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.DataResponse;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.types.Status;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.openapi.utils.RequestUtils;
import eu.sdi4apps.openapi.utils.ResultSetUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author runarbe
 */
public class CustomDb {

    public static Response createDb() {
        return new Response();
    }

    public static DataResponse deleteApplication(HttpServletRequest req) {

        DataResponse r = new DataResponse();

        Map<String, String> m = RequestUtils.GetParams(req, r, new String[]{"applicationId"});

        if (r.status == Status.success) {
            try {
                Database db = Database.Open(Settings.CUSTOMDB);
                String sql = String.format("DELETE FROM capplication WHERE id=%s", m.get("applicationId"));
                ResultSet rs = db.Delete(sql);
                List rm = ResultSetUtil.ToHashMap(rs);
                r.setData(rm, true);
                r.count = rm.size();
                db.Close();
            } catch (Exception e) {
                r.setError(e.toString());
            }
        }
        return r;
    }

    public static DataResponse listApplications(HttpServletRequest req) {
        DataResponse r = new DataResponse();
        try {
            Database db = Database.Open(Settings.CUSTOMDB);
            ResultSet res = db.Query("SELECT * FROM capplication");
            Object t = ResultSetUtil.ToHashMap(res);
            r.setData(t, true);
            db.Close();
        } catch (Exception e) {
            r.setError(e.toString());
        }
        return r;
    }

    public static DataResponse createApplication(HttpServletRequest req) {

        DataResponse r = new DataResponse();

        Map<String, String> m = RequestUtils.GetParams(req, r, new String[]{"applicationTitle", "userId"});

        if (r.status == Status.success) {
            try {
                Database db = Database.Open(Settings.CUSTOMDB);
                db.Execute(String.format("INSERT INTO capplication (title, user_id) "
                        + "VALUES ('%s', %s)", m.get("applicationTitle"), NumberUtils.toInt(m.get("userId"))));
                r.setData(null, true);
                db.Close();
            } catch (Exception e) {
                r.setError(e.toString());
            }
        }
        return r;
    }

    public static Response addObject(HttpServletRequest req) {
        DataResponse r = new DataResponse();

        Map<String, String> m = RequestUtils.GetParams(req, r, new String[]{"data", "capplication_id", "cuser_id", "ctype_id"});

        if (r.status == Status.success) {
            try {
                Database db = Database.Open(Settings.CUSTOMDB);
                PreparedStatement pst = db.Prepare("INSERT INTO cobject (user_id, ctype_id, capplication_id, data) VALUES (?,?,?,?)");
                pst.setInt(1, NumberUtils.toInt(m.get("user_id")));
                pst.setInt(2, NumberUtils.toInt(m.get("ctype_id")));
                pst.setInt(3, NumberUtils.toInt(m.get("capplication_id")));
                pst.setString(4, m.get("data"));
                pst.executeQuery();
                r.setData(null, true);
                db.Close();
            } catch (Exception e) {
                r.setError(e.toString());
            }
        }
        return r;
    }

    public static Response updateObject(Object obj) {
        return new Response();
    }

    public static Response getObject(Object obj) {
        return new Response();
    }

    public static Response queryObject() {
        return new Response();
    }

    public static Response deleteObject(Object obj) {
        return new Response();
    }

}
