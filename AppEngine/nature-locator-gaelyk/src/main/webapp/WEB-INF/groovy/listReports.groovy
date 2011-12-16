/**
 * List reports in desc chronological order
 * @author cmcpb
 */
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*

if (!Auth.authenticate(response, request, log)) return;

log.info "Getting all reports"

def pageSize = params.size ? Integer.parseInt(params.size) : 20
def page = params.page ? Integer.parseInt(params.page) : 1
def offset =  (page-1) * pageSize

def query = new Query("Report")
PreparedQuery preparedQuery = datastore.prepare(query)
query.addSort("created", Query.SortDirection.DESCENDING)
def reports = preparedQuery.asList(withLimit(pageSize).offset(offset))

def valQuery = new Query("Validation")
PreparedQuery preparedValQuery = datastore.prepare(valQuery)
valQuery.addSort("created", Query.SortDirection.DESCENDING)

reports.each { i ->
	i.displayTime = Time.display(i.displayTime)
	valQuery.setAncestor(i.key)
	i.validationCount = preparedValQuery.countEntities(withDefaults())
	}
request.reports = reports

Query allreports = new Query("Report").setKeysOnly();
request.total = datastore.prepare(allreports).countEntities(withDefaults())

request.currentPage = page
request.pageSize = pageSize

forward '/WEB-INF/pages/reportList.gtpl'
