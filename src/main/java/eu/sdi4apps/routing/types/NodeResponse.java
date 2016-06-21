/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.routing.types;

import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.LonLat;
import eu.sdi4apps.openapi.types.Status;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.exceptions.GeneralException;
import eu.sdi4apps.openapi.utils.Database;
import eu.sdi4apps.routing.RoutingSQL;
import java.sql.ResultSet;

/**
 *
 * @author runarbe
 */
public class NodeResponse extends Response {

    public Node data;

    public NodeResponse() {
        super();
        this.action = RoutingOperations.NEARESTNODE;
    }

    public void setData(Node data) {
        this.data = data;
        this.count = 1;
        this.setSuccess();
    }
    
}
