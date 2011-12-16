/**
 *
 * @author ecjet
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

def scoreParams = ["0", "1", "2", "3", "4", "-1", "99"]
def errorResponse = "ERROR"

if(session == null || !Auth.isUserLoggedIn(session)) {
	forward '/WEB-INF/pages/loginValidate.gtpl'
	return
}

String username = (String) session.getAttribute("USERNAME")
String userid = (String) session.getAttribute("USERID")

if(username == null || userid == null) return;

log.info "Validating report"

	Key personKey = KeyFactory.createKey("Person", userid)
	def person
	try {
		person = datastore.get(personKey)
		// TODO nickname change?
	} catch (EntityNotFoundException e) {
		person = new Entity("Person", userid)
		person.uid = userid
		person.created = Time.parse()
		def nick = username
		if(nick.indexOf('@') != -1) {
			nick = nick.substring(0, nick.indexOf('@'))
		}
		person.nickname = nick	
		person.validated = 0
		person.viewed = 0
		person.save()
		request.showGuide = true
	}

if (request.method == "POST") {
	// we have a POST so we could have validation data

	def ok = true
	def reportKeyId = params.reportKeyId
	if (!reportKeyId) {
		log.info "Missing or incorrect photoId: " + reportKeyId
		ok = false
	}
	
	def validator = System.getProperty('naturelocator.validator')
	if (!params.v || params.v != validator)
	{
		log.info "Missing or incorrect validator param: " + params.v
		ok = false
	}

	if(ok) { 
	
		// SCORE PARAMETER
		def score = params.score
		if (!scoreParams.contains(score))
		{
			// skipped
			person.viewed++ 
			person.save()
		} else {

			// find desired report
			def id = Long.parseLong(reportKeyId)
			Key key = KeyFactory.createKey("Report", id)

			try {
				datastore.withTransaction
				{
					// create a new report
					def validation = new Entity("Validation", key)
					validation.score = score
					validation.nickname = username
					validation.uid = userid
					validation.created = Time.parse()
					validation.save()
					request.validation = validation
				}
				person.validated++ 
				person.viewed++ 
				person.save()
			} catch (Exception e) {
			}
		}		
	}
}

request.username = username
request.userid = userid

request.current = person

// get white-list
def wl = new JsonSlurper().parse( new File('WEB-INF/white-list.json') )

def nextReportKeyId = Long.valueOf(wl[person.viewed.intValue()])
Key nextReportKey = KeyFactory.createKey("Report", nextReportKeyId)

try {
	def report = datastore.get(nextReportKey)
	report.displayTime = Time.display(report.displayTime)
	request.report = report
} catch (EntityNotFoundException e) {
	// might be at the end
	if(person.viewed.intValue() < wl.size()) {
		def missingKey = nextReportKey
		request.missingKey = missingKey
	}
}

Query beforeQuery = new Query("Person").setKeysOnly();
beforeQuery.addFilter("validated", Query.FilterOperator.GREATER_THAN, person.validated)
request.position = datastore.prepare(beforeQuery).countEntities(withDefaults()) + 1

def leadersQuery = new Query("Person")
PreparedQuery preparedLeadersQuery = datastore.prepare(leadersQuery)
leadersQuery.addSort("validated", Query.SortDirection.DESCENDING)
def leaders = preparedLeadersQuery.asList(withLimit(5));
request.leaders = leaders

def found = false
leaders.each { i ->
	if(i.key == person.key) found = true
}
request.inLeaders = found

forward '/WEB-INF/pages/reportValidate.gtpl'
