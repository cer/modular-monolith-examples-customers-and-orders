package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;

public class CustomerNotFoundException extends RuntimeException {
  private final long customerId;

  public CustomerNotFoundException(long customerId) {
    this.customerId = customerId;
  }
}
