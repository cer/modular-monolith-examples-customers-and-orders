package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObservers;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerEventPublisher;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class CustomerDomainObserversImpl implements CustomerDomainObservers, CustomerEventPublisher {
  private final List<CustomerDomainObserver> observers = new LinkedList<>();

  @Override
  public void registerObserver(CustomerDomainObserver observer) {
    this.observers.add(observer);
  }

  @Override
  public void noteCustomerCreated(net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerInfo customerInfo) {
    CustomerInfo apiCustomerInfo = new CustomerInfo(customerInfo.customerId(),
            customerInfo.name(),
            customerInfo.emailAddress(),
            customerInfo.creditLimit(),
            customerInfo.availableCredit());
    this.observers.forEach(o -> o.noteCustomerCreated(apiCustomerInfo));
  }
}
