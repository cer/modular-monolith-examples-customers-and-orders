package net.chrisrichardson.examples.monolithiccustomersandorders.web.customers;


public class CreateCustomerResponse {
  private Long customerId;

  public CreateCustomerResponse() {
  }

  public CreateCustomerResponse(Long customerId) {
    this.customerId = customerId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }
}
