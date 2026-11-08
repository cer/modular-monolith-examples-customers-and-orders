package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.grpcserver;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.SendEmailRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.pojo.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationGrpcServiceTest {

  private static final SendEmailRequest REQUEST = SendEmailRequest.newBuilder()
          .setEmailAddress("fred@example.com")
          .setTemplateName("OrderConfirmation")
          .putParams("orderId", "42")
          .build();

  private final NotificationService notificationService = mock(NotificationService.class);
  private Server server;
  private ManagedChannel channel;
  private NotificationServiceGrpc.NotificationServiceBlockingStub stub;

  @BeforeEach
  void setUp() throws IOException {
    String name = UUID.randomUUID().toString();
    server = InProcessServerBuilder.forName(name).directExecutor()
            .addService(new NotificationGrpcService(notificationService)).build().start();
    channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    stub = NotificationServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void tearDown() {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @Test
  public void shouldSendEmail() {
    stub.sendEmail(REQUEST);

    verify(notificationService).sendEmail("fred@example.com", "OrderConfirmation", Map.of("orderId", "42"));
  }

  @Test
  public void shouldReturnErrorStatusWhenSendEmailFails() {
    doThrow(new RuntimeException("failed")).when(notificationService)
            .sendEmail("fred@example.com", "OrderConfirmation", Map.of("orderId", "42"));

    StatusRuntimeException e = assertThrows(StatusRuntimeException.class, () -> stub.sendEmail(REQUEST));

    assertEquals(Status.Code.UNKNOWN, e.getStatus().getCode());
  }
}
