package net.chrisrichardson.examples.monolithiccustomersandorders.orders.web;

import java.util.List;

public class GetOrdersResponse {

  private List<GetOrderResponse> orders;

  public GetOrdersResponse() {
  }

  public GetOrdersResponse(List<GetOrderResponse> orders) {
    this.orders = orders;
  }

  public List<GetOrderResponse> getOrders() {
    return orders;
  }

  public void setOrders(List<GetOrderResponse> orders) {
    this.orders = orders;
  }
}
