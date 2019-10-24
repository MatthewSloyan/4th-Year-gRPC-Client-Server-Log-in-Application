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

    private void hashPassword(HashRequest hashRequest) {
        logger.info("Hashing password");

        HashResponse result = HashResponse.newBuilder().getDefaultInstanceForType();
        try {
            result = syncPasswordService.hash(hashRequest);

            // Save results to local variables for testing
            hashedTestPassword = result.getHashedPassword();
            saltTest = result.getSalt();
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

    private void validatePassword() {
        StreamObserver<BoolValue> responseObserver = new StreamObserver<BoolValue>() {
            @Override
            public void onNext(BoolValue value) {
                logger.info("Validation!");
                if(value.getValue()){
                    logger.info("Successful match!");
                }
                else {
                    logger.info("Unsuccessful match!");
                }
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
                System.exit(0);
            }
        };

        try {
            asyncPasswordService.validate(ValidateRequest.newBuilder()
                    .setPassword(testPassword)
                    .setHashedPassword(hashedTestPassword)
                    .setSalt(saltTest)
                    .build(), responseObserver);
            logger.info("Validation returned ");
        } catch (
                StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        TestClient client = new TestClient("localhost", 50551);

        Scanner console = new Scanner(System.in);

        System.out.println("Please enter a userID: ");
        int userId = console.nextInt();

        System.out.println("Please enter a password: ");
        String userPassword = console.next();

        System.out.println("Please enter a test password: ");
        client.testPassword = console.next();

        // Build a hashRequest object
        HashRequest hashRequest = HashRequest.newBuilder()
                .setUserId(userId)
                .setPassword(userPassword)
                .build();
        try {
            client.hashPassword(hashRequest); // synchronous
            client.validatePassword(); // asynchronous
        } finally {
            // Don't stop process, keep alive to receive async response
            Thread.currentThread().join();
        }
    }
}
