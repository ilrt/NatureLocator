import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.images.Image
import org.apache.commons.lang.StringEscapeUtils
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*;

if (!Auth.isUserLoggedIn(session) && !Auth.authenticate(response, request, log)) return

/**
 * Serves an image or thumbnail
 **/

try
{
	// find desired image
	def id = Long.parseLong(params.id)
	def thumb = params.thumb ? true : false

	Key key = KeyFactory.createKey("Report", id)

	Query userPhotoQuery = new Query("Photo");
	userPhotoQuery.setAncestor(key);

	def results = datastore.prepare(userPhotoQuery).asList(withDefaults());

	def image = results[0]
	Blob imageData = thumb ? image.thumb : image.full

	// serve the first image
	response.setContentType(image.imagetype);
	sout << imageData.getBytes()
}
catch (Exception e)
{
	response.status = 404
	log.info "Unable to find image for '" + StringEscapeUtils.escapeJava(params.id) + "'"
	sout << "Unable to find image"
}