package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer;

public interface CustomerDomainObserver {
  void noteCustomerCreated(CustomerInfo customerInfo);
}
