/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.customdata.types;

import eu.sdi4apps.openapi.types.Response;

/**
 *
 * @author runarbe
 */
public class CustomType {

    public static Response CreateType(String typeName) {
        return new Response();
    }

    public static Response ListTypes() {
        return new Response();
    }

}
