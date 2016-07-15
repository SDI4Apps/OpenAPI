/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author runarbe
 */
public class FeatureCollection {

    public String type = "FeatureCollection";
    
    public List<Feature> features;

    public FeatureCollection() {
        features = new ArrayList();
    }

    public void addFeature(Feature f) {
        features.add(f);
    }

}
