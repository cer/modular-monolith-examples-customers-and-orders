package net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders;


import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers.Customer;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;

@Entity
@Table(name="orders")
@Access(AccessType.FIELD)
public class Order {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Customer customer;
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

  public Order(Customer customer, Money orderTotal) {
    this.customer = customer;
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
    customer.releaseCredit(id);
    this.state = OrderState.CANCELLED;
  }

  public Customer getCustomer() {
    return customer;
  }

  void reserveCredit() {
    this.customer.reserveCredit(id, orderTotal);
    this.state = OrderState.APPROVED;
  }
}
