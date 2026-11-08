package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

/**
 * What the orders module needs from the customers module.
 * Implemented by orders-credit-management-pojo-proxy (default) and orders-credit-management-grpc-proxy (profile grpc).
 */
public interface CustomerCreditService {

  void reserveCredit(long customerId, long orderId, Money orderTotal);

  void releaseCredit(long customerId, long orderId);

  String getCustomerEmailAddress(long customerId);
}
