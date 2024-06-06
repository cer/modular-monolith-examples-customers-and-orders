package net.chrisrichardson.examples.monolithiccustomersandorders.web.orders;


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
