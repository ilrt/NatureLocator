import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import com.google.appengine.api.datastore.FetchOptions
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.DatastoreService
import scripts.*;

if (!Auth.authenticate(response, request, log)) return;

if (!params.confirm)
{
    Query allreports = new Query("Report").setKeysOnly();
    Query allphotos = new Query("Photo").setKeysOnly();
    request.totalReports = datastore.prepare(allreports).countEntities(withDefaults())
    request.totalPhotos = datastore.prepare(allphotos).countEntities(withDefaults())
}
else
{
    log.info "Deleting all reports"

    request.totalReports = deleteAll("Report")
    request.totalPhotos = deleteAll("Photo")
    
    request.success = true

}

forward '/WEB-INF/pages/deleteAllReports.gtpl'


int deleteAll(String kind)
{
    start = System.currentTimeMillis();

    deleted_count = 0;
    is_finished = false;

    DatastoreService dss = DatastoreServiceFactory.getDatastoreService();

    while (System.currentTimeMillis() - start < 16384)
    {
        Query q = new Query(kind).setKeysOnly();
        final ArrayList<Key> keys = new ArrayList<Key>();

        dss.prepare(q).asIterable(FetchOptions.Builder.withLimit(128)).each
        {
            keys.add(it.getKey());
        }

        keys.trimToSize();

        if (keys.size() == 0) {
            is_finished = true;
            break;
        }

        while (System.currentTimeMillis() - start < 16384) 
        {

            try 
            {
                dss.delete(keys);
                deleted_count += keys.size();

                break;

            }
            catch (Throwable ignore) 
            {
                continue;
            }
        }
    }
    
    return deleted_count;
}