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

if (!Auth.authenticate(response, request, log)) return;

// CSV file header
def csvfileContents =  "Created"+sep+"Report"+sep+"Damage"+sep+"Userid"+sep+"Nickname"+"\n"

// set up query parameters
def restrictions = withDefaults()

def pageSize = params.size ? Integer.parseInt(params.size) : 20
def page = params.page ? Integer.parseInt(params.page) : 1
def offset =  (page-1) * pageSize

// download metadata only
def query = new Query("Validation")
preparedQuery = datastore.prepare(query)
query.addSort("created", Query.SortDirection.DESCENDING)
def validations = preparedQuery.asList(withLimit(pageSize).offset(offset))

validations.each
{	
	validation = it
	// write validation data to csv file
	def rId = validation.getParent().getId() 
	def uid = sanitize(validation.uid)
	def nickname = sanitize(validation.nickname)
	def score = sanitize(validation.score)
	
	csvfileContents += Time.display(validation.created) + sep + rId + sep + score + sep + uid + sep + nickname + "\n"
}
response.addHeader("Content-Disposition", "attachment;filename=validations.csv")
response.setContentType("text/csv")
sout << csvfileContents

String sanitize(String input)
{
	if (input == null) return ""
	input = input.replaceAll(sep,sep_replace);
	input = input.replaceAll("\n"," ");
	input = input.replaceAll("\r","");
	return input
}