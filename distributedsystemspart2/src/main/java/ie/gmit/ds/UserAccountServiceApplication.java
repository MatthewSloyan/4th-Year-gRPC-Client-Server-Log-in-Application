package ie.gmit.ds;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.util.Scanner;

public class UserAccountServiceApplication extends Application<UserAccountServiceConfig> {

    public static void main(String[] args) throws Exception {
        new UserAccountServiceApplication().run(args);
    }

    public void run(UserAccountServiceConfig userAccountServiceConfig, Environment environment) {

        Scanner console = new Scanner(System.in);
        String port = "";
        boolean valid = true;

        // Keep looping until a valid input is entered E.g correct port number
        while (valid){
            try {
                System.out.println("Please enter a port to run server on: ");
                port = console.next();

                // Check if port number is in the valid range.
                if(Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535){
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

        // Server setup
        final ExampleHealthCheck healthCheck = new ExampleHealthCheck();
        environment.healthChecks().register("example", healthCheck);

        final UserAccountServiceResource resource = new UserAccountServiceResource(Integer.parseInt(port), environment.getValidator());

        environment.jersey().register(resource);
    }
}
