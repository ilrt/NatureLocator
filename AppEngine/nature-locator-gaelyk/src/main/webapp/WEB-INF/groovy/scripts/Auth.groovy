package scripts

import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.memcache.MemcacheServiceFactory
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

import org.ilrt.nl.oauth.OAuthUser

/**
 *
 * @author cmcpb
 */
class Auth {
	
	static boolean isUserLoggedIn(session) {
		if(session == null) return false
		return (session.getAttribute("USERID") != null)
	}
	
	static boolean authenticate(response, request, log)
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		def memcache = MemcacheServiceFactory.memcacheService
		
		if (user != null) {
			
			if (!memcache.AuthUsers)
			{			
				def query = new Query("AuthUsers")
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
				PreparedQuery preparedQuery = datastore.prepare(query)
				def authUsers = preparedQuery.asList( withDefaults() ).collect{ it.name }

				log.info "auth users added to memcache"
				authUsers.each { auser -> log.info auser }
				memcache.AuthUsers = authUsers
			}
			if (memcache.AuthUsers.contains(user.getNickname().toLowerCase()))
			{
				// auth
			}
			else
			{
				log.info "Unrecognized user: " + user.getNickname()
				response.sendRedirect("/");
				return false;
			}
		} 
		else {
			response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
			return false;
		}
		
		return true;
	}
}

