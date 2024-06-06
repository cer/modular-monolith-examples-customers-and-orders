package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

public record CustomerInfo(long id, String name, String emailAddress, Money creditLimit) {
}
