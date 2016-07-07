/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.routing.types;

/**
 *
 * @author runarbe
 */
public class RouteSegment {

    public String geoJson;
    public String streetName;
    public double length;
    
    public RouteSegment(String geoJson, String streetName, double distance) {
        this.geoJson = geoJson;
        this.streetName = streetName;
        this.length = distance;
    }

}
