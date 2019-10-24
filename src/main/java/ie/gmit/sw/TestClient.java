package ie.gmit.sw;

import ie.gmit.ds.HashRequest;
import ie.gmit.ds.HashResponse;
import ie.gmit.ds.PasswordServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClient {

    // Initialize clients and
    private static final Logger logger = Logger.getLogger(TestClient.class.getName());
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordService;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;

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

        HashResponse result = HashResponse.newBuilder().getDefaultInstanceForType();
        try {
            result = syncPasswordService.hash(hashRequest);
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }
        if (result.getHashedPassword() != null) {
            logger.info("Successful");
        } else {
            logger.warning("Failed to hash password");
        }
    }

    public static void main(String[] args) throws Exception {
        TestClient client = new TestClient("localhost", 50551);

        Scanner console = new Scanner(System.in);

        System.out.println("Please enter a userID: ");
        int userId = console.nextInt();

        System.out.println("Please enter a password: ");
        String userPassword = console.next();

        // Build a hashRequest object
        HashRequest hashRequest = HashRequest.newBuilder()
                .setUserId(userId)
                .setPassword(userPassword)
                .build();
        try {
            client.hashPassword(hashRequest); // synchronous
            //client.validatePassword(); // asynchronous
        } finally {
            // Don't stop process, keep alive to receive async response
            Thread.currentThread().join();
        }
    }
}
