package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api;

import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

public record CustomerInfo(long customerId, String name, String emailAddress, Money creditLimit) {
}
