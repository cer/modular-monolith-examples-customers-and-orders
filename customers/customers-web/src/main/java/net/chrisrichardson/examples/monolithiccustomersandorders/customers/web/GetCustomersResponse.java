package net.chrisrichardson.examples.monolithiccustomersandorders.customers.web;

import java.util.List;

public class GetCustomersResponse {

  private List<GetCustomerResponse> customers;


  public GetCustomersResponse() {
  }

  public GetCustomersResponse(List<GetCustomerResponse> customers) {
    this.customers = customers;
  }

  public List<GetCustomerResponse> getCustomers() {
    return customers;
  }

  public void setCustomers(List<GetCustomerResponse> customers) {
    this.customers = customers;
  }
}
