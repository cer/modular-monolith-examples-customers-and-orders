package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.customerobserver.pojoadapter;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObservers;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.CustomerEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerDomainObserverPojoAdapterConfiguration {

  @Bean
  public CustomerDomainObserverPojoAdapter customerDomainObserverPojoAdapter(CustomerDomainObservers customerDomainObservers,
                                                                            CustomerEventHandler customerEventHandler) {
    CustomerDomainObserverPojoAdapter adapter = new CustomerDomainObserverPojoAdapter(customerEventHandler);
    customerDomainObservers.registerObserver(adapter);
    return adapter;
  }
}
