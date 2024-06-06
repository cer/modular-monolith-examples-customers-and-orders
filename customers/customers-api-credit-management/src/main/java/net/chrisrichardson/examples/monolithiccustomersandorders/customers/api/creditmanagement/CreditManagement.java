package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement;

import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

public interface CreditManagement {
  void reserveCredit(long customerId, long orderId, Money orderTotal);

  void releaseCredit(long customerId, long orderId);

  CustomerInfo getCustomerInfo(long customerId);
}
