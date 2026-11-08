package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.customerobserver.grpcadapter;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserverServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.NoteCustomerCreatedRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.CustomerEventHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CustomerDomainObserverGrpcServiceTest {

  private static final NoteCustomerCreatedRequest REQUEST = NoteCustomerCreatedRequest.newBuilder()
          .setCustomerId(101)
          .setName("Fred")
          .setEmailAddress("fred@example.com")
          .setCreditLimit("100.00")
          .setAvailableCredit("87.66")
          .build();

  private final CustomerEventHandler customerEventHandler = mock(CustomerEventHandler.class);
  private Server server;
  private ManagedChannel channel;
  private CustomerDomainObserverServiceGrpc.CustomerDomainObserverServiceBlockingStub stub;

  @BeforeEach
  void setUp() throws IOException {
    String name = UUID.randomUUID().toString();
    server = InProcessServerBuilder.forName(name).directExecutor()
            .addService(new CustomerDomainObserverGrpcService(customerEventHandler)).build().start();
    channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    stub = CustomerDomainObserverServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void tearDown() {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @Test
  public void shouldCallCustomerEventHandler() {
    stub.noteCustomerCreated(REQUEST);

    verify(customerEventHandler).noteCustomerCreated(101, "Fred", "fred@example.com");
  }

  @Test
  public void shouldReturnErrorStatusWhenCustomerEventHandlerFails() {
    doThrow(new RuntimeException("failed")).when(customerEventHandler).noteCustomerCreated(101, "Fred", "fred@example.com");

    StatusRuntimeException e = assertThrows(StatusRuntimeException.class, () -> stub.noteCustomerCreated(REQUEST));

    assertEquals(Status.Code.UNKNOWN, e.getStatus().getCode());
  }
}
