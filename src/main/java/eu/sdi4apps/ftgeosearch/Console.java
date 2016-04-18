/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.ftgeosearch.drivers.ShapefileDriver;
import java.io.IOException;
import static java.util.Arrays.asList;
import java.util.List;
import org.apache.lucene.index.IndexWriter;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

/**
 *
 * @author runarbe
 */
public class Console {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        try {

            ogr.RegisterAll();

            IndexerQueue.drop();
            IndexerQueue.create();
//            Indexer.dropIndex();

            ShapefileDriver driver = new ShapefileDriver("C:/Users/runarbe/Documents/My Map Data/Natural Earth/10m_cultural/ne_10m_populated_places_simple.shp");
            List<String> titleFields = asList("name");
            String titleFormat = "Karel Title: %s";
            List<String> descriptionFields = asList("featurecla", "adm0name", "sov0name");
            String descriptionFormat = "Premek: %s, Tomas: %s, (Runar: %s)";
            List<String> additionalFields = asList("featurecla", "sov0name", "adm0name");
            List<String> jsonDataFields = asList("featurecla", "adm0name");
            
            QueueItem entry = QueueItem.create("nepopulatedplaces", DatasetType.Shapefile, driver , titleFields, titleFormat, descriptionFields, descriptionFormat, null, null);
            entry.jsonDataFields = jsonDataFields;
            
            IndexerQueue.enqueue(entry);
            
            ShapefileDriver drv2 = new ShapefileDriver("C:/Users/runarbe/Documents/My Map Data/Natural Earth/10m_cultural/ne_10m_admin_1_states_provinces_lakes_shp.shp");
            QueueItem e2 = QueueItem.create("states_provinces", DatasetType.Shapefile, drv2, "name", "admin");

            IndexerQueue.enqueue(e2);

            ShapefileDriver drv;

            for (QueueItem qi : IndexerQueue.top(2)) {

                qi.updateIndexingStatus(IndexingStatus.Indexing);

                switch (qi.datasettype) {
                    case Shapefile:
                        drv = (ShapefileDriver) qi.ogrdriver;
                        break;
                    default:
                        drv = null;
                        break;
                }

                DataSource ds = ogr.Open(drv.filenameOfShpFile);

                Layer lyr = ds.GetLayer(0);

                IndexWriter w = Indexer.getWriter();
                
                Indexer.indexLayer(lyr, qi, w);

                Indexer.closeWriter();

                qi.updateIndexingStatus(IndexingStatus.Indexed);
            }
        } catch (Exception e) {
            Logger.Log(e.toString());
        }

    }

}
