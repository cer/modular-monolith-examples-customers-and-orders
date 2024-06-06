package net.chrisrichardson.examples.monolithiccustomersandorders.orders.web;


public class CreateOrderResponse {
  private long orderId;

  public CreateOrderResponse() {
  }

  public CreateOrderResponse(long orderId) {
    this.orderId = orderId;
  }

  public long getOrderId() {
    return orderId;
  }
}
