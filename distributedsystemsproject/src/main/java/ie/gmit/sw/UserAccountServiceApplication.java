package ie.gmit.sw;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class UserAccountServiceApplication extends Application<UserAccountServiceConfig> {

    public static void main(String[] args) throws Exception {
        new UserAccountServiceApplication().run(args);
    }

    public void run(UserAccountServiceConfig userAccountServiceConfig, Environment environment) throws Exception {

        final ExampleHealthCheck healthCheck = new ExampleHealthCheck();
        environment.healthChecks().register("example", healthCheck);

        final UserAccountServiceResource resource = new UserAccountServiceResource();

        environment.jersey().register(resource);
    }
}
