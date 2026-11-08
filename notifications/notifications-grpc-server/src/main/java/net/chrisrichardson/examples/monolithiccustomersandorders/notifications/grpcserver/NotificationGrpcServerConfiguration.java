package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.grpcserver;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.grpc.NotificationsInProcessServer;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.pojo.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("grpc")
public class NotificationGrpcServerConfiguration {

  @Bean
  public NotificationGrpcService notificationGrpcService(NotificationService notificationService) {
    return new NotificationGrpcService(notificationService);
  }

  // directExecutor() runs each call on the caller's thread, so it joins the caller's transaction
  @Bean(destroyMethod = "shutdownNow")
  public Server notificationGrpcServer(NotificationGrpcService notificationGrpcService) throws IOException {
    return InProcessServerBuilder.forName(NotificationsInProcessServer.NAME)
            .directExecutor()
            .addService(notificationGrpcService)
            .build()
            .start();
  }
}
