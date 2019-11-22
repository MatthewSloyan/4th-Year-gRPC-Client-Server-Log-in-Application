package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public Client(String host, int port) {
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

    public void hashPassword(HashRequest hashRequest){
        logger.info("Hashing password");

        StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {
            @Override
            public void onNext(HashResponse value) {
                try {
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

        } catch (StatusRuntimeException | InterruptedException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.fillInStackTrace());
        }
    }

    public String validatePassword(String password, String hashedPassword, String salt) throws UnsupportedEncodingException {
        String responseMessage = "";

        byte[] bp = hashedPassword.getBytes("ISO-8859-1");
        ByteString hashedPasswordBs = ByteString.copyFrom(bp);

        byte[] bs = salt.getBytes("ISO-8859-1");
        ByteString saltBs = ByteString.copyFrom(bs);

        BoolValue result = BoolValue.newBuilder().setValue(false).build();

        try {
            result = syncPasswordService.validate(ValidateRequest.newBuilder()
                    .setPassword(password)
                    .setHashedPassword(hashedPasswordBs)
                    .setSalt(saltBs)
                    .build());

            logger.info(result.toString());

            if(result.getValue()){
                logger.info("Successful match!");
                responseMessage = "Successful match";
            }
            else {
                logger.info("Unsuccessful match!");
                responseMessage = "Unsuccessful match";
            }
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            responseMessage = "System Error";
        }
        return responseMessage;
    }
}
