import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query.FilterOperator;
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import scripts.*;

if (!Auth.authenticate(response, request, log)) return;

log.info "Deleting user validations"

def name = params.name
if(name == null || name.trim().length() == 0) return "No name"

Key key = KeyFactory.createKey("Person", name)

def person = datastore.get(key)

Query userValidationsQuery = new Query("Validation");
userValidationsQuery.addFilter("uid", FilterOperator.EQUAL, person.uid);

def results = datastore.prepare(userValidationsQuery).asList(withDefaults());

results.each { it.delete() }

person.delete()

redirect '/users'