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
     *  This method is used to hash the password sent by the client and generate a response to send back.
     *  HashRequest request = The request sent which inclues a int32 userId and String password.
     *  StreamObserver<HashResponse> responseObserver = Returns a HashResponse generated in the proto file which includes a
     *  int32 userId, bytes hashedPassword & bytes salt.
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

            // Use the responseObserver’s onNext() method to return the HashRequest,
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
     * This method is used to validate if the password sent by the client matches.
     * ValidateRequest request = The request sent. Includes password as a string, and hashedpassword/salt as a ByteString
     * StreamObserver<BoolValue> responseObserver = returns a bool value depending if the password matches or not.
     * Using the Passwords class which checks if the Byte array of the password and salt matches the user password input .
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void validate(ValidateRequest request, StreamObserver<BoolValue> responseObserver) {

        try {
            // As request.getSalt & getHashedPassword return ByteStrings
            ByteString bsSalt = request.getSalt();
            ByteString bsHashedPassword = request.getHashedPassword();

            // However Passwords.isExpectedPassword requires byte arrays so convert byteStrings above.
            byte[] salt = bsSalt.toByteArray();
            byte[] hashedPassword = bsHashedPassword.toByteArray();

            // Check if actual password matches the salt and hashed password (return true). Else return false to the client.
            if (Passwords.isExpectedPassword(request.getPassword().toCharArray(), salt, hashedPassword)){
                System.out.println("Validation Successful");

                // Send back the bool value as true to the client.
                responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
            }
            else {
                System.out.println("Validation Failed!");
                responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
            }

        } catch (RuntimeException ex) {
            // Send back false if error. This will be handled in the client.
            responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
        }

        responseObserver.onCompleted();
    }
}
