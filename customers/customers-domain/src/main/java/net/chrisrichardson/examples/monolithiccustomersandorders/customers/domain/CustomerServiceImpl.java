package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;


import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.CustomerService;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerDomainObserversImpl customerDomainObservers;

  @Autowired
  public CustomerServiceImpl(CustomerRepository customerRepository, CustomerDomainObserversImpl customerDomainObservers) {
    this.customerRepository = customerRepository;
    this.customerDomainObservers = customerDomainObservers;
  }

  @Override
  public CustomerInfo createCustomer(String name, Money creditLimit) {
    Customer customer  = new Customer(name, creditLimit);
    Customer savedCustomer = customerRepository.save(customer);
    customerDomainObservers.noteCustomerCreated(new net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerInfo(savedCustomer.getId(),
            customer.getName(),
            savedCustomer.getEmailAddress(),
            creditLimit,
            customer.availableCredit()));
    return makeCustomerInfo(savedCustomer);
  }

  @Override
  public List<CustomerInfo> findAll() {
    return StreamSupport.stream(customerRepository.findAll().spliterator(), false).map(this::makeCustomerInfo).collect(toList());
  }

  @Override
  public Optional<CustomerInfo> findById(long customerId) {
    return customerRepository.findById(customerId).map(this::makeCustomerInfo);
  }


  @Override
  public void reserveCredit(long customerId, long orderId, Money orderTotal) {
    findRequiredCustomerById(customerId).reserveCredit(orderId, orderTotal);
  }

  @Override
  public void releaseCredit(long customerId, long orderId) {
    findRequiredCustomerById(orderId).releaseCredit(orderId);
  }

  @Override
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
