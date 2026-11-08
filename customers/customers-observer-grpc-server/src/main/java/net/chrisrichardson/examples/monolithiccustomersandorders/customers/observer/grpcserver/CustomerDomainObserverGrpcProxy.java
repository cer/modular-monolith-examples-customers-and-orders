package net.chrisrichardson.examples.monolithiccustomersandorders.customers.observer.grpcserver;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserverServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.NoteCustomerCreatedRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerInfo;

/**
 * Must use a blocking stub, so that the observer finishes inside the publisher's transaction.
 * An error status reaches the publisher as StatusRuntimeException, which rolls back its transaction.
 */
public class CustomerDomainObserverGrpcProxy implements CustomerDomainObserver {

  private final CustomerDomainObserverServiceGrpc.CustomerDomainObserverServiceBlockingStub stub;

  public CustomerDomainObserverGrpcProxy(CustomerDomainObserverServiceGrpc.CustomerDomainObserverServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public void noteCustomerCreated(CustomerInfo customerInfo) {
    stub.noteCustomerCreated(NoteCustomerCreatedRequest.newBuilder()
            .setCustomerId(customerInfo.customerId())
            .setName(customerInfo.name())
            .setEmailAddress(customerInfo.emailAddress())
            .setCreditLimit(customerInfo.creditLimit().asString())
            .setAvailableCredit(customerInfo.availableCredit().asString())
            .build());
  }
}
