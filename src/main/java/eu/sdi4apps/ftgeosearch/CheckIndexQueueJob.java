/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.openapi.utils.Logger;
import eu.sdi4apps.ftgeosearch.drivers.ShapefileDriver;
import java.io.File;
import org.apache.lucene.index.IndexWriter;
import org.gdal.ogr.DataSource;
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

    private Boolean IsWorking = false;

    @Override
    public void run() {

        if (IsWorking == true) {
            Logger.Log("Previous indexing job still in progress");
            return;
        }

        IsWorking = true;

        try {

            DateTime currentTime = DateTime.now();

            int runningTime = Seconds.secondsBetween(StartTime, currentTime).getSeconds();
            CheckCount++;

            Logger.Log("Checked index queue: #" + CheckCount + ", background indexing task has been running for " + runningTime + " seconds since last restart...");

            for (QueueItem qi : IndexerQueue.top(1)) {

                Logger.Log("Processing entry: " + qi.layer + " added " + qi.enqueued);
                
                IndexWriter w = Indexer.getWriter();

                switch (qi.datasettype) {
                    case Shapefile:
                        ShapefileDriver drv = (ShapefileDriver) qi.ogrdriver;
                        if (drv == null) {
                            throw new Exception("Could not deserialize queue item into ogr driver: " + qi.ogrdriver);
                        }
                        File f = new File(drv.filenameOfShpFile);

                        if (!f.exists()) {
                            throw new Exception("Enqueued source file '" + drv.filenameOfShpFile + "' does not exist");
                        }

                        if (!f.canRead()) {
                            throw new Exception("No file read access to '" + drv.filenameOfShpFile + "'");
                        }

                        DataSource ds = ogr.Open(drv.filenameOfShpFile, 0);
                        if (ds == null) {
                            throw new Exception("Could not open data source'" + drv.filenameOfShpFile + "' using gdal/ogr");
                        }

                        Layer lyr = ds.GetLayer(0);
                        if (lyr == null) {
                            throw new Exception("Could not get layer from source file '" + drv.filenameOfShpFile + "' using gdal/ogr");
                        }

                        Indexer.indexLayer(lyr, qi, w);

                        break;
                    default:
                        Logger.Log("Something went wrong");
                        drv = null;
                        break;
                }

                qi.updateIndexingStatus(IndexingStatus.Indexed);
            }

        } catch (Exception e) {
            Logger.Log("An indexing error occurred: " + e.toString());
        } finally {
            Indexer.closeWriter();
            IsWorking = false;
        }

    }

}
