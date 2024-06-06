package net.chrisrichardson.examples.monolithiccustomersandorders.web.customers;


import jakarta.validation.constraints.NotNull;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;

public class CreateCustomerRequest {
  @NotNull
  private String name;
  @NotNull
  private Money creditLimit;

  public CreateCustomerRequest() {
  }

  public CreateCustomerRequest(String name, Money creditLimit) {

    this.name = name;
    this.creditLimit = creditLimit;
  }


  public String getName() {
    return name;
  }

  public Money getCreditLimit() {
    return creditLimit;
  }
}
