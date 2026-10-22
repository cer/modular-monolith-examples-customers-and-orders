package net.chrisrichardson.examples.monolithiccustomersandorders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the CustomersAndOrdersApplicationTest tests with orders calling customers over in-process gRPC.
 */
@ActiveProfiles("grpc")
public class CustomersAndOrdersGrpcApplicationTest extends CustomersAndOrdersApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  public void shouldUseGrpcProxy() {
    assertTrue(applicationContext.containsBean("grpcCustomerCreditService"));
    assertFalse(applicationContext.containsBean("javaCustomerCreditService"));
  }
}
