package ie.gmit.sw;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class PasswordServer {
    private Server grpcServer;
    private static final Logger logger = Logger.getLogger(PasswordServer.class.getName());
    private static final int PORT = 50551;

    private void start() throws IOException {
        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new PasswordServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
    }

    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }
}
