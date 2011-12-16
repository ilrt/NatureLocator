import com.google.appengine.api.datastore.Entity

def adminUsers = System.getProperty('admin.users')

users = adminUsers.split()

users.each { i ->
    def user = new Entity("AuthUsers")
    user.name = i
    user.save()
}

sout << "Created "+users.size()+" users"

