package net.chrisrichardson.examples.monolithiccustomersandorders.domain.notifications;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

  public void sendEmail(String emailAddress, String templateName, Map<String, Object> params) {
    // Do something.
  }
}
