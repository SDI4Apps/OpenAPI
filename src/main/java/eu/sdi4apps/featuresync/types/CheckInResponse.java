package eu.sdi4apps.featuresync.types;

import eu.sdi4apps.openapi.types.Response;
import java.util.ArrayList;

public class CheckInResponse extends Response {

    public CheckInResponse() {
        super();
    }
    
    public ArrayList<FeatureSyncConflict> Conflicts;

}
