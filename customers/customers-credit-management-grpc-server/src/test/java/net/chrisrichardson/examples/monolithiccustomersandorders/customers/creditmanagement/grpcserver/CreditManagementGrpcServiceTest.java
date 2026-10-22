package net.chrisrichardson.examples.monolithiccustomersandorders.customers.creditmanagement.grpcserver;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.GetCustomerInfoRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReleaseCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.ReserveCreditRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerCreditLimitExceededException;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerNotFoundException;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
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
import static org.mockito.Mockito.when;

public class CreditManagementGrpcServiceTest {

  private static final long CUSTOMER_ID = 101;
  private static final long ORDER_ID = 202;

  private final CreditManagement creditManagement = mock(CreditManagement.class);
  private Server server;
  private ManagedChannel channel;
  private CreditManagementServiceGrpc.CreditManagementServiceBlockingStub stub;

  @BeforeEach
  void setUp() throws IOException {
    String name = UUID.randomUUID().toString();
    server = InProcessServerBuilder.forName(name).directExecutor()
            .addService(new CreditManagementGrpcService(creditManagement)).build().start();
    channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    stub = CreditManagementServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void tearDown() {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @Test
  public void shouldReserveCredit() {
    stub.reserveCredit(reserveCreditRequest());

    verify(creditManagement).reserveCredit(CUSTOMER_ID, ORDER_ID, new Money("12.34"));
  }

  @Test
  public void shouldReturnNotFoundForUnknownCustomer() {
    doThrow(new CustomerNotFoundException(CUSTOMER_ID)).when(creditManagement).reserveCredit(CUSTOMER_ID, ORDER_ID, new Money("12.34"));

    assertStatus(Status.Code.NOT_FOUND, () -> stub.reserveCredit(reserveCreditRequest()));
  }

  @Test
  public void shouldReturnFailedPreconditionWhenCreditLimitExceeded() {
    doThrow(new CustomerCreditLimitExceededException()).when(creditManagement).reserveCredit(CUSTOMER_ID, ORDER_ID, new Money("12.34"));

    assertStatus(Status.Code.FAILED_PRECONDITION, () -> stub.reserveCredit(reserveCreditRequest()));
  }

  @Test
  public void shouldReleaseCredit() {
    stub.releaseCredit(ReleaseCreditRequest.newBuilder().setCustomerId(CUSTOMER_ID).setOrderId(ORDER_ID).build());

    verify(creditManagement).releaseCredit(CUSTOMER_ID, ORDER_ID);
  }

  @Test
  public void shouldGetCustomerInfo() {
    when(creditManagement.getCustomerInfo(CUSTOMER_ID))
            .thenReturn(new CustomerInfo(CUSTOMER_ID, "Fred", "fred@example.com", new Money("100"), new Money("87.66")));

    var response = stub.getCustomerInfo(GetCustomerInfoRequest.newBuilder().setCustomerId(CUSTOMER_ID).build());

    assertEquals(CUSTOMER_ID, response.getCustomerId());
    assertEquals("Fred", response.getName());
    assertEquals("fred@example.com", response.getEmailAddress());
    assertEquals("100.00", response.getCreditLimit());
    assertEquals("87.66", response.getAvailableCredit());
  }

  @Test
  public void shouldReturnNotFoundWhenGettingUnknownCustomer() {
    when(creditManagement.getCustomerInfo(CUSTOMER_ID)).thenThrow(new CustomerNotFoundException(CUSTOMER_ID));

    assertStatus(Status.Code.NOT_FOUND,
            () -> stub.getCustomerInfo(GetCustomerInfoRequest.newBuilder().setCustomerId(CUSTOMER_ID).build()));
  }

  private ReserveCreditRequest reserveCreditRequest() {
    return ReserveCreditRequest.newBuilder().setCustomerId(CUSTOMER_ID).setOrderId(ORDER_ID).setOrderTotal("12.34").build();
  }

  private void assertStatus(Status.Code expected, Runnable call) {
    StatusRuntimeException e = assertThrows(StatusRuntimeException.class, call::run);
    assertEquals(expected, e.getStatus().getCode());
  }
}
