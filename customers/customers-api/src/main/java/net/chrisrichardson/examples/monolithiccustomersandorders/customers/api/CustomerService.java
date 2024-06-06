package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api;

import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

public interface CustomerService {
  CustomerInfo createCustomer(String name, Money creditLimit);

  void reserveCredit(long customerId, long orderId, Money orderTotal);

  void releaseCredit(long customerId, long orderId);

  CustomerInfo getCustomerInfo(long customerId);

}
