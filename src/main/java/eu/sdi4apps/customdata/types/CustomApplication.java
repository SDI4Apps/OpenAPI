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

    public Integer id;
    public String title;
    public Integer user_id;
    
}
