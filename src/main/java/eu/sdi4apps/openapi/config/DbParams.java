/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.config;

/**
 *
 * @author runarbe
 */
public class DbParams {

    public String dbName = "routing";
    public String dbUser = "postgres";
    public String dbPassword = "postgres";
    public int dbPort = 5432;
    public String dbHost = "localhost";

    public DbParams(String dbName, String dbUser, String dbPassword, int dbPort, String dbHost) {

        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbPort = dbPort;
        this.dbHost = dbHost;
    }

    public DbParams(String dbName, String dbUser, String dbPassword) {
        this(dbName, dbUser, dbPassword, 5432, "localhost");
    }
    
    /**
     * Return the JDBC URL of the respective setting
     * @return 
     */
    public String getJdbcUrl() {
            return "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword;
    }
}
