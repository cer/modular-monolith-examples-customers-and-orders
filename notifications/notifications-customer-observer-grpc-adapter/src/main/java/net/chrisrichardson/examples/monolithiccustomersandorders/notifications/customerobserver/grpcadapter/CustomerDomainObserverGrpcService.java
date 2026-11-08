package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.customerobserver.grpcadapter;

import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserverServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.NoteCustomerCreatedRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.NoteCustomerCreatedResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.CustomerEventHandler;

/**
 * An exception thrown by CustomerEventHandler reaches customers as an UNKNOWN status,
 * which rolls back the transaction that published the event.
 */
public class CustomerDomainObserverGrpcService extends CustomerDomainObserverServiceGrpc.CustomerDomainObserverServiceImplBase {

  private final CustomerEventHandler customerEventHandler;

  public CustomerDomainObserverGrpcService(CustomerEventHandler customerEventHandler) {
    this.customerEventHandler = customerEventHandler;
  }

  @Override
  public void noteCustomerCreated(NoteCustomerCreatedRequest request, StreamObserver<NoteCustomerCreatedResponse> responseObserver) {
    customerEventHandler.noteCustomerCreated(request.getCustomerId(), request.getName(), request.getEmailAddress());
    responseObserver.onNext(NoteCustomerCreatedResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
