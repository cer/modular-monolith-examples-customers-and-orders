package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

import java.util.List;
import java.util.Optional;

public interface CustomerService extends CreditManagement {
  CustomerInfo createCustomer(String name, Money creditLimit);

  List<CustomerInfo> findAll();

  Optional<CustomerInfo> findById(long customerId);
}
