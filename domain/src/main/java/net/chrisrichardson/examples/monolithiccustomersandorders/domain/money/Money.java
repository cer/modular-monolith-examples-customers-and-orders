package net.chrisrichardson.examples.monolithiccustomersandorders.domain.money;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
  public Money(String s) {
    this.amount = new BigDecimal(s).setScale(2, RoundingMode.UNNECESSARY);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
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