package net.chrisrichardson.examples.monolithiccustomersandorders.orders.notifications.pojoproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.pojo.NotificationService;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.NotificationSender;

import java.util.Map;

public class NotificationPojoProxy implements NotificationSender {

  private final NotificationService notificationService;

  public NotificationPojoProxy(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Override
  public void sendEmail(String emailAddress, String templateName, Map<String, Object> params) {
    notificationService.sendEmail(emailAddress, templateName, params);
  }
}
