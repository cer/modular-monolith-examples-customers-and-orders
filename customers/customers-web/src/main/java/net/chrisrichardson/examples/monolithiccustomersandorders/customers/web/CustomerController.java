package net.chrisrichardson.examples.monolithiccustomersandorders.customers.web;


import jakarta.validation.Valid;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.CustomerService;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
public class CustomerController {

  private CustomerService customerService;
  private CustomerRepository customerRepository;

  @Autowired
  public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
    this.customerService = customerService;
    this.customerRepository = customerRepository;
  }

  @RequestMapping(value = "/customers", method = RequestMethod.POST)
  public CustomerInfo createCustomer(@RequestBody @Valid CreateCustomerRequest createCustomerRequest) {
    return customerService.createCustomer(createCustomerRequest.getName(), createCustomerRequest.getCreditLimit());
  }

  @RequestMapping(value="/customers", method= RequestMethod.GET)
  public ResponseEntity<GetCustomersResponse> getAll() {
    return ResponseEntity.ok(new GetCustomersResponse(customerService.findAll().stream()
            .map(c -> new GetCustomerResponse(c.customerId(), c.name(), c.creditLimit())).collect(Collectors.toList())));
  }

  @RequestMapping(value="/customers/{customerId}", method= RequestMethod.GET)
  public ResponseEntity<GetCustomerResponse> getCustomer(@PathVariable Long customerId) {
    return customerService
            .findById(customerId)
            .map(c -> new ResponseEntity<>(new GetCustomerResponse(c.customerId(), c.name(), c.creditLimit()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
