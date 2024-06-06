package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain;

import jakarta.annotation.PostConstruct;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerDomainObservers;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService, CustomerDomainObserver {

  private final CustomerDomainObservers customerDomainObservers;

  @Autowired
  public NotificationServiceImpl(CustomerDomainObservers customerDomainObservers) {
    this.customerDomainObservers = customerDomainObservers;
  }

  @PostConstruct
  public void registerCustomerDomainObserver() {
    customerDomainObservers.registerObserver(this);
  }

  public void sendEmail(String emailAddress, String templateName, Map<String, Object> params) {
    // Do something.
  }

  @Override
  public void noteCustomerCreated(CustomerInfo customerInfo) {
    sendEmail(customerInfo.emailAddress(), "customer-created", Map.of("customerId", customerInfo.customerId(), "name", customerInfo.name()));
  }
}
