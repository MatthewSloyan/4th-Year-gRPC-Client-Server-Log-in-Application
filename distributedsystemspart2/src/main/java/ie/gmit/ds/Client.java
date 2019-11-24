package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    // Initialize logger, channel and sync/async services.
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordService;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;

    private String hashedPassword;
    private String salt;

    // Gets to access the hashPassword and salt from the Resource class.
    // This was the only way I could get it to properly access the variables.
    // As initially tried to return an arrayList but it didn't work asynchronously.
    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setNull() {
        this.hashedPassword = null;
        this.salt = null;
    }

    // Constructor to sent up asynchronous and synchronous PasswordServiceGrpc methods.
    public Client(String host, int port) {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        asyncPasswordService = PasswordServiceGrpc.newStub(channel);
        syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
    }

    // Shutdown client
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Asynchronous hash method, which uses the Grpc password service created in part 1
     * to hash the password and return the result with a salt.
     * The password and salt are then converted to strings to be accessed in the API resource class.
     */
    public void hashPassword(HashRequest hashRequest){
        logger.info("Hashing password");

        StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {
            @Override
            public void onNext(HashResponse value) {
                try {
                    // Get the returned ByteString and convert it to a string using ISO-8859-1 encoding.
                    // I tried UTF-8 also which is the default encoding but it didn't work for some reason, however ISO-8859-1 did.
                    // Code adapted from: https://stackoverflow.com/questions/54924619/convert-com-google-protobuf-bytestring-to-string
                    hashedPassword = new String(value.getHashedPassword().toByteArray(), "ISO-8859-1");
                    salt = new String(value.getSalt().toByteArray(), "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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
            }
        };

        try {
            // Build new hashRequest and send to grpc password service created in part 1
            asyncPasswordService.hash(HashRequest.newBuilder()
                    .setUserId(hashRequest.getUserId())
                    .setPassword(hashRequest.getPassword())
                    .build(), responseObserver);
            logger.info("Password hashing sent!");

            // Pause for 2 seconds to ensure password is hashed before Resource accesses it.
            TimeUnit.SECONDS.sleep(2);

        } catch (StatusRuntimeException | InterruptedException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.fillInStackTrace());
        }
    }

    /**
     * Synchronous validate method, which uses the Grpc password service created in part 1
     * to validate if a password matches the hashed password + salt
     *
     * @return String - Response message depending on results to gracefully handle errors.
     */
    public String validatePassword(String password, String hashedPassword, String salt) throws UnsupportedEncodingException {
        String responseMessage = "";

        // Convert the string to ByteStrings for comparision using ISO-8859-1 decoding.
        ByteString hashedPasswordBs = ByteString.copyFrom(hashedPassword.getBytes("ISO-8859-1"));
        ByteString saltBs = ByteString.copyFrom(salt.getBytes("ISO-8859-1"));

        BoolValue result = BoolValue.newBuilder().setValue(false).build();

        try {
            // result is a boolean value which is true if validation is successful and false if not.
            result = syncPasswordService.validate(ValidateRequest.newBuilder()
                    .setPassword(password)
                    .setHashedPassword(hashedPasswordBs)
                    .setSalt(saltBs)
                    .build());

            if(result.getValue()){
                responseMessage = "Successful match";
            }
            else {
                responseMessage = "Unsuccessful match";
            }
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            responseMessage = "System Error";
        }
        return responseMessage;
    }
}
