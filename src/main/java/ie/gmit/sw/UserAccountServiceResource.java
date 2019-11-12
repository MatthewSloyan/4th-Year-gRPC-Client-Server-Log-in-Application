package ie.gmit.sw;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountServiceResource {

    private HashMap<Integer, User> usersMap = new HashMap<>();

    public UserAccountServiceResource() {
        //Artist testArtist = new Artist(1, "The GZA", "HipHop", 2);
        //artistsMap.put(testArtist.getArtistId(), testArtist);
    }

    @GET
    public Collection<User> getUsers() {
        // usersMap.values() returns Collection<User>
        // Collection is the interface implemented by Java collections like ArrayList, LinkedList etc.
        // it's basically a generic list.
        // https://docs.oracle.com/javase/7/docs/api/java/util/Collection.html

        return usersMap.values();
    }
}
