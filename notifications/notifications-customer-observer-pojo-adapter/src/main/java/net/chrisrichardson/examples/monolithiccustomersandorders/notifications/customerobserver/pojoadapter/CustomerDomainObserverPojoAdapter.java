package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.customerobserver.pojoadapter;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.CustomerEventHandler;

public class CustomerDomainObserverPojoAdapter implements CustomerDomainObserver {

  private final CustomerEventHandler customerEventHandler;

  public CustomerDomainObserverPojoAdapter(CustomerEventHandler customerEventHandler) {
    this.customerEventHandler = customerEventHandler;
  }

  @Override
  public void noteCustomerCreated(CustomerInfo customerInfo) {
    customerEventHandler.noteCustomerCreated(customerInfo.customerId(), customerInfo.name(), customerInfo.emailAddress());
  }
}
