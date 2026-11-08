package net.chrisrichardson.examples.monolithiccustomersandorders.orders.notifications.grpcproxy;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.SendEmailRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.SendEmailResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NotificationGrpcProxyTest {

  private final FakeNotificationService service = new FakeNotificationService();
  private Server server;
  private ManagedChannel channel;
  private NotificationGrpcProxy proxy;

  @BeforeEach
  void setUp() throws IOException {
    String name = UUID.randomUUID().toString();
    server = InProcessServerBuilder.forName(name).directExecutor().addService(service).build().start();
    channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    proxy = new NotificationGrpcProxy(NotificationServiceGrpc.newBlockingStub(channel));
  }

  @AfterEach
  void tearDown() {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @Test
  public void shouldSendEmailWithParamsAsStrings() {
    proxy.sendEmail("fred@example.com", "OrderConfirmation", Map.of("orderId", 42L));

    assertEquals(SendEmailRequest.newBuilder()
                    .setEmailAddress("fred@example.com")
                    .setTemplateName("OrderConfirmation")
                    .putParams("orderId", "42")
                    .build(),
            service.request);
  }

  @Test
  public void shouldPropagateErrorStatus() {
    service.error = Status.UNKNOWN;

    StatusRuntimeException e = assertThrows(StatusRuntimeException.class,
            () -> proxy.sendEmail("fred@example.com", "OrderConfirmation", Map.of("orderId", 42L)));

    assertEquals(Status.Code.UNKNOWN, e.getStatus().getCode());
  }

  static class FakeNotificationService extends NotificationServiceGrpc.NotificationServiceImplBase {
    SendEmailRequest request;
    Status error;

    @Override
    public void sendEmail(SendEmailRequest request, StreamObserver<SendEmailResponse> responseObserver) {
      this.request = request;
      if (error != null) {
        responseObserver.onError(error.asRuntimeException());
        return;
      }
      responseObserver.onNext(SendEmailResponse.getDefaultInstance());
      responseObserver.onCompleted();
    }
  }
}
