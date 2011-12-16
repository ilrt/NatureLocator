import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.quota.QuotaService
import com.google.appengine.api.quota.QuotaServiceFactory
import org.apache.commons.io.IOUtils
import org.apache.commons.fileupload.util.Streams
import org.apache.commons.fileupload.servlet.ServletFileUpload
import com.google.appengine.api.images.Image
import com.google.appengine.api.images.ImagesService
import com.google.appengine.api.images.ImagesServiceFactory
import com.google.appengine.api.images.ImagesService.OutputEncoding
import com.google.appengine.api.images.OutputSettings
import com.google.appengine.api.images.Transform
import com.google.appengine.api.images.CompositeTransform
import com.google.appengine.api.datastore.*
import com.google.appengine.api.blobstore.*
import com.google.appengine.api.files.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.io.*;
import java.nio.ByteBuffer;
import scripts.*

log.info "Inserting report"

def errorResponse = "ERROR"
def litterParams = ["bare", "short", "dead", "long", "scrub", "stone", "various", "unlisted"]
def scoreParams = ["0", "1", "2", "3", "4", "-1"]
	
/* gets image upload. From Mr. Haki's blog - http://mrhaki.blogspot.com/2009/11/add-file-upload-support-to-groovlets-in.html */
uploads = [:]  // Store result from multipart content.
if (ServletFileUpload.isMultipartContent(request))
{
	def uploader = new ServletFileUpload()  // Cannot use diskbased fileupload in Google App Engine!
	def items = uploader.getItemIterator(request)
	while (items.hasNext())
	{
		def item = items.next()
		def stream = item.openStream()
		try {
			if (item.formField)
			{  // 'Normal' form field.
				params[item.fieldName] = Streams.asString(stream)
			} else
			{
				uploads[item.fieldName] = [
					name: item.name,
					contentType: item.contentType,
					data: IOUtils.toByteArray(stream)
				]
				log.info "Content type: "+item.contentType
			}
		}
		finally
		{
			IOUtils.closeQuietly stream
		}
	}
}
/* end upload processing */

def validator = System.getProperty('naturelocator.validator')
if (!params.v || params.v != validator)
{
	log.info "Missing or incorrect validator param: " + params.v
	response.status = 400
	sout << errorResponse
	return;
}

try
{
	// generate thumbnail
	ImagesService imagesService = ImagesServiceFactory.getImagesService();
	Image pic = ImagesServiceFactory.makeImage( uploads['photo'].data )
	byte [] imageData = pic.getImageData()
	log.info "Image size:" + imageData.size() 
	OutputSettings os = new OutputSettings(OutputEncoding.JPEG)
	os.setQuality(80)
	
	def fullImage = null
	def thumbImage = null
	try
	{
		// following code works for jpegs only
		if (imageData.size() > 900000) 
		{
			log.info "original image too large, resizing to 1024x1024"
			pic = images.applyTransform(images.makeResize(1024,1024), pic, os)
			imageData = pic.getImageData()
		}

		log.info "Image size:" + imageData.size() 
		if (imageData.size() > 900000) 
		{
			log.info "800x800 image still too large, resizing further to 800x800"
			pic = images.applyTransform(images.makeResize(800,800), pic, os)
			imageData = pic.getImageData()
		}

		fullImage = new Blob (imageData)
	}
	catch (Exception e)
	{
		log.info "Image processing failed - possible image format issue"
		log.info e.toString()
		// catch other forms of image and process them more simply
		fullImage = new Blob (pic.resize(600,600).getImageData())
	}
	
	try
	{
		thumbImage = new Blob (images.applyTransform(images.makeResize(80,80), pic, os).getImageData())
	}
	catch (Exception e)
	{
		thumbImage = new Blob (pic.resize(80,80).getImageData())
	}
	
	// UID PARAMETER
	def uid = params.uid
	if (!uid == null || uid == "")
	{
		log.info "uid param missing"
		response.status = 400
		sout << errorResponse
		return;
	}
	
	// LOCATION PARAMETER
	// work out the location
	// ~ creates a Pattern from String for matching "lat,lng"
	def pattern = ~/^-?\d+\.?\d*,-?\d+\.?\d*$/
	def location = params.location
	def originallocation = location
	if (location && !pattern.matcher(location).matches())
	{
		log.info "No precise location given, geocoding..."
		location = GeoCode.get(location+",uk")
	}

	// SCORE PARAMETER
	def score = params.score
	if (!scoreParams.contains(score))
	{
		log.info "score param invalid: " + score
		response.status = 400
		sout << errorResponse
		return;
	}
	
	// LITTER PARAMETER
	def litter = params.litter ? params.litter.toString().toLowerCase() : ""
	if (!litterParams.contains(litter))
	{
		log.info "litter param invalid: " + litter
		response.status = 400
		sout << errorResponse
		return;
	}
	
	def email = params.email ? params.email : ""
	
	def key;
	datastore.withTransaction 
	{
		// create a new report
		def report = new Entity("Report")
		report.uid = uid
		report.score = score
		report.litter = litter
		report.email = email
		report.originallocation = originallocation
		report.location = location
		report.created = Time.parse()
		report.displayTime = Time.parse(params.created)
		report.save()

		key = report.getKey().getId()
		
		// Save the image
		def photo = new Entity("Photo", report.getKey())
		photo.user = params.user
		photo.imagetype = uploads['photo'].contentType.toLowerCase()
		photo.full = fullImage
		photo.thumb = thumbImage
		photo.created = new Date()
		photo.save()
	}

	// send email notification
//	mail.send from: "chris.p.bailey@gmail.com",
//		to: "chris.p.bailey@gmail.com",
//		subject: "Report created",
//		textBody: "New report uploaded.\n\nhttps://"+app.id+".appspot.com/reports/"+key

	if (params.device && params.device.equalsIgnoreCase("mobile"))
	{
		log.info "Returning success indicator to device"
		sout << "SUCCESS"
	}
	else
	{
		redirect '/reports'
	}
}
catch (Exception e)
{
	log.info "ERROR " + e
	response.status = 400
	sout << errorResponse
	return;
}
