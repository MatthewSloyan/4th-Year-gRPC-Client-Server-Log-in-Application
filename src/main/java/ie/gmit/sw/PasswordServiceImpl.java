package ie.gmit.sw;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import ie.gmit.ds.HashRequest;
import ie.gmit.ds.HashResponse;
import ie.gmit.ds.PasswordServiceGrpc;
import ie.gmit.ds.ValidateRequest;
import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

    /**
     *  This method is used to generate a response to send back to the client.
     *  HashRequest request = The request sent.
     *  StreamObserver<HashResponse> responseObserver = Is a special interface for the server to call with its response.
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void hash(HashRequest request, StreamObserver<HashResponse> responseObserver) {

        try {
            // Take in password from request (Sent from client).
            char[] password = request.getPassword().toCharArray();

            // Get a random salt from Passwords class.
            byte[] salt = Passwords.getNextSalt();

            // Hash the password using the new salt.
            byte[] hashedPassword = Passwords.hash(password, salt);

            // As Google Protocol Buffers method returns a ByteString the byte array must be converted first.
            // To achieve this I research and found the documentation for ByteStrings
            // https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/ByteString
            ByteString bsSalt = ByteString.copyFrom(salt);
            ByteString bsHashedPassword = ByteString.copyFrom(hashedPassword);

            // We use the responseObserver’s onNext() method to return the HashRequest,
            // while populating it with the userId, salt, and hashedPassword.
            responseObserver.onNext(HashResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setSalt(bsSalt)
                    .setHashedPassword(bsHashedPassword)
                    .build());

        } catch (RuntimeException ex) {
            // Send back an error message to client
            responseObserver.onNext(HashResponse.newBuilder().getDefaultInstanceForType());
        }
        // Specifies that we’ve finished dealing with the RPC.
        responseObserver.onCompleted();
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void validate(ValidateRequest request, StreamObserver<BoolValue> responseObserver) {
        super.validate(request, responseObserver);
    }
}
