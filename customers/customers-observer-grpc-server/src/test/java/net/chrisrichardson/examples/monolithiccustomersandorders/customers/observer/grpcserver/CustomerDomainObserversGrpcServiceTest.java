package net.chrisrichardson.examples.monolithiccustomersandorders.customers.observer.grpcserver;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserverServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserversServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.NoteCustomerCreatedRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.NoteCustomerCreatedResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.RegisterObserverRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObservers;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CustomerDomainObserversGrpcServiceTest {

  private static final CustomerInfo CUSTOMER_INFO =
          new CustomerInfo(101, "Fred", "fred@example.com", new Money("100"), new Money("87.66"));

  private final CustomerDomainObservers customerDomainObservers = mock(CustomerDomainObservers.class);
  private final FakeCustomerDomainObserverService observerService = new FakeCustomerDomainObserverService();
  private final String observerServerName = UUID.randomUUID().toString();
  private CustomerDomainObserversGrpcService service;
  private Server server;
  private Server observerServer;
  private ManagedChannel channel;
  private CustomerDomainObserversServiceGrpc.CustomerDomainObserversServiceBlockingStub stub;

  @BeforeEach
  void setUp() throws IOException {
    String name = UUID.randomUUID().toString();
    service = new CustomerDomainObserversGrpcService(customerDomainObservers);
    server = InProcessServerBuilder.forName(name).directExecutor().addService(service).build().start();
    observerServer = InProcessServerBuilder.forName(observerServerName).directExecutor().addService(observerService).build().start();
    channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    stub = CustomerDomainObserversServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void tearDown() {
    channel.shutdownNow();
    server.shutdownNow();
    observerServer.shutdownNow();
    service.shutdown();
  }

  @Test
  public void shouldRegisterObserverThatCallsObserverServer() {
    registeredObserver().noteCustomerCreated(CUSTOMER_INFO);

    assertEquals(NoteCustomerCreatedRequest.newBuilder()
                    .setCustomerId(101)
                    .setName("Fred")
                    .setEmailAddress("fred@example.com")
                    .setCreditLimit("100.00")
                    .setAvailableCredit("87.66")
                    .build(),
            observerService.request);
  }

  @Test
  public void shouldPropagateObserverErrorStatus() {
    observerService.error = Status.FAILED_PRECONDITION;
    CustomerDomainObserver observer = registeredObserver();

    StatusRuntimeException e = assertThrows(StatusRuntimeException.class, () -> observer.noteCustomerCreated(CUSTOMER_INFO));

    assertEquals(Status.Code.FAILED_PRECONDITION, e.getStatus().getCode());
  }

  private CustomerDomainObserver registeredObserver() {
    stub.registerObserver(RegisterObserverRequest.newBuilder().setInProcessServerName(observerServerName).build());
    ArgumentCaptor<CustomerDomainObserver> observer = ArgumentCaptor.forClass(CustomerDomainObserver.class);
    verify(customerDomainObservers).registerObserver(observer.capture());
    return observer.getValue();
  }

  static class FakeCustomerDomainObserverService extends CustomerDomainObserverServiceGrpc.CustomerDomainObserverServiceImplBase {
    NoteCustomerCreatedRequest request;
    Status error;

    @Override
    public void noteCustomerCreated(NoteCustomerCreatedRequest request, StreamObserver<NoteCustomerCreatedResponse> responseObserver) {
      this.request = request;
      if (error != null) {
        responseObserver.onError(error.asRuntimeException());
        return;
      }
      responseObserver.onNext(NoteCustomerCreatedResponse.getDefaultInstance());
      responseObserver.onCompleted();
    }
  }
}
