package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.ftgeosearch.drivers.ShapefileDriver;
import eu.sdi4apps.openapi.config.Settings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;

/**
 * Indexer
 *
 * @author runarbe
 */
public class Indexer {

    public static StandardAnalyzer analyzer = null;

    public static IndexWriter indexWriter = null;

    public static Directory directory = null;

    public static IndexWriterConfig indexWriterConfig = null;

    /**
     * Get the IndexWriter or null
     *
     * @return
     */
    public static IndexWriter getWriter() {
        try {
            if (indexWriter == null || indexWriter.isOpen() == false) {
                analyzer = new StandardAnalyzer();
                directory = FSDirectory.open(Settings.IndexDirectory);
                indexWriterConfig = new IndexWriterConfig(analyzer);
                indexWriter = new IndexWriter(directory, indexWriterConfig);
            }
        } catch (Exception e) {
            System.out.println("Error opening IndexWriter: " + e.toString());
        }
        return indexWriter;
    }

    public static void dropIndex() {
        try {
            IndexWriter w = Indexer.getWriter();
            w.deleteAll();
            w.commit();
            w.close();
        } catch (Exception e) {
            Logger.Log(e.toString());
        }
    }

    public static void indexLayer(Layer lyr, QueueItem qi, IndexWriter w) {

        try {
            Feature f = null;

            int totalFeatures = lyr.GetFeatureCount(1);

            int batch = 500;

            int counter = 1;

            LinkedHashMap<String, Boolean> titleFieldMap = GdalExt.getFieldMap(lyr, qi.titlefields);
            LinkedHashMap<String, Boolean> descriptionFieldMap = GdalExt.getFieldMap(lyr, qi.descriptionfields);
            LinkedHashMap<String, Boolean> indexAdditionalFieldMap = GdalExt.getFieldMap(lyr, qi.additionalfields);
            LinkedHashMap<String, Boolean> jsonDataFieldMap = GdalExt.getFieldMap(lyr, qi.jsonDataFields);

            Logger.Log(jsonDataFieldMap.toString());

            while (null != (f = lyr.GetNextFeature())) {

                Geometry g = f.GetGeometryRef();

                String[] titleData = GdalExt.getFormattedAndIndexValuesAsString(f, titleFieldMap, qi.titleformat);
                String[] descData = GdalExt.getFormattedAndIndexValuesAsString(f, descriptionFieldMap, qi.descriptionformat);
                String additionalData = GdalExt.getFieldValuesAsString(f, indexAdditionalFieldMap);
                Object jsonData = GdalExt.getAsMapObject(f, jsonDataFieldMap);

                String compositeId = qi.layer + "-" + f.GetFID();

                GeoDoc gd = GeoDoc.create(
                        compositeId,
                        qi.layer,
                        g.ExportToWkt(),
                        g.PointOnSurface().ExportToWkt(),
                        titleData[0],
                        descData[0],
                        titleData[1],
                        descData[1],
                        additionalData,
                        jsonData);

                if (gd != null) {
                    w.updateDocument(new Term("Id", compositeId), gd.asLuceneDoc());
                }

                if (counter % batch == 0 || counter == totalFeatures) {
                    Logger.Log("Processed: " + counter + " items...");
                }
                counter++;
            }
            Logger.Log("Done");
        } catch (Exception ex) {
            Logger.Log("An error occurred while indexing Layer", ex.toString());
        }
    }

    /**
     * Close the index writer
     */
    public static void closeWriter() {

        try {

            if (indexWriter != null) {
                indexWriter.close();
                indexWriter = null;
            }

            if (indexWriterConfig != null) {
                indexWriterConfig = null;
            }

            if (directory != null) {
                directory.close();
                directory = null;
            }

            if (analyzer != null) {
                analyzer.close();
                analyzer = null;
            }
        } catch (Exception e) {
            System.out.println("Error during closing of writer: " + e.toString());
        }
    }

    /**
     * create a test index
     *
     * @param dropExistingQueueDatabase
     * @throws IOException
     */
    public static void create(boolean dropExistingQueueDatabase) throws IOException, SQLException {

        IndexerQueue.init();

        if (dropExistingQueueDatabase) {
            IndexerQueue.drop();
            IndexerQueue.create();
        }

        ShapefileDriver driver = new ShapefileDriver("C:/Users/runarbe/Documents/My Map Data/Natural Earth/10m_cultural/ne_10m_populated_places_simple.shp");
        List<String> titleFields = asList("name");
        String titleFormat = "Title: %s";
        List<String> descriptionFields = asList("featurecla");
        String descriptionFormat = "Description: %s...";
        List<String> additionalFields = asList("featurecla", "sov0name", "adm0name");
        QueueItem entry = QueueItem.create("Natural Earth Simple", DatasetType.Shapefile, driver, titleFields, titleFormat, descriptionFields, descriptionFormat, null, null);
        IndexerQueue.enqueue(entry);

        IndexWriter w = Indexer.getWriter();
        w.deleteAll();

        w.commit();
        Indexer.closeWriter();
    }

}
