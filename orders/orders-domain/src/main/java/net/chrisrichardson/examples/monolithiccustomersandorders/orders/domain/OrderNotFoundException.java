package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(long orderId) {
        super("Order not found: " + orderId);
    }
}
