package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService, CustomerEventHandler {

  public void sendEmail(String emailAddress, String templateName, Map<String, Object> params) {
    // Do something.
  }

  @Override
  public void noteCustomerCreated(long customerId, String name, String emailAddress) {
    sendEmail(emailAddress, "customer-created", Map.of("customerId", customerId, "name", name));
  }
}
