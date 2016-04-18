/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.ftgeosearch;

import eu.sdi4apps.ftgeosearch.drivers.OGRDriver;
import com.cedarsoftware.util.io.JsonWriter;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static java.sql.Types.NULL;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Indexer queue item
 *
 * @author runarbe
 */
public class QueueItem {

    /**
     * Unique PK of the entry in the database
     */
    public int id = -1;

    /**
     * Name of dataset to be indexed
     */
    public String layer;

    /**
     * Time when the dataset was enqueued
     */
    public DateTime enqueued = null;

    /**
     * Time when the dataset was indexed
     */
    public DateTime indexed = null;

    /**
     * A list of of one or more fields to be used in the display title
     */
    public List<String> titlefields;

    /**
     * A format string to be used to generate a display title the fields listed
     * in title fields
     */
    public String titleformat = "%s";

    /**
     * A list of one or more fields to be used as description
     */
    public List<String> descriptionfields;

    /**
     * A format string to be used to generate a description using the fields
     * listed in description fields
     */
    public String descriptionformat = "%s";

    /**
     * Additional fields to be indexed
     */
    public List<String> additionalfields = null;

    /**
     * Fields to be included as a JSON object
     */
    public List<String> jsonDataFields = null;
    /**
     * A list of fields to be excluded from indexing
     */
    public List<String> excludefields;

    /**
     * An interval at which the indexed data should be re-indexed specified in
     * minutes.
     *
     * 0 = never re-index
     */
    public int refresh = 0;

    /**
     * The current status of the layer in the queue
     */
    public IndexingStatus status = IndexingStatus.Enqueued;

    /**
     * Whether the dataset is an ESRI Shapefile, a KML document, a GeoJSON file
     * etc.
     */
    public DatasetType datasettype;

    /**
     * An OGR driver object that is used to connect to the data source and
     * create a layer
     */
    public OGRDriver ogrdriver;

    /**
     * Constructor
     */
    QueueItem() {
    }

    /**
     * create a new queue item with a multiple title and description fields and
     * formats
     *
     * @param datasetname
     * @param datasettype
     * @param ogrdriver
     * @param titlefields
     * @param titleformat
     * @param descriptionfields
     * @param descriptionformat
     * @param additionalfields
     * @param jsondatafields
     * @return
     */
    public static QueueItem create(
            String datasetname,
            DatasetType datasettype,
            OGRDriver ogrdriver,
            List<String> titlefields,
            String titleformat,
            List<String> descriptionfields,
            String descriptionformat,
            List<String> additionalfields,
            List<String> jsondatafields
    ) {
        QueueItem item = new QueueItem();
        item.layer = datasetname;
        item.datasettype = datasettype;
        item.ogrdriver = ogrdriver;
        item.titlefields = titlefields;
        item.titleformat = titleformat;
        item.descriptionfields = descriptionfields;
        item.descriptionformat = descriptionformat;
        item.additionalfields = additionalfields;
        item.jsonDataFields = jsondatafields;
        return item;
    }

    /**
     * Index a layer with simple 
     * 
     * @param datasetname
     * @param datasettype
     * @param ogrdriver
     * @param titlefields
     * @param titleformat
     * @param descriptionfields
     * @param descriptionformat
     * @return 
     */
    public static QueueItem create(String datasetname,
            DatasetType datasettype,
            OGRDriver ogrdriver,
            List<String> titlefields,
            String titleformat,
            List<String> descriptionfields,
            String descriptionformat) {
        return create(
                datasetname,
                datasettype,
                ogrdriver,
                titlefields,
                titleformat,
                descriptionfields,
                descriptionformat,
                null,
                null);
    }

    /**
     * Index a layer with single field mapping to title and description
     * 
     * @param datasetname
     * @param datasettype
     * @param ogrdriver
     * @param titleField
     * @param descriptionField
     * @return 
     */
    public static QueueItem create(
            String datasetname,
            DatasetType datasettype,
            OGRDriver ogrdriver,
            String titleField,
            String descriptionField) {

        return create(datasetname,
                datasettype,
                ogrdriver,
                asList(titleField),
                "%s",
                asList(descriptionField),
                "%s",
                null,
                null);
    }

    public void insert() throws SQLException {
        PreparedStatement s = IndexerQueue.prepare("INSERT INTO queue (layer, datasettype, ogrdriver, titlefields, titleformat, descriptionfields, descriptionformat, additionalfields, jsondatafields)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        s.setString(1, this.layer);
        s.setString(2, this.datasettype.toString());
        s.setString(3, JsonWriter.objectToJson(this.ogrdriver));
        s.setArray(4, IndexerQueue.createArrayOf(this.titlefields));
        s.setString(5, this.titleformat);
        s.setArray(6, IndexerQueue.createArrayOf(this.descriptionfields));
        s.setString(7, this.descriptionformat);

        if (this.additionalfields != null) {
            s.setArray(8, IndexerQueue.createArrayOf(this.additionalfields));
        } else {
            s.setNull(8, NULL);
        }

        if (this.jsonDataFields != null) {
            s.setArray(9, IndexerQueue.createArrayOf(this.jsonDataFields));
        } else {
            s.setNull(9, NULL);
        }
        s.execute();

        Logger.Log("Added layer: " + this.layer + " to indxing queue");

    }

    public void updateIndexingStatus(IndexingStatus newIndexingStatus) throws SQLException {
        if (this.id != -1) {
            PreparedStatement ps = IndexerQueue.prepare("UPDATE queue SET indexingstatus = ? WHERE id=?");
            ps.setString(1, newIndexingStatus.name());
            ps.setInt(2, this.id);
            ps.execute();
        }
    }

    public void delete() {
        String sql = String.format(
                "DELETE FROM queue WHERE id = %s",
                this.id);
        IndexerQueue.executeUpdate(sql);
    }

}
