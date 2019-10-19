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
        super.hash(request, responseObserver);
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
