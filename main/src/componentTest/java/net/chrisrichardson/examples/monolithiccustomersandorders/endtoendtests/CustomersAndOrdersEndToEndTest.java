package net.chrisrichardson.examples.monolithiccustomersandorders.endtoendtests;

import io.restassured.http.ContentType;
import net.chrisrichardson.examples.monolithiccustomersandorders.main.CustomersAndOrdersConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


@SpringBootTest(classes=CustomersAndOrdersEndToEndTest.Config.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomersAndOrdersEndToEndTest {

  @Configuration
  @Import(CustomersAndOrdersConfiguration.class)
  @EnableAutoConfiguration
  public static class Config {
  }

  private static PropertyProvidingPostgresSQLContainer<?> postgres = new PropertyProvidingPostgresSQLContainer<>();

  @DynamicPropertySource
  static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
    postgres.startAndRegisterProperties(registry);
  }

  @LocalServerPort
  private int port;

  @Test
  public void shouldStart() {

  }

  @Test
  public void shouldCreateCustomer() {
    given().
            port(port).
            contentType(ContentType.JSON).
            body(Map.of("name", "Fred", "creditLimit", "100")).
            when().
            post("/customers").
            then().
            statusCode(200)
            .body("customerId", greaterThanOrEqualTo(0));

  }
  @Test
  public void shouldFailToCreateCustomer() {
    given().
            port(port).
            contentType(ContentType.JSON).
            body(Map.of("name", "Fred")).
            when().
            post("/customers").
            then().
            statusCode(400);

  }

}
