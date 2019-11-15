package ie.gmit.sw;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import ie.gmit.ds.HashRequest;
import ie.gmit.ds.HashResponse;
import ie.gmit.ds.PasswordServiceGrpc;
import ie.gmit.ds.ValidateRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClient {

    // Initialize logger, channel and sync/async services.
    private static final Logger logger = Logger.getLogger(TestClient.class.getName());
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordService;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;

    private String testPassword;
    private int testUserId = 1;
    private ByteString hashedTestPassword;
    private ByteString saltTest;

    public TestClient(String host, int port) {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        asyncPasswordService = PasswordServiceGrpc.newStub(channel);
        syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void hashPassword(HashRequest hashRequest) {
        logger.info("Hashing password");

        StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {
            @Override
            public void onNext(HashResponse value) {
                // Save results to local variables for testing
                testUserId = value.getUserId();
                hashedTestPassword = value.getHashedPassword();
                saltTest = value.getSalt();
            }

            @Override
            public void onError(Throwable throwable) {
                Status status = Status.fromThrowable(throwable);

                logger.log(Level.WARNING, "RPC Error: {0}", status);
            }

            @Override
            public void onCompleted() {
                logger.info("Finished");
                // End program
                //System.exit(0);
            }
        };

        try {
            asyncPasswordService.hash(HashRequest.newBuilder()
                    .setUserId(hashRequest.getUserId())
                    .setPassword(hashRequest.getPassword())
                    .build(), responseObserver);
            logger.info("Password hashing sent!");
            TimeUnit.SECONDS.sleep(2);
        } catch (
                StatusRuntimeException | InterruptedException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.fillInStackTrace());
            return;
        }
    }

    public void validatePassword() {
        logger.info("Validation!");

        BoolValue result = BoolValue.newBuilder().setValue(false).build();
        logger.info(result.toString() + "Test");

        try {
            result = syncPasswordService.validate(ValidateRequest.newBuilder()
                    .setPassword(testPassword)
                    .setHashedPassword(hashedTestPassword)
                    .setSalt(saltTest)
                    .build());

            logger.info(result.toString());

            if(result.getValue()){
                logger.info("Successful match!");
            }
            else {
                logger.info("Unsuccessful match!");
            }
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }
    }

    public static void main(String[] args) throws Exception {

        Scanner console = new Scanner(System.in);

        int port = 0;
        String menuInput = "";
        int userId = 0;
        String userName;
        String userEmail;
        String userPassword;
        boolean valid = true;
        boolean keepRunning = true;

        // Keep looping until a valid input is entered E.g correct port number
        while (valid){
            try {
                System.out.println("Please enter a port to run server on: ");
                port = console.nextInt();

                // Check if port number is in the valid range.
                if(port >= 1024 && port <= 65535){
                    valid = false;
                }
                else {
                    System.out.print("Invalid Port number (Must be in the range of 1024 to 65535).");
                }
            }
            catch (RuntimeException e){
                System.out.println("Invalid input, please try again.");
            }
        }

        TestClient client = new TestClient("localhost", port);

        while(keepRunning) {
            System.out.println("======= USER SERVICE =======");

            System.out.println("Please select an option:\n (1) Create user\n (2) Get user\n " +
                    "(3) List all users\n (4) Login\n (5) Exit Program");
            menuInput = console.next();

            try {
                switch (Integer.parseInt(menuInput))
                {
                    case 1:
                        // Create a user
                        System.out.println("Please enter a user name: ");
                        userName = console.next();

                        System.out.println("Please enter a email address: ");
                        userEmail = console.next();

                        System.out.println("Please enter a password: ");
                        userPassword = console.next();

                        // Build a hashRequest object
                        HashRequest hashRequest = HashRequest.newBuilder()
                                .setUserId(userId)
                                .setPassword(userPassword)
                                .build();
                        try {
                            client.hashPassword(hashRequest); // asynchronous
                        }catch (RuntimeException e) {
                            e.getMessage();
                        }
                        break;
                    case 2:
                        // Get specific user
                        System.out.println("Please enter a userID: ");
                        userId = console.nextInt();
                        break;
                    case 3:
                        // List all users
                        break;
                    case 4:
                        // Login
//                    System.out.println("Please enter a userID: ");
//                    userId = console.nextInt();
//
//                    System.out.println("Please enter a password: ");
//                    userPassword = console.next();
//
//                    client.validatePassword(); // synchronous
                        break;
                    default:
                        keepRunning = false;
                } // menu selection switch
            }
            catch (RuntimeException e){
                System.out.println("Invalid input, please try again.\n");
            }
        }

        //System.out.println("Please enter a test password: ");
        //client.testPassword = console.next();

        // Start client once valid.
        //new TestClient("localhost", port).display();
    }
}
