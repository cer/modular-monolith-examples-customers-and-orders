package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement;

import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

public record CustomerInfo(long customerId, String name, String emailAddress, Money creditLimit, Money availableCredit) {
}
