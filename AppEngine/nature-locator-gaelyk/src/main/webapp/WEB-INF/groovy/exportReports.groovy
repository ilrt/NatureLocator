/**
 *
 * @author cmcpb
 */
import java.util.zip.ZipEntry
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import java.util.zip.ZipOutputStream;
import scripts.*;

sep = "\t";
sep_replace = "[tab]"
csvFile = "reports.csv"
publicaccess = false;

if (!Auth.authenticate(response, request, log)) return;

if (params.submit == null)
{
	forward '/WEB-INF/pages/export.gtpl'
	return;
}
if (params.submit == "inline") publicaccess = true;

// CSV file header
def csvfileContents = "ID" + sep + "Submitted date"+sep+"Sighting date"+sep+"uid"+sep+"email"+sep+"Location"+sep+"Submitted location"+sep+"Damage"+sep+"Leaf litter"+sep+"photo"+"\n"
if (publicaccess) csvfileContents =  "Submitted date"+sep+"Location"+sep+"Damage"+sep+"Leaf litter"+sep+"photo"+"\n"

// set up query parameters
def restrictions = withDefaults()
if (params.qty != null && params.qty != "All" ) 
{
	def qty = Integer.parseInt(params.qty)
	log.info "Listing " + qty + " reports"
	restrictions = withLimit(qty)
}
def offset = 0;
if (params.offset) offset = Integer.parseInt(params.offset)


if (params.images != null)
{
	log.info "Exporting reports + photos"

	def query = new Query("Photo")
	preparedQuery = datastore.prepare(query)
	query.addSort("created", Query.SortDirection.DESCENDING)
	def photos = preparedQuery.asList( restrictions.offset(offset) )

	// generate zip file of reports
	def target = "export.zip";

	// set content type
	response.addHeader('Content-Type','application/zip')
	response.addHeader('Content-Disposition','attachment;filename='+target)

	// Create a buffer for reading the files
	byte[] buf = new byte[1024];

	try 
	{
		// Create the ZIP stream
		ZipOutputStream zout = new ZipOutputStream(sout);

		// Add the photos
		photos.each
		{		
			// obtain the corresponding report data		
			def report = datastore.get(it.getParent())

			def filename = report.getKey().getId()

			if (it.imagetype.contains("jpg")) filename += ".jpg"
			else if (it.imagetype.contains("gif")) filename += ".gif"
			else if (it.imagetype.contains("png")) filename += ".png"
			else if (it.imagetype.contains("jpeg")) filename += ".jpeg"
			else if (it.imagetype.contains("pjpg")) filename += ".pjpg"
			else if (it.imagetype.contains("tiff")) filename += ".tiff"
			else if (it.imagetype.contains("ico")) filename += ".ico"
			else if (it.imagetype.contains("bmp")) filename += ".bmp"
			else filename += ".unknown"

			// Add ZIP entry to output stream.
			zout.putNextEntry(new ZipEntry(filename));

			// Transfer bytes from the file to the ZIP file
			zout.write(it.full.getBytes(), 0, it.full.getBytes().size());

			// Complete the entry
			zout.closeEntry();

			// write report data to csv file
			def uid = sanitize(report.uid)
			def name = sanitize(report.name)
			def email = sanitize(report.email)
			def litter = sanitize(report.litter)
			def score = sanitize(report.score)
			def location = sanitize(report.location)
			def origloc = sanitize(report.originallocation)
			csvfileContents += report.getKey().getId() + sep + Time.display(report.created) + sep + Time.display(report.displayTime) + sep + uid + sep + email + sep + location + sep + origloc + sep + score + sep + litter + sep + filename + "\n"
		}

		// finally add the csv file
		zout.putNextEntry(new ZipEntry(csvFile));
		zout.write(csvfileContents.getBytes(), 0, csvfileContents.getBytes().size());
		zout.closeEntry();

		// Complete the ZIP file
		zout.close();
	} 
	catch (IOException e) {
		log.warning e.toString()
	}
}
else
{
	// download metadata only
	def query = new Query("Report")
	preparedQuery = datastore.prepare(query)
	query.addSort("created", Query.SortDirection.DESCENDING)
	def reports = preparedQuery.asList( restrictions.offset(offset) )

	if (params.submit != "inline") 
	{
		response.addHeader('Content-Type','text/csv')
		response.addHeader('Content-Disposition','attachment;filename='+csvFile)
	}

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
		if (publicaccess) 
		{
			// restrict export contents
			csvfileContents += Time.display(report.created) + sep + location + sep + score + sep + litter + sep + filename + "\n"
		}
		else csvfileContents += id + sep + Time.display(report.created) + sep + Time.display(report.displayTime) + sep + uid + sep + email + sep + location + sep + origloc + sep + score + sep + litter + sep + filename + "\n"
	}

	sout << csvfileContents
}

String sanitize(String input)
{
	if (input == null) return ""
	input = input.replaceAll(sep,sep_replace);
	input = input.replaceAll("\n"," ");
	input = input.replaceAll("\r","");
	return input
}