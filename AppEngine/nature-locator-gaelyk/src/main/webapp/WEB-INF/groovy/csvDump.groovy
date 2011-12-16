/**
 *
 * @author cmcpb
 */
import java.util.zip.ZipEntry
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import java.util.zip.ZipOutputStream
import net.sf.json.*
import groovy.json.JsonOutput
import scripts.*

sep = "\t";
sep_replace = "[tab]"

//if (!Auth.authenticate(response, request, log)) return;

// CSV file header
def csvfileContents =  "Submitted date"+sep+"Location"+sep+"Damage"+sep+"Leaf litter"+sep+"photo"+"\n"

// set up query parameters
def restrictions = withDefaults()

def pageSize = params.size ? Integer.parseInt(params.size) : 20
def page = params.page ? Integer.parseInt(params.page) : 1
def offset =  (page-1) * pageSize

def type = headers["Accept"].contains("json") ? "json" : "csv"

if (type == "json")
{
	response.contentType = "application/json"
	records = []
}

// download metadata only
def query = new Query("Report")
preparedQuery = datastore.prepare(query)
query.addSort("created", Query.SortDirection.DESCENDING)
def reports = preparedQuery.asList(withLimit(pageSize).offset(offset))

if (type == "json") content = "["
reports.each
{	
	report = it
	// write report data to csv file
	def id = report.getKey().getId() 
	def uid = sanitize(report.uid)
	def name = sanitize(report.name)
	def email = sanitize(report.email)
	def litter = sanitize(report.litter)
	def score = sanitize(report.score)
	def location = sanitize(report.location)
	def origloc = sanitize(report.originallocation)
	def filename = "https://"+app.id+".appspot.com/image/"+id
	
	if (type == "json")
	{
		content += '{"date":'+report.displayTime+', "location":"'+location+'", "score":'+score+', "litter":"'+litter+'"},'
	}
	else csvfileContents += Time.display(report.created) + sep + location + sep + score + sep + litter + sep + filename + "\n"
}

if (type == "json") sout << content.substring(0,content.length()-1)+ "]"
else sout << csvfileContents

String sanitize(String input)
{
	if (input == null) return ""
	input = input.replaceAll(sep,sep_replace);
	input = input.replaceAll("\n"," ");
	input = input.replaceAll("\r","");
	return input
}