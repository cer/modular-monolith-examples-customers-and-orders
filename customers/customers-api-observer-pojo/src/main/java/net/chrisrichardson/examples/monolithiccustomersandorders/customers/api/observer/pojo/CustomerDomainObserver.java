package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo;

public interface CustomerDomainObserver {
  void noteCustomerCreated(CustomerInfo customerInfo);
}
