/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import static eu.sdi4apps.ftgeosearch.Indexer.maxSpatialIndexLevels;
import static eu.sdi4apps.ftgeosearch.Indexer.spatialCtx;
import static eu.sdi4apps.ftgeosearch.Indexer.spatialPrefixTree;
import static eu.sdi4apps.ftgeosearch.Indexer.spatialStrategy;
import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.utils.Logger;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author runarbe
 */
public class Searcher {

    public static Analyzer analyzer;

    public static Directory directory;

    public static DirectoryReader ireader = null;

    public static IndexSearcher isearcher = null;

    public static SpatialContext spatialCtx = null;

    public static SpatialStrategy spatialStrategy = null;

    public static SpatialPrefixTree spatialPrefixTree = null;

    public static int maxSpatialIndexLevels = 11;

    static {
        createSearcher();
    }

    public static IndexSearcher createSearcher() {
        try {
            spatialCtx = SpatialContext.GEO;
            spatialPrefixTree = new GeohashPrefixTree(spatialCtx, maxSpatialIndexLevels);
            spatialStrategy = new RecursivePrefixTreeStrategy(spatialPrefixTree, "GeoField");

            analyzer = new StandardAnalyzer();
            directory = FSDirectory.open(Paths.get(Settings.INDEXDIR));
            ireader = DirectoryReader.open(directory);
            isearcher = new IndexSearcher(ireader);
            return isearcher;
        } catch (Exception e) {
            return null;
        }
    }

    public static void destroySearcher() {
        try {
            if (isearcher != null) {
                isearcher = null;
            }

            if (ireader != null) {
                ireader.close();
                ireader = null;
            }

            if (directory != null) {
                directory.close();
                directory = null;
            }
        } catch (Exception e) {
            Logger.Log("Failed to destroy searcher");
        }
    }

    public static List<Object> Search(String q,
            Integer maxresults,
            String filter,
            String extent) throws IOException, ParseException {

        List<Object> searchResults = new ArrayList<>();

        try {

            /**
             * Create searcher if it does not exist
             */
            if (isearcher == null) {
                createSearcher();
            }

            /**
             * Create a boolean query
             */
            BooleanQuery combinedQuery = new BooleanQuery();

            /**
             * Set default number of results to 100
             */
            if (maxresults == null) {
                maxresults = Settings.NUMRESULTS;
            }

            /**
             * Split filter layers into an array
             */
            String[] filterLayers;
            if (filter != null) {
                filterLayers = StringUtils.split(filter, ",");
            } else {
                filterLayers = null;
            }

            /**
             * Create query clause for user specified term(s)
             */
            QueryParser parser = new MultiFieldQueryParser(new String[]{"IndexTitle", "IndexDescription", "IndexAdditional"}, analyzer);
            Query termQuery = parser.parse(q);
            combinedQuery.add(termQuery, Occur.MUST);
            
            /**
             * Convert string extent to BBox object
             */
            if (extent != null) {
                BBox bbox = BBox.createFromString(extent);
                if (bbox != null) {
                    SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
                            spatialCtx.makeRectangle(bbox.minX, bbox.maxX, bbox.minY, bbox.maxY));
                    Query spatialQuery = spatialStrategy.makeQuery(args);
                    spatialQuery.setBoost(Settings.SPATIALBOOST);
                    combinedQuery.add(spatialQuery, Occur.SHOULD);
                }
            }

            ScoreDoc[] hits = isearcher.search(combinedQuery, maxresults).scoreDocs;

            // Iterate through the results:
            for (int i = 0; i < hits.length; i++) {
                GeoDoc g = new GeoDoc();
                Document hitDoc = isearcher.doc(hits[i].doc);
                g.Score = hits[i].score;
                g.Id = hitDoc.get("Id");
                g.Layer = hitDoc.get("Layer");
                g.ObjType = hitDoc.get("ObjType");
                g.FullGeom = hitDoc.get("FullGeom");
                g.DisplayTitle = hitDoc.get("DisplayTitle");
                g.DisplayDescription = hitDoc.get("DisplayDescription");
                g.PointGeom = hitDoc.get("PointGeom");
                g.JsonData = Serializer.Deserialize(hitDoc.get("JsonData"));
                searchResults.add(g);
            }

        } catch (Exception e) {
            Logger.Log("An exception occurred during search: " + e.toString());
        } finally {
            destroySearcher();
            return searchResults;
        }

    }

}
