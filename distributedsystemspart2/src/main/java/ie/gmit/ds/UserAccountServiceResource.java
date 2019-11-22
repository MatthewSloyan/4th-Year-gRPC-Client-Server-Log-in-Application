package ie.gmit.ds;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountServiceResource {

    private Client passwordClient;
    private HashMap<Integer, User> usersMap = new HashMap<>();

    public UserAccountServiceResource(int port) throws InterruptedException, UnsupportedEncodingException{
        passwordClient = new Client("127.0.0.1", port);

        hashPassword(1, "test");

        User testUser = new User(1, "Matthew_S97", "test@gmail.com",
                passwordClient.getHashedPassword(), passwordClient.getSalt());

        usersMap.put(1, testUser);
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
    public Response addUsers(UserPost userPost){

        hashPassword(userPost.getUserId(), userPost.getPassword());

        User newUser = new User(userPost.getUserId(), userPost.getUserName(), userPost.getEmail(),
                passwordClient.getHashedPassword(), passwordClient.getSalt());

        usersMap.put(userPost.getUserId(), newUser);

        return Response.status(201).build();
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
        if(usersMap.containsKey(userId)){
            usersMap.replace(userId, user);
            return Response.status(200).build();
        }
        else {
            return Response.status(400).type(MediaType.TEXT_PLAIN).entity("User not found!").build();
        }
    }

    //Delete a user
    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") Integer userId)
    {
        if(usersMap.containsKey(userId)){
            usersMap.remove(userId);
            return Response.status(200).build();
        }
        else {
            return Response.status(400).type(MediaType.TEXT_PLAIN).entity("User not found!").build();
        }
    }

    // Validates a users login
    @POST
    @Path("/login")
    public Response login(UserLogin userLogin) throws UnsupportedEncodingException {
        String responseMessage;

        if(usersMap.containsKey(userLogin.getUserId())){
            // First check if user exists
            responseMessage = passwordClient.validatePassword(userLogin.getPassword(),
                    usersMap.get(userLogin.getUserId()).getHashedPassword(),
                    usersMap.get(userLogin.getUserId()).getSalt());

            if (responseMessage == "Successful match"){
                return Response.status(200).type(MediaType.TEXT_PLAIN).entity("Validation Successful.").build();
            }
            else if (responseMessage == "Unsuccessful match"){
                return Response.status(400).type(MediaType.TEXT_PLAIN).entity("User ID or password incorrect.").build();
            }
            else {
                return Response.status(400).type(MediaType.TEXT_PLAIN).entity("System Error!").build();
            }
        }
        else {
            return Response.status(400).type(MediaType.TEXT_PLAIN).entity("User ID or password incorrect.").build();
        }
    }

    // Calls asynchronous hashPassword method in Client
    private void hashPassword(int userId, String userPassword)
    {
        // Build a hashRequest object
        HashRequest hashRequest = HashRequest.newBuilder()
                .setUserId(userId)
                .setPassword(userPassword)
                .build();

        passwordClient.hashPassword(hashRequest);
    }
}
