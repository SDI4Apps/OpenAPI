/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.openapi.config.Settings;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author runarbe
 */
public class Searcher {

    public static SearchResult Search(String q) throws IOException, ParseException {

        SearchResult sr = new SearchResult(true);

        Analyzer analyzer = new StandardAnalyzer();

        Directory directory = FSDirectory.open(Settings.IndexDirectory);

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        QueryParser parser = new MultiFieldQueryParser(new String[]{"IndexTitle", "IndexDescription", "IndexAdditional"}, analyzer);
        Query query = parser.parse(q);
        ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;

        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            GeoDoc g = new GeoDoc();
            Document hitDoc = isearcher.doc(hits[i].doc);
            g.Score = hits[i].score;
            g.Id = hitDoc.get("Id");
            g.Layer = hitDoc.get("Layer");
            g.FullGeom = hitDoc.get("FullGeom");
            g.DisplayTitle = hitDoc.get("DisplayTitle");
            g.DisplayDescription = hitDoc.get("DisplayDescription");
            g.PointGeom = hitDoc.get("PointGeom");
            g.JsonData = Serializer.Deserialize(hitDoc.get("JsonData"));
            sr.addHit(g);
        }
        ireader.close();
        return sr;

    }

}
