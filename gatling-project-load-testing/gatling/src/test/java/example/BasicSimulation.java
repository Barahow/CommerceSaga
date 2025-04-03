package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.core.Simulation;

import java.util.List;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

  public class BasicSimulation extends Simulation {

    // Load virtual user count from system properties, defaulting to 500 users
    private static final int vu = Integer.getInteger("vu", 500);

    // Define HTTP configuration (update the baseUrl as needed)
    private static final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Request bodies for valid and invalid login attempts
    private static final String validBody = "{\"email\": \"test@gmail.com\", \"password\": \"1234\"}";
    private static final String invalidBody = "{\"email\": \"test@example.com\", \"password\": \"invalidPass\"}";

    // Chain for valid login: expects 200 and a token in the response
    private static final ChainBuilder validLoginChain = exec(
            http("Valid Login")
                    .post("/api/v1/login")
                    .body(StringBody(validBody)).asJson()
                    .check(status().is(200))
                    .check(jsonPath("$.token").saveAs("jwtToken"))
    ).exec(session -> {
      System.out.println("Valid login token: " + session.getString("jwtToken"));
      return session;
    });

    // Chain for invalid login: expects 401 Unauthorized but force a check for 200 so that the check fails
    private static final ChainBuilder invalidLoginChain = exec(
            http("Invalid Login")
                    .post("/api/v1/login")
                    .body(StringBody(invalidBody)).asJson()
                    // Force the check to expect 200 (which will fail if the server returns 401)
                    .check(status().is(200))
    ).exec(session -> {
      System.out.println("Invalid login response: " + session.getString("responseBody"));
      return session;
    });

    // Chain that randomly chooses which login chain to execute:
    // 10% chance to use valid credentials, 90% chance to use invalid credentials.
    private static final ChainBuilder loginRequest = exec(session -> {
      double rand = ThreadLocalRandom.current().nextDouble();
      if (rand < 0.1) {
        System.out.println("Chosen valid login");
        return session.set("scenarioType", "valid");
      } else {
        System.out.println("Chosen invalid login");
        return session.set("scenarioType", "invalid");
      }
    })
            .doIf(session -> "valid".equals(session.getString("scenarioType")))
            .then(validLoginChain)
            .doIf(session -> "invalid".equals(session.getString("scenarioType")))
            .then(invalidLoginChain);

    // Define the scenario using the loginRequest chain.
    private static final ScenarioBuilder scenario = scenario("Login Simulation")
            .exec(loginRequest);

    // Define assertions:
    // Gatling considers a request successful if all checks pass.
    // In our case, only valid logins (about 10%) should pass, while invalid logins will fail.
    private static final List<Assertion> assertions = List.of(
            global().successfulRequests().percent().is(10.2), // Expect ~10% successes (valid logins)
            global().responseTime().max().lt(2000)              // And max response time under 2 seconds
    );

    {
      setUp(
              scenario.injectOpen(rampUsers(vu).during(30)) // Ramp up to 'vu' users over 30 seconds
      ).assertions(assertions)
              .protocols(httpProtocol);
    }
  }