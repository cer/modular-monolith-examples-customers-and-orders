package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;


import jakarta.persistence.*;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

@Entity
@Table(name="orders")
@Access(AccessType.FIELD)
public class Order {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // TODO - what about the FK constraint (which would make testing more difficult BTW)
  private long customerId;
  @Embedded
  private Money orderTotal;

  @Enumerated(EnumType.STRING)
  private OrderState state;


  @Enumerated(EnumType.STRING)
  private RejectionReason rejectionReason;

  @Version
  private Long version;

  public Order() {
  }

  public Order(long customerId, Money orderTotal) {
    this.customerId = customerId;
    this.orderTotal = orderTotal;
    this.state = OrderState.PENDING;
  }


  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void reject(RejectionReason rejectionReason) {
    this.state = OrderState.REJECTED;
    this.rejectionReason = rejectionReason;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }
  public OrderState getState() {
    return state;
  }

  public RejectionReason getRejectionReason() {
    return rejectionReason;
  }

  public void cancel() {
    this.state = OrderState.CANCELLED;
  }

  public long getCustomerId() {
    return customerId;
  }

  void noteApproved() {
    this.state = OrderState.APPROVED;
  }
}
