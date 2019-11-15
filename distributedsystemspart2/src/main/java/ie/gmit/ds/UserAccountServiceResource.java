package ie.gmit.ds;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import ie.gmit.sw.TestClient;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountServiceResource {

    private TestClient passwordClient;
    private HashMap<Integer, User> usersMap = new HashMap<>();

    public UserAccountServiceResource(int port) {
        passwordClient = new TestClient("127.0.0.1", port);

        User testUser = new User(1, "Matthew_S97", "test@gmail.com", "sdfsd", "sdfsd");
        usersMap.put(testUser.getUserId(), testUser);

        User testUser2 = new User(2, "Matthew_S97", "test@gmail.com", "sdfsd", "sdfsd");
        usersMap.put(testUser2.getUserId(), testUser2);

        // {"userId":10, "userName":"Matthew_S97", "email":"test@gmail.com", "hashedPassword":"sdfsd", "salt":"dsfsd"}
    }

    // Get all users
    @GET
    public Collection<User> getUsers() {
        // usersMap.values() returns Collection<User>
        // Collection is the interface implemented by Java collections like ArrayList, LinkedList etc.
        // it's basically a generic list.
        // https://docs.oracle.com/javase/7/docs/api/java/util/Collection.html

        return usersMap.values();
    }

    // Adds a new user
    @POST
    public void addUsers(User user) throws IOException {
        // Will need validation
        String[] hashPasswordSalt = hashPassword(user.getUserId(), user.getPassword());
        User newUser = new User(user.getUserId(), user.getUserName(), user.getEmail(), hashPasswordSalt[0], hashPasswordSalt[1]);

        usersMap.put(user.getUserId(), newUser);
    }

    // Get a specific user
    @GET
    @Path("{userId}")
    public User getUserById(@PathParam("userId") Integer userId) {
        return usersMap.get(userId);
    }

    //Update a user
    @PUT
    @Path("/{userId}")
    public Response updateUser(User user, @PathParam("userId") Integer userId)
    {
        try {
            usersMap.replace(userId, user);
            return Response.status(200).build();
        }
        catch (RuntimeException e){
            return Response.status(400).build();
        }
    }

    //Delete a user
    @DELETE
    @Path("/{userId}")
    public Response deleteUser(User user, @PathParam("userId") Integer userId)
    {
        usersMap.remove(userId);
        return Response.status(200).build();
    }

    // Calls asynchronous hashPassword method in Client
    private String[] hashPassword(int userId, String userPassword)
    {
        // Build a hashRequest object
        HashRequest hashRequest = HashRequest.newBuilder()
                .setUserId(userId)
                .setPassword(userPassword)
                .build();

        return passwordClient.hashPassword(hashRequest);
    }
}
