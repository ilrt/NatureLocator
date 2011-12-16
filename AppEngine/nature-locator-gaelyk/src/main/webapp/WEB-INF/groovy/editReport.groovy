/**
 *
 * @author cmcpb
 */
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import scripts.*;

if (!Auth.authenticate(response, request, log)) return;

log.info "Editing report"

def id = Long.parseLong(params.id)
Key key = KeyFactory.createKey("Report", id)
def report = datastore.get(key)

request.report = report
forward '/WEB-INF/pages/reportAdd.gtpl'
