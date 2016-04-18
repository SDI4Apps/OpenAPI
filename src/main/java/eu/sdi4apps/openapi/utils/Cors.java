/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.utils;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author runarbe
 */
public class Cors {

    public static HttpServletResponse EnableCors(HttpServletResponse hResp) {
        hResp.setContentType("application/json;charset=UTF-8");
        hResp.setHeader("Cache-control", "no-cache, no-store");
        hResp.setHeader("Pragma", "no-cache");
        hResp.setHeader("Expires", "-1");
        hResp.setHeader("Access-Control-Allow-Origin", "*");
        hResp.setHeader("Access-Control-Allow-Methods", "POST,GET");
        hResp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        hResp.setHeader("Access-Control-Max-Age", "86400");
        return hResp;
    }   

}
