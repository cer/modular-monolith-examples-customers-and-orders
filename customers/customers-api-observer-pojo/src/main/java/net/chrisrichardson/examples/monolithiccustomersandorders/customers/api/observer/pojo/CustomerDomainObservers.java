package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo;

public interface CustomerDomainObservers {

  void registerObserver(CustomerDomainObserver observer);
}
