package net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers;

import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CustomerTest {

  private final Money creditLimit = new Money(100);
  private long orderId = 101L;
  private Customer customer;

  @BeforeEach
  public void setUp() {
    customer = new Customer("Chris", creditLimit);
  }

  @Test
  public void shouldReserveCredit() {
    Money amount = new Money(10);
    customer.reserveCredit(orderId, amount);
    assertEquals(creditLimit.subtract(amount), customer.availableCredit());
  }

  @Test
  public void shouldReserveAllCredit() {
    customer.reserveCredit(orderId, creditLimit);
    assertEquals(Money.ZERO, customer.availableCredit());
  }

  @Test
  public void shouldExceedCreditLimit() {
    assertThrows(CustomerCreditLimitExceededException.class, () -> {
      customer.reserveCredit(orderId, creditLimit.add(new Money("0.01")));
    });
  }

}