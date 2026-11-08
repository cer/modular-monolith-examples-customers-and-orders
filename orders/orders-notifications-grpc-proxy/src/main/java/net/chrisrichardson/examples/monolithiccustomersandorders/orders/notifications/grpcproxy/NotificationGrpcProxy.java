package net.chrisrichardson.examples.monolithiccustomersandorders.orders.notifications.grpcproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.SendEmailRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.NotificationSender;

import java.util.Map;

/**
 * Must use a blocking stub, so that the email is sent inside the caller's transaction.
 * The template parameters are sent as strings, using String.valueOf().
 * Error statuses reach the caller as StatusRuntimeException, which rolls back its transaction.
 */
public class NotificationGrpcProxy implements NotificationSender {

  private final NotificationServiceGrpc.NotificationServiceBlockingStub stub;

  public NotificationGrpcProxy(NotificationServiceGrpc.NotificationServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public void sendEmail(String emailAddress, String templateName, Map<String, Object> params) {
    SendEmailRequest.Builder request = SendEmailRequest.newBuilder()
            .setEmailAddress(emailAddress)
            .setTemplateName(templateName);
    params.forEach((name, value) -> request.putParams(name, String.valueOf(value)));
    stub.sendEmail(request.build());
  }
}
