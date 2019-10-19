package ie.gmit.sw;

import com.google.protobuf.BoolValue;
import ie.gmit.ds.HashRequest;
import ie.gmit.ds.HashResponse;
import ie.gmit.ds.PasswordServiceGrpc;
import ie.gmit.ds.ValidateRequest;
import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

    /**
     * <pre>
     * HashRequest is sent as a parameter and waits for sever to respond with hash response.
     * </pre>
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

        } catch (RuntimeException ex) {
            responseObserver.onNext(HashResponse.newBuilder().getDefaultInstanceForType());
        }
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
