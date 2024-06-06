package net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;

@Embeddable
public class OrderDetails {

  private Long customerId;

  @Embedded
  private Money orderTotal;

  public OrderDetails() {
  }

  public OrderDetails(Long customerId, Money orderTotal) {
    this.customerId = customerId;
    this.orderTotal = orderTotal;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }
}
