package net.chrisrichardson.examples.monolithiccustomersandorders.orders.notifications.pojoproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.pojo.NotificationService;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.NotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!grpc")
public class NotificationPojoProxyConfiguration {

  @Bean
  public NotificationSender pojoNotificationSender(NotificationService notificationService) {
    return new NotificationPojoProxy(notificationService);
  }
}
