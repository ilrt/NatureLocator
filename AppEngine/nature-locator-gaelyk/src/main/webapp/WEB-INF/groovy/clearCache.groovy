import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.quota.QuotaService
import com.google.appengine.api.quota.QuotaServiceFactory
import org.apache.commons.io.IOUtils
import org.apache.commons.fileupload.util.Streams
import org.apache.commons.fileupload.servlet.ServletFileUpload
import com.google.appengine.api.images.Image
import com.google.appengine.api.images.ImagesService
import com.google.appengine.api.images.ImagesServiceFactory
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

log.info "Clearing caches"

memcache.clearAll()

sout << "Caches cleared"