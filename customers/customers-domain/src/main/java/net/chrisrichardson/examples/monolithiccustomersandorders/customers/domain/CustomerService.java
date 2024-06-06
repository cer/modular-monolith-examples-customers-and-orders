package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;


import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final NotificationService notificationService;

  @Autowired
  public CustomerService(CustomerRepository customerRepository, NotificationService notificationService) {
    this.customerRepository = customerRepository;
    this.notificationService = notificationService;
  }

  public Customer createCustomer(String name, Money creditLimit) {
    Customer customer  = new Customer(name, creditLimit);
    Customer savedCustomer = customerRepository.save(customer);
    notificationService.sendEmail(savedCustomer.getEmailAddress(), "Welcome", Map.of("name", customer.getName()));
    return savedCustomer;
  }

  public void reserveCredit(long customerId, long orderId, Money orderTotal) {
    findRequiredCustomerById(customerId).reserveCredit(orderId, orderTotal);
  }

  public void releaseCredit(long customerId, long orderId) {
    findRequiredCustomerById(orderId).releaseCredit(orderId);
  }

  public CustomerInfo getCustomerInfo(long customerId) {
    return makeCustomerInfo(findRequiredCustomerById(customerId));
  }

  private CustomerInfo makeCustomerInfo(Customer customer) {
    return new CustomerInfo(customer.getId(), customer.getName(), customer.getEmailAddress(), customer.getCreditLimit());
  }

  private Customer findRequiredCustomerById(long customerId) {
    return customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
  }

}
