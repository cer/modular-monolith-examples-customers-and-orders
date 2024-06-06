package net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers;

public class CustomerNotFoundException extends RuntimeException {
  private final long customerId;

  public CustomerNotFoundException(long customerId) {
    this.customerId = customerId;
  }
}
