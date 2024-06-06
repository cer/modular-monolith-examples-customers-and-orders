package net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public class PropertyProvidingPostgresSQLContainer<SELF extends PropertyProvidingPostgresSQLContainer<SELF>> extends PostgreSQLContainer<SELF> {
  public static final String POSTGRES_USER = "postgresuser";
  public static final String POSTGRES_PASSWORD = "postgresspassword";
  public static final String POSTGRES_DB = "customers_and_orders";

  public PropertyProvidingPostgresSQLContainer() {
    super("postgres:11");
    this.withDatabaseName(POSTGRES_DB)
            .withUsername(POSTGRES_USER)
            .withPassword(POSTGRES_PASSWORD)
            .withExposedPorts(5432)
            .withReuse(true);

  }

  public void startAndRegisterProperties(DynamicPropertyRegistry registry) {
    start();
    registry.add("spring.datasource.url",
            () -> String.format("jdbc:postgresql://localhost:%d/%s", getFirstMappedPort(), POSTGRES_DB));
    registry.add("spring.datasource.username", () -> POSTGRES_USER);
    registry.add("spring.datasource.password", () -> POSTGRES_PASSWORD);
  }
}
