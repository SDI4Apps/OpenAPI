/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.ftgeosearch.drivers.OGRDriver;
import eu.sdi4apps.ftgeosearch.drivers.ShapefileDriver;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.lucene.index.IndexWriter;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 *
 * @author runarbe
 */
public class CheckIndexQueueJob implements Runnable {

    private int CheckCount = 0;

    private DateTime StartTime = DateTime.now();

    @Override
    public void run() {

        try {

            DateTime currentTime = DateTime.now();

            int runningTime = Seconds.secondsBetween(StartTime, currentTime).getSeconds();
            CheckCount++;

            Logger.Log("Checked index queue: #" + CheckCount + ", background indexing task has been running for " + runningTime + " seconds since last restart...");

            for (QueueItem qi : IndexerQueue.top(1)) {

                qi.updateIndexingStatus(IndexingStatus.Indexing);

                IndexWriter w = Indexer.getWriter();

                switch (qi.datasettype) {
                    case Shapefile:
                        ShapefileDriver drv = (ShapefileDriver) qi.ogrdriver;
                        DataSource ds = ogr.Open(((ShapefileDriver) drv).filenameOfShpFile);
                        if (ds != null) {
                            Layer lyr = ds.GetLayer(0);
                            if (lyr == null) {
                                Logger.Log("No layer derived from source");
                                return;
                            }
                            Indexer.indexLayer(lyr, qi, w);
                        }
                        break;
                    default:
                        drv = null;
                        break;
                }

                Indexer.closeWriter();

                qi.updateIndexingStatus(IndexingStatus.Indexed);
                Logger.Log("Processing entry: " + qi.layer + " added " + qi.enqueued);
            }

        } catch (Exception e) {
            Logger.Log(e.toString());
        }

    }

}
