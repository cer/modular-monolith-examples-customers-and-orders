package net.chrisrichardson.examples.monolithiccustomersandorders.money.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Money {

  public static final Money ZERO = new Money(0);

  @NotNull
  private BigDecimal amount;

  public Money() {
  }

  public Money(int i) {
    this.amount = new BigDecimal(i).setScale(2, RoundingMode.UNNECESSARY);
  }

  @JsonCreator
  public Money(String s) {
    this.amount = new BigDecimal(s).setScale(2, RoundingMode.UNNECESSARY);
  }

  @Override
  public String toString() {
    return "Money{amount=%s}".formatted(amount);
  }

  @JsonValue
  public String asString() {
    return amount.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Money money = (Money) o;
    return Objects.equals(amount, money.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount);
  }

  public Money(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public boolean isGreaterThanOrEqual(Money other) {
    return amount.compareTo(other.amount) >= 0;
  }

  public Money add(Money other) {
    return new Money(amount.add(other.amount));
  }
  public Money subtract(Money other) {
    return new Money(amount.subtract(other.amount));
  }
}