package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer;

public interface CustomerDomainObservers {

  void registerObserver(CustomerDomainObserver observer);
}
