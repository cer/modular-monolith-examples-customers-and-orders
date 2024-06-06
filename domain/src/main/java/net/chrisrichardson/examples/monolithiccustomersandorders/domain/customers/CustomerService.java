package net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers;


import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.notifications.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final NotificationService notificationService;

  @Autowired
  public CustomerService(CustomerRepository customerRepository, NotificationService notificationService) {
    this.customerRepository = customerRepository;
    this.notificationService = notificationService;
  }

  @Transactional
  public Customer createCustomer(String name, Money creditLimit) {
    Customer customer  = new Customer(name, creditLimit);
    Customer savedCustomer = customerRepository.save(customer);
    notificationService.sendEmail(savedCustomer.getEmailAddress(), "Welcome", Map.of("name", customer.getName()));
    return savedCustomer;
  }

}
