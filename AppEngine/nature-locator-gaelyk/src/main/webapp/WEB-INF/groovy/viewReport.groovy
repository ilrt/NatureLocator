/**
 *
 * @author cmcpb
 */
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*;

if (!Auth.authenticate(response, request, log)) return;

log.info "Viewing full report"

def id = Long.parseLong(params.id)
Key key = KeyFactory.createKey("Report", id)
def report = datastore.get(key)

report.created = Time.display(report.created)
report.displayTime = Time.display(report.displayTime)
request.report = report

def valQuery = new Query("Validation")
PreparedQuery preparedValQuery = datastore.prepare(valQuery)
valQuery.addSort("created", Query.SortDirection.DESCENDING)
valQuery.setAncestor(key)
def validations = preparedValQuery.asList(withDefaults())
validations.each { i ->
	i.created = Time.display(i.created)
}
request.validations = validations

forward '/WEB-INF/pages/reportView.gtpl'
