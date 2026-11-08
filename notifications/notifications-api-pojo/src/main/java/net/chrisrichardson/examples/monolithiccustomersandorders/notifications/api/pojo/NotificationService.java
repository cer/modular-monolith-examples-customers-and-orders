package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.pojo;

import java.util.Map;

public interface NotificationService {
  void sendEmail(String emailAddress, String templateName, Map<String, Object> params);
}

