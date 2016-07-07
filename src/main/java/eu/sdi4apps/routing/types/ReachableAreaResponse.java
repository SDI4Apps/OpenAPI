/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.routing.types;

import eu.sdi4apps.geojson.Feature;
import eu.sdi4apps.geojson.Geometry;
import eu.sdi4apps.openapi.types.Response;
import java.util.Map;

/**
 *
 * @author runarbe
 */
public class ReachableAreaResponse extends Response {

    public Feature feature;

    public ReachableAreaResponse() {
        super();
        this.action = RoutingOperations.REACHABLEAREA;
        this.feature = new Feature();
    }

    public void setGeometry(Geometry geometry) {
        this.feature.geometry = geometry;
    }

    public void setProperties(Map<String, Object> properties) {
        this.feature.properties = properties;
    }

}
