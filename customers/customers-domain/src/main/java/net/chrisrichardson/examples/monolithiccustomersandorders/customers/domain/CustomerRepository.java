package net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
