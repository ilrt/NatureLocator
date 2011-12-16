/**
 * List reports in desc chronological order
 * @author cmcpb
 */
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*

if (!Auth.authenticate(response, request, log)) return;

log.info "Getting all users"

def pageSize = params.size ? Integer.parseInt(params.size) : 20
def page = params.page ? Integer.parseInt(params.page) : 1
def offset =  (page-1) * pageSize

def query = new Query("Person")
PreparedQuery preparedQuery = datastore.prepare(query)
query.addSort("nickname", Query.SortDirection.DESCENDING)
def people = preparedQuery.asList(withLimit(pageSize).offset(offset))

people.each { i ->
	i.displayTime = Time.display(i.created)
	}
request.people = people

Query allpeople = new Query("Person").setKeysOnly();
request.total = datastore.prepare(allpeople).countEntities(withDefaults())

request.currentPage = page
request.pageSize = pageSize

forward '/WEB-INF/pages/userList.gtpl'
