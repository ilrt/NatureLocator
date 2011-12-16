/**
 *
 * @author cmcpb
 */
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*

log.info "Plotting reports on maps"

Query allreports = new Query("Report").setKeysOnly();
request.total = datastore.prepare(allreports).countEntities(withDefaults())

forward '/WEB-INF/pages/map.gtpl'
