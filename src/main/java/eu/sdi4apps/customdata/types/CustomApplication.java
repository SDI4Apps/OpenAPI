/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.customdata.types;

import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.DataResponse;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.openapi.utils.ResultSetUtil;
import java.sql.ResultSet;

/**
 *
 * @author runarbe
 */
public class CustomApplication {

    public static DataResponse CreateApplication(String applicationTitle) {
        DataResponse r = new DataResponse();
        try {
            Database db = Database.Open(Settings.CUSTOMDB);

            db.Execute(String.format("INSERT INTO capplication (title, user_id) VALUES ('%s', %s)",
                    applicationTitle,
                    1));

            r.setData(null, true);
            db.Close();
        } catch (Exception e) {
            r.setError(e.toString());
        }
        return r;
    }

    public static DataResponse DeleteApplication(int applicationId) {
        DataResponse r = new DataResponse();
        try {
            Database db = Database.Open(Settings.CUSTOMDB);
            String sql = String.format("DELETE FROM capplication WHERE id=%s",
                    applicationId);
            
            r.addMessage(sql);
            r.addMessage("Runar");
             
            ResultSet rs = db.Delete(sql);

            r.setData(ResultSetUtil.ToHashMap(rs), true);
            db.Close();
        } catch (Exception e) {
            r.setError(e.toString());
        }
        return r;
    }

    public static DataResponse ListApplications() {
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

}
