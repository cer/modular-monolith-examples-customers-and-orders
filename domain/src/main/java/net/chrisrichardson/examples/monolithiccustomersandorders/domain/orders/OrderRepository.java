package net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
  List<Order> findAllByCustomerId(Long customerId);
}
