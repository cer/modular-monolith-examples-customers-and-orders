package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.grpcserver;

import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.SendEmailRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.SendEmailResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.pojo.NotificationService;

import java.util.HashMap;

/**
 * The template parameters arrive as strings.
 * An exception thrown by NotificationService reaches the caller as an UNKNOWN status, which rolls back its transaction.
 */
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

  private final NotificationService notificationService;

  public NotificationGrpcService(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Override
  public void sendEmail(SendEmailRequest request, StreamObserver<SendEmailResponse> responseObserver) {
    notificationService.sendEmail(request.getEmailAddress(), request.getTemplateName(), new HashMap<>(request.getParamsMap()));
    responseObserver.onNext(SendEmailResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
