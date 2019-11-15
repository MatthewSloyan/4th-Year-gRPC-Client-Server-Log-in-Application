package ie.gmit.sw;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountServiceResource {

    private HashMap<Integer, User> usersMap = new HashMap<>();

    public UserAccountServiceResource() {
        //Artist testArtist = new Artist(1, "The GZA", "HipHop", 2);
        //artistsMap.put(testArtist.getArtistId(), testArtist);

        // {"userId":10, "userName":"Matthew_S97", "email":"test@gmail.com", "hashedPassword":"sdfsd", "salt":"dsfsd"}
    }

    @GET
    public Collection<User> getUsers() {
        // usersMap.values() returns Collection<User>
        // Collection is the interface implemented by Java collections like ArrayList, LinkedList etc.
        // it's basically a generic list.
        // https://docs.oracle.com/javase/7/docs/api/java/util/Collection.html

        return usersMap.values();
    }

//    @POST
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    public void addUsers(@FormParam("user") User user, @Context HttpServletResponse servletResponse) throws IOException {
//        usersMap.put(user);
//    }

    @POST
    public void addUsers(User user) throws IOException {
        usersMap.put(user.getUserId(), user);
    }

    @GET
    @Path("{userId}")
    public User getUserById(@PathParam("userId") Integer userId) {
        return usersMap.get(userId);
    }
}
