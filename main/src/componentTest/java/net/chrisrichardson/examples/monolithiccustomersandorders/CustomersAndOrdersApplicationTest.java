package net.chrisrichardson.examples.monolithiccustomersandorders;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


@SpringBootTest(classes= CustomersAndOrdersMain.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomersAndOrdersApplicationTest {

  private static final PropertyProvidingPostgresSQLContainer<?> postgres = new PropertyProvidingPostgresSQLContainer<>();

  @DynamicPropertySource
  static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
    postgres.startAndRegisterProperties(registry);
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
  }

  @Test
  public void shouldStart() {

  }

  @Test
  public void shouldCreateCustomerAndOrder() {
    int customerId = given().
            contentType(ContentType.JSON).
            body(Map.of("name", "Fred", "creditLimit", "100")).
            when().
            post("/customers").
            then().
            statusCode(200)
            .extract().path("customerId");

    int orderId = given().
            contentType(ContentType.JSON).
            body(Map.of("customerId", customerId, "orderTotal", "18")).
            when().
            post("/orders").
            then().
            statusCode(200)
            .extract().path("orderId");


  }

  // Move these down
  @Test
  public void shouldCreateCustomer() {
    given().
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
            contentType(ContentType.JSON).
            body(Map.of("name", "Fred")).
            when().
            post("/customers").
            then().
            statusCode(400);

  }


}
