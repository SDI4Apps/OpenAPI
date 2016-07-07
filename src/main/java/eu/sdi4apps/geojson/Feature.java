/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.geojson;

import java.util.Map;

/**
 *
 * @author runarbe
 */
public class Feature {

    public String type = "Feature";
    public Geometry geometry;
    public Map<String, Object> properties;

    public Feature() {

    }

    public static Feature Create(Object geometry, Map<String, Object> properties) {
        Feature f = new Feature();
        f.geometry = (Geometry)geometry;
        if (properties != null) {
            f.properties = properties;
        }
        return f;
    }

}
