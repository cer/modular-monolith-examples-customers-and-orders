package net.chrisrichardson.examples.monolithiccustomersandorders.orders.creditmanagement.javaproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.CustomerCreditService;

public class CreditManagementJavaProxy implements CustomerCreditService {

  private final CreditManagement creditManagement;

  public CreditManagementJavaProxy(CreditManagement creditManagement) {
    this.creditManagement = creditManagement;
  }

  @Override
  public void reserveCredit(long customerId, long orderId, Money orderTotal) {
    creditManagement.reserveCredit(customerId, orderId, orderTotal);
  }

  @Override
  public void releaseCredit(long customerId, long orderId) {
    creditManagement.releaseCredit(customerId, orderId);
  }

  @Override
  public String getCustomerEmailAddress(long customerId) {
    return creditManagement.getCustomerInfo(customerId).emailAddress();
  }
}
