package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;

public interface CustomerEventPublisher {
  void noteCustomerCreated(CustomerInfo customerInfo);
}
