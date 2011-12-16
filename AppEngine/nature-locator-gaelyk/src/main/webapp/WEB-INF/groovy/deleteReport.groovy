import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*;

if (!Auth.authenticate(response, request, log)) return;

log.info "Deleting report"

def id = Long.parseLong(params.id)
Key key = KeyFactory.createKey("Report", id)

Query userPhotoQuery = new Query("Photo");
userPhotoQuery.setAncestor(key);

def results = datastore.prepare(userPhotoQuery).asList(withDefaults());

results.each { it.delete() }
key.delete()

// clear cache to force new result list
memcache.clearCacheForUri('/reports')

redirect '/reports'