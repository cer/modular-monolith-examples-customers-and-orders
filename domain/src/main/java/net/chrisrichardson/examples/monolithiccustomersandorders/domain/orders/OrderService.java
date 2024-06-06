package net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders;

import net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers.CustomerNotFoundException;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers.CustomerRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.notifications.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final NotificationService notificationService;

  @Autowired
  public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, NotificationService notificationService) {
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.notificationService = notificationService;
  }

  @Transactional
  public Order createOrder(long customerId, Money orderTotal) {
    return customerRepository.findById(customerId)
            .map(customer -> {
              Order order = new Order(customer, orderTotal);
              order = orderRepository.save(order);
              order.reserveCredit();
              notificationService.sendEmail(customer.getEmailAddress(), "OrderConfirmation", Map.of("orderId", order.getId()));
              return order;
            })
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
  }

  public void cancelOrder(Long orderId) {
    orderRepository.findById(orderId).get().cancel();
  }
}
