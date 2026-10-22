package net.chrisrichardson.examples.monolithiccustomersandorders.orders.creditmanagement.grpcproxy;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.GetCustomerInfoRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.GetCustomerInfoResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReleaseCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReleaseCreditResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReserveCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReserveCreditResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreditManagementGrpcProxyTest {

  private static final long CUSTOMER_ID = 101;
  private static final long ORDER_ID = 202;

  private final FakeCreditManagementService service = new FakeCreditManagementService();
  private Server server;
  private ManagedChannel channel;
  private CreditManagementGrpcProxy proxy;

  @BeforeEach
  void setUp() throws IOException {
    String name = UUID.randomUUID().toString();
    server = InProcessServerBuilder.forName(name).directExecutor().addService(service).build().start();
    channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    proxy = new CreditManagementGrpcProxy(CreditManagementServiceGrpc.newBlockingStub(channel));
  }

  @AfterEach
  void tearDown() {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @Test
  public void shouldReserveCredit() {
    proxy.reserveCredit(CUSTOMER_ID, ORDER_ID, new Money("12.34"));

    assertEquals(ReserveCreditRequest.newBuilder().setCustomerId(CUSTOMER_ID).setOrderId(ORDER_ID).setOrderTotal("12.34").build(),
            service.reserveCreditRequest);
  }

  @Test
  public void shouldPropagateErrorStatus() {
    service.error = Status.FAILED_PRECONDITION;

    StatusRuntimeException e = assertThrows(StatusRuntimeException.class,
            () -> proxy.reserveCredit(CUSTOMER_ID, ORDER_ID, new Money("12.34")));

    assertEquals(Status.Code.FAILED_PRECONDITION, e.getStatus().getCode());
  }

  @Test
  public void shouldReleaseCredit() {
    proxy.releaseCredit(CUSTOMER_ID, ORDER_ID);

    assertEquals(ReleaseCreditRequest.newBuilder().setCustomerId(CUSTOMER_ID).setOrderId(ORDER_ID).build(),
            service.releaseCreditRequest);
  }

  @Test
  public void shouldGetCustomerEmailAddress() {
    assertEquals("fred@example.com", proxy.getCustomerEmailAddress(CUSTOMER_ID));
  }

  static class FakeCreditManagementService extends CreditManagementServiceGrpc.CreditManagementServiceImplBase {
    ReserveCreditRequest reserveCreditRequest;
    ReleaseCreditRequest releaseCreditRequest;
    Status error;

    @Override
    public void reserveCredit(ReserveCreditRequest request, StreamObserver<ReserveCreditResponse> responseObserver) {
      reserveCreditRequest = request;
      if (error != null) {
        responseObserver.onError(error.asRuntimeException());
        return;
      }
      responseObserver.onNext(ReserveCreditResponse.getDefaultInstance());
      responseObserver.onCompleted();
    }

    @Override
    public void releaseCredit(ReleaseCreditRequest request, StreamObserver<ReleaseCreditResponse> responseObserver) {
      releaseCreditRequest = request;
      responseObserver.onNext(ReleaseCreditResponse.getDefaultInstance());
      responseObserver.onCompleted();
    }

    @Override
    public void getCustomerInfo(GetCustomerInfoRequest request, StreamObserver<GetCustomerInfoResponse> responseObserver) {
      responseObserver.onNext(GetCustomerInfoResponse.newBuilder()
              .setCustomerId(request.getCustomerId()).setEmailAddress("fred@example.com").build());
      responseObserver.onCompleted();
    }
  }
}
