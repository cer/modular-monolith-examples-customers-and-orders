package net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
