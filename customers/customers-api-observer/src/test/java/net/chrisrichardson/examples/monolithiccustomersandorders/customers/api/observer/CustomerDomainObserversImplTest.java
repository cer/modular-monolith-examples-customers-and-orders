package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CustomerDomainObserversImplTest {

  @Test
  public void shouldNotifyRegisteredObserverWhenCustomerCreated() {
    CustomerDomainObserver observer = mock(CustomerDomainObserver.class);
    CustomerDomainObserversImpl customerDomainObservers = new CustomerDomainObserversImpl();
    customerDomainObservers.registerObserver(observer);

    Money creditLimit = new Money("12.34");
    Money availableCredit = new Money("10.00");

    customerDomainObservers.noteCustomerCreated(
            new net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerInfo(
                    101L, "Fred", "fred@example.com", creditLimit, availableCredit));

    verify(observer).noteCustomerCreated(new CustomerInfo(101L, "Fred", "fred@example.com", creditLimit, availableCredit));
  }
}
