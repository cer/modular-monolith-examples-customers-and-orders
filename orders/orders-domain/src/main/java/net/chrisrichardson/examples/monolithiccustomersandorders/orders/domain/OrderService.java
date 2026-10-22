package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;

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
  private final CustomerCreditService customerCreditService;

    @Autowired
  public OrderService(OrderRepository orderRepository, NotificationService notificationService, CustomerCreditService customerCreditService) {
    this.orderRepository = orderRepository;
    this.notificationService = notificationService;
    this.customerCreditService = customerCreditService;
   }

  @Transactional
  public Order createOrder(long customerId, Money orderTotal) {
      Order order = new Order(customerId, orderTotal);
      order = orderRepository.save(order);
      customerCreditService.reserveCredit(customerId, order.getId(), orderTotal);
      order.noteApproved();
      var emailAddress = customerCreditService.getCustomerEmailAddress(customerId);
      notificationService.sendEmail(emailAddress, "OrderConfirmation", Map.of("orderId", order.getId()));
      return order;
  }

  public void cancelOrder(long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    order.cancel();
    customerCreditService.releaseCredit(order.getCustomerId(), orderId);
  }

}
