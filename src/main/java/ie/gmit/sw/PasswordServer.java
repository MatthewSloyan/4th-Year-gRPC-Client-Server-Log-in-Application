package ie.gmit.sw;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class PasswordServer {
    private Server grpcServer;
    private static final Logger logger = Logger.getLogger(PasswordServer.class.getName());
    private static int PORT;

    /**
     * Start gRPC server on a valid port entered by the user, and build using
     *
     * @param port
     */
    private void start(int port) throws IOException {
        PORT = port;

        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new PasswordServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
    }

    /**
     * Stop gRPC server
     */
    private void stop() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    /**
     * main to run the server. This starts the server and stops it when requested. E.g Ctrl C
     * Valid Port numbers and in the range of 1024 to 65535, as 0 to 1024 are reserved for privileged services such as HTTP etc.
     * The max port number is and unsigned 16-bit integer, so 65535.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner console = new Scanner(System.in);

        int port = 0;
        boolean valid = true;

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

        // Start server once valid.
        final PasswordServer passwordServer = new PasswordServer();
        passwordServer.start(port);
        passwordServer.blockUntilShutdown();
    }
}
