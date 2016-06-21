/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.utils;

import com.google.gson.Gson;
import eu.sdi4apps.geojson.Geometry;

/**
 *
 * @author runarbe
 */
public class JsonSerializer {

    public static Gson gson = new Gson();

    public static String Serialize(Object obj) {
        return JsonSerializer.gson.toJson(obj);
    }

    public static Object Deserialize(String json, Class targetClass) {
        return JsonSerializer.gson.fromJson(json, targetClass);
    }

}
