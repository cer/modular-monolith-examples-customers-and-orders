package net.chrisrichardson.examples.monolithiccustomersandorders;

import io.eventuate.common.testcontainers.EventuateVanillaPostgresContainer;
import io.eventuate.common.testcontainers.ReusableNetworkFactory;
import io.eventuate.testcontainers.service.ServiceContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CustomersAndOrdersContainerizedApplicationTest {

    private static final Network network = ReusableNetworkFactory.createNetwork("testNetwork");
    private static final EventuateVanillaPostgresContainer postgres =
            new EventuateVanillaPostgresContainer().
                    withNetworkAliases("postgres").
                    withNetwork(network);

    public static ServiceContainer service =
            ServiceContainer.makeFromDockerfileOnClasspath()
                    .withDatabase(postgres)
                    .withNetwork(network)
                    .dependsOn(postgres)
                    .withReuse(false) // should rebuild
            ;


    @BeforeAll
    public static void startContainers() {
        Startables.deepStart(service).join();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = service.getFirstMappedPort();
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
}
