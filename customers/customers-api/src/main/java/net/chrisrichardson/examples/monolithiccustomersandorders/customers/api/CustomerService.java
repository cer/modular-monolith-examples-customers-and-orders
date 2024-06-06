package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

public interface CustomerService extends CreditManagement {
  CustomerInfo createCustomer(String name, Money creditLimit);

}
