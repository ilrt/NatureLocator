/**
 * List reports in desc chronological order
 * @author cmcpb
 */
import net.sf.json.groovy.JsonSlurper
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.EntityNotFoundException
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*;

if (!Auth.authenticate(response, request, log)) return;

log.info "Getting white list reports"

// get white-list
def wl = new JsonSlurper().parse( new File('WEB-INF/white-list.json') )

def pageSize = params.size ? Integer.parseInt(params.size) : 20
def page = params.page ? Integer.parseInt(params.page) : 1
def offset =  (page-1) * pageSize

def reports = []
for(x = offset; x < (offset + pageSize); x++) {
	def reportKeyId = Long.valueOf(wl[x])
	Key reportKey = KeyFactory.createKey("Report", reportKeyId)
	try {
		def report = datastore.get(reportKey)
		report.displayTime = Time.display(report.displayTime)
		reports.add(report)
	} catch (EntityNotFoundException e) {
		// might be at the end
		if(x < wl.size()) {
			def missingKey = reportKey
			log.info "Missing white listed report: " + reportKeyId
		}
	}
	
}

def valQuery = new Query("Validation")
PreparedQuery preparedValQuery = datastore.prepare(valQuery)
valQuery.addSort("created", Query.SortDirection.DESCENDING)

reports.each { i ->
	i.displayTime = Time.display(i.displayTime)
	valQuery.setAncestor(i.key)
	i.validationCount = preparedValQuery.countEntities(withDefaults())
	}
request.reports = reports

request.total = wl.size()

request.currentPage = page
request.pageSize = pageSize

forward '/WEB-INF/pages/reportWhiteList.gtpl'
