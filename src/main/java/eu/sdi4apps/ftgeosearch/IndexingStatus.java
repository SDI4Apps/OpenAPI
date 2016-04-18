package eu.sdi4apps.ftgeosearch;

/**
 *
 * @author runarbe
 */
public enum IndexingStatus {

    Enqueued("Enqueued"),
    Indexing("Indexing"),
    Indexed("Indexed"),
    Error("Error");

    public final String Label;

    private IndexingStatus(String label) {

        this.Label = label;

    }

}
