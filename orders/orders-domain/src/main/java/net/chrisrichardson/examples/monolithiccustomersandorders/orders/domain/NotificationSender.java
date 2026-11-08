package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;

import java.util.Map;

/**
 * What the orders module needs from the notifications module.
 * Implemented by orders-notifications-pojo-proxy (default) and orders-notifications-grpc-proxy (profile grpc).
 */
public interface NotificationSender {

  void sendEmail(String emailAddress, String templateName, Map<String, Object> params);
}
