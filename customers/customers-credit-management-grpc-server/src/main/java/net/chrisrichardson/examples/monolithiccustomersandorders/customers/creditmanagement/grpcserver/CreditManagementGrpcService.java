package net.chrisrichardson.examples.monolithiccustomersandorders.customers.creditmanagement.grpcserver;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.GetCustomerInfoRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.GetCustomerInfoResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReleaseCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReleaseCreditResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReserveCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReserveCreditResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerCreditLimitExceededException;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerNotFoundException;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;

import java.util.function.Supplier;

public class CreditManagementGrpcService extends CreditManagementServiceGrpc.CreditManagementServiceImplBase {

  private final CreditManagement creditManagement;

  public CreditManagementGrpcService(CreditManagement creditManagement) {
    this.creditManagement = creditManagement;
  }

  @Override
  public void reserveCredit(ReserveCreditRequest request, StreamObserver<ReserveCreditResponse> responseObserver) {
    respond(responseObserver, () -> {
      creditManagement.reserveCredit(request.getCustomerId(), request.getOrderId(), new Money(request.getOrderTotal()));
      return ReserveCreditResponse.getDefaultInstance();
    });
  }

  @Override
  public void releaseCredit(ReleaseCreditRequest request, StreamObserver<ReleaseCreditResponse> responseObserver) {
    respond(responseObserver, () -> {
      creditManagement.releaseCredit(request.getCustomerId(), request.getOrderId());
      return ReleaseCreditResponse.getDefaultInstance();
    });
  }

  @Override
  public void getCustomerInfo(GetCustomerInfoRequest request, StreamObserver<GetCustomerInfoResponse> responseObserver) {
    respond(responseObserver, () -> toResponse(creditManagement.getCustomerInfo(request.getCustomerId())));
  }

  private GetCustomerInfoResponse toResponse(CustomerInfo customerInfo) {
    return GetCustomerInfoResponse.newBuilder()
            .setCustomerId(customerInfo.customerId())
            .setName(customerInfo.name())
            .setEmailAddress(customerInfo.emailAddress())
            .setCreditLimit(customerInfo.creditLimit().asString())
            .setAvailableCredit(customerInfo.availableCredit().asString())
            .build();
  }

  private <T> void respond(StreamObserver<T> responseObserver, Supplier<T> call) {
    T response;
    try {
      response = call.get();
    } catch (RuntimeException e) {
      responseObserver.onError(toStatus(e).withDescription(e.getMessage()).asRuntimeException());
      return;
    }
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private Status toStatus(RuntimeException e) {
    if (e instanceof CustomerNotFoundException)
      return Status.NOT_FOUND;
    if (e instanceof CustomerCreditLimitExceededException)
      return Status.FAILED_PRECONDITION;
    return Status.UNKNOWN.withCause(e);
  }
}
