/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

/**
 * Static logger class
 *
 * @author runarbe
 */
public class Logger {

    /**
     * Log a message
     *
     * @param msg
     */
    public static void Log(String msg) {
        System.out.println(msg);
    }

    /**
     * Log a message with a title
     *
     * @param title
     * @param msg
     */
    public static void Log(String title, String msg) {
        Log(String.format("%s: %s", title, msg));
    }
    

    /**
     * Log a message with a title and extra
     * 
     * @param title
     * @param msg
     * @param extra 
     */
    public static void Log(String title, String msg, String extra) {
        Log(String.format("%s: '%s'\n---\n%s", title, msg, extra));        
    }

}
