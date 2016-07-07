/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.routing.types;

import eu.sdi4apps.geojson.Feature;
import eu.sdi4apps.openapi.types.Response;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author runarbe
 */
public class RouteResponse extends Response {

    public List<Feature> data = new ArrayList<>();

    public double distance;

    public RouteResponse() {
        super();
        this.action = RoutingOperations.SHORTESTPATH;
    }

    public void addSegment(Feature feature, double length) {
        this.data.add(feature);
        this.distance += length;
    }

    public void setData(List<Feature> segments, double length, Boolean resetDistance) {
        this.data = segments;
        if (resetDistance) {
            this.distance = 0;
        }
        for (Feature rs : segments) {
            this.distance += length;
        }
    }
}
