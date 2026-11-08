package net.chrisrichardson.examples.monolithiccustomersandorders.orders.notifications.grpcproxy;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationsInProcessServer;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.NotificationSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("grpc")
public class NotificationGrpcProxyConfiguration {

  @Bean(destroyMethod = "shutdownNow")
  public ManagedChannel notificationsChannel() {
    return InProcessChannelBuilder.forName(NotificationsInProcessServer.NAME).directExecutor().build();
  }

  @Bean
  public NotificationSender grpcNotificationSender(@Qualifier("notificationsChannel") ManagedChannel notificationsChannel) {
    return new NotificationGrpcProxy(NotificationServiceGrpc.newBlockingStub(notificationsChannel));
  }
}
