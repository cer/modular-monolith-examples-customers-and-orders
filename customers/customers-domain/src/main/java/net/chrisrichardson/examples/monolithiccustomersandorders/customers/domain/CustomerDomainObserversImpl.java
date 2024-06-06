package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerDomainObservers;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerInfo;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class CustomerDomainObserversImpl implements CustomerDomainObservers {
  private final List<CustomerDomainObserver> observers = new LinkedList<>();

  @Override
  public void registerObserver(CustomerDomainObserver observer) {
    this.observers.add(observer);
  }

  void noteCustomerCreated(CustomerInfo customerInfo) {
    this.observers.forEach(o -> o.noteCustomerCreated(customerInfo));
  }
}
