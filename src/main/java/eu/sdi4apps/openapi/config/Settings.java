package eu.sdi4apps.openapi.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration parameters
 *
 * @author runarbe
 */
public class Settings {
    
    /**
     * Connection parameters for routing database
     */
    public static final DbParams ROUTINGDB = new DbParams("routing", "postgres", "postgres");
    
    /**
     * Connection parameters for custom data db
     */
    public static final DbParams CUSTOMDB = new DbParams("customdb", "postgres", "postgres");
    
    /**
     * Connection parameters for indexer db
     */
    public static final DbParams INDEXDB = new DbParams("indexdb", "postgres", "postgres");

    /**
     * The directory path to where the Lucene index should be stored
     */
    public static final Path IndexDirectory = Paths.get("C:/users/runarbe/documents/Lucene");

}
