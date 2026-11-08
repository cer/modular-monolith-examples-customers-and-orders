package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain;

/**
 * What the notifications module does when the customers module publishes an event.
 * Called by notifications-customer-observer-pojo-adapter.
 */
public interface CustomerEventHandler {

  void noteCustomerCreated(long customerId, String name, String emailAddress);
}
