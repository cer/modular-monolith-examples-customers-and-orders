package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final NotificationService notificationService;
  private final CreditManagement creditManagement;

    @Autowired
  public OrderService(OrderRepository orderRepository, NotificationService notificationService, CreditManagement creditManagement) {
    this.orderRepository = orderRepository;
    this.notificationService = notificationService;
    this.creditManagement = creditManagement;
   }

  @Transactional
  public Order createOrder(long customerId, Money orderTotal) {
      Order order = new Order(customerId, orderTotal);
      order = orderRepository.save(order);
      creditManagement.reserveCredit(customerId, order.getId(), orderTotal);
      order.noteApproved();
      var customer = creditManagement.getCustomerInfo(customerId);
      notificationService.sendEmail(customer.emailAddress(), "OrderConfirmation", Map.of("orderId", order.getId()));
      return order;
  }

  public void cancelOrder(long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    order.cancel();
    creditManagement.releaseCredit(order.getCustomerId(), orderId);
  }

}
