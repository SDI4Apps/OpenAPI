/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import com.cedarsoftware.util.io.JsonWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Search result object to be returned from searches for GeoDocs
 * 
 * @author runarbe
 */
public class SearchResult {

    /**
     * Constant Error
     */
    public static final String Error = "Error";
    
    /**
     * Constant Success
     */
    public static final String Success = "Success";

    /**
     * JSON options
     */
    public static Map JsonOptions;

    /**
     * Search result status, one of the constants
     */
    public String Status;

    /**
     * Time consumption of search operation
     */
    public double Time = -1;

    /**
     * Number of search results
     */
    public int Count = 0;

    /**
     * List of hits
     */
    public List<GeoDoc> Hits;

    /**
     * Parameterless constructor
     */
    public SearchResult() {
        
        SearchResult.JsonOptions = new HashMap();
        SearchResult.JsonOptions.put(JsonWriter.TYPE, false);

        this.Hits = new ArrayList<>();
    }

    /**
     * Constructor
     * 
     * @param success True if the search result shall be initialized as success, false if error
     */
    public SearchResult(boolean success) {
        this();
        if (success == false) {
            this.Status = SearchResult.Error;
        } else {
            this.Status = SearchResult.Success;
        }
    }

    /**
     * Add hit to the list
     * 
     * @param hitDoc
     */
    public void addHit(GeoDoc hitDoc) {
        this.Hits.add(hitDoc);
        this.Count++;
    }

    /**
     * Return a JSON string representation of the SearchResult object
     * 
     * @return JSON string
     */
    public String asJson() {
        return JsonWriter.objectToJson(this, SearchResult.JsonOptions);
    }

    /**
     * Set the time consumption of the operation
     * 
     * @param startTime The start time of the operation in nanoseconds
     */
    public void calculateTime(long startTime) {
        this.Time = Math.round((System.nanoTime() - startTime) * 0.000001);
    }

}
