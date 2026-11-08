package net.chrisrichardson.examples.monolithiccustomersandorders.orders.creditmanagement.grpcproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.GetCustomerInfoRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReleaseCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReserveCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.CustomerCreditService;

/**
 * Must use a blocking stub: an async or future stub would let the caller continue
 * before the server finishes, outside the caller's transaction.
 * Error statuses reach the caller as StatusRuntimeException, which rolls back its transaction.
 */
public class CreditManagementGrpcProxy implements CustomerCreditService {

  private final CreditManagementServiceGrpc.CreditManagementServiceBlockingStub stub;

  public CreditManagementGrpcProxy(CreditManagementServiceGrpc.CreditManagementServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public void reserveCredit(long customerId, long orderId, Money orderTotal) {
    stub.reserveCredit(ReserveCreditRequest.newBuilder()
            .setCustomerId(customerId)
            .setOrderId(orderId)
            .setOrderTotal(orderTotal.asString())
            .build());
  }

  @Override
  public void releaseCredit(long customerId, long orderId) {
    stub.releaseCredit(ReleaseCreditRequest.newBuilder()
            .setCustomerId(customerId)
            .setOrderId(orderId)
            .build());
  }

  @Override
  public String getCustomerEmailAddress(long customerId) {
    return stub.getCustomerInfo(GetCustomerInfoRequest.newBuilder().setCustomerId(customerId).build()).getEmailAddress();
  }
}
