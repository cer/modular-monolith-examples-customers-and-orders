package net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
  List<Order> findAllByCustomerId(Long customerId);
}
