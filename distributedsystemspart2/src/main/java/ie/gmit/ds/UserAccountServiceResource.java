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
import ie.gmit.ds.HashRequest;
import ie.gmit.ds.HashResponse;
import ie.gmit.ds.PasswordServiceGrpc;
import ie.gmit.ds.ValidateRequest;
import ie.gmit.sw.TestClient;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountServiceResource {

    private TestClient passwordClient = new TestClient("127.0.0.1", 40000);
    private HashMap<Integer, User> usersMap = new HashMap<>();

    public UserAccountServiceResource() {
        User testUser = new User(1, "Matthew_S97", "test@gmail.com", "sdfsd", "sdfsd");
        usersMap.put(testUser.getUserId(), testUser);

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
        // Not completed
        usersMap.put(user.getUserId(), user);
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
    public Response alterUser(User user, @PathParam("userId") Integer userId)
    {
        try {
            usersMap.replace(userId, user);
            return Response.status(200).build();
        }
        catch (RuntimeException e){
            return Response.status(400).build();
        }
    }

//    private String hashPassword(int userId, String userPassword)
//    {
//        //passwordClient.
//
//        // Build a hashRequest object
//        HashRequest hashRequest = HashRequest.newBuilder()
//                .setUserId(userId)
//                .setPassword(userPassword)
//                .build();
//
//        return "Test";
//    }
}
