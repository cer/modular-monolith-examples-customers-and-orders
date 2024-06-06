package net.chrisrichardson.examples.monolithiccustomersandorders.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers.Customer;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.customers.CustomerRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.money.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders.Order;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders.OrderRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.domain.orders.OrderState;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.assertEquals;

@DataJpaTest
@ContextConfiguration(classes=RepositoriesTest.Config.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class RepositoriesTest {


  private static PropertyProvidingPostgresSQLContainer<?> postgres = new PropertyProvidingPostgresSQLContainer<>();

  @DynamicPropertySource
  static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
    postgres.startAndRegisterProperties(registry);
  }

  public static final String customerName = "Chris";

  @Configuration
  @Import(DomainConfiguration.class)
  static public class Config {
  }

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldSaveAndLoadCustomer() {
    Money creditLimit = new Money("12.34");
    Money amount = new Money("10");
    Money expectedAvailableCredit = creditLimit.subtract(amount);

    Customer c = new Customer(customerName, creditLimit);

    transactionTemplate.executeWithoutResult( ts -> customerRepository.save(c) );

    long customerId = c.getId();

    transactionTemplate.executeWithoutResult(ts -> {
      Customer c2 = customerRepository.findById(customerId).get();
      assertEquals(customerName, c2.getName());
      assertEquals(creditLimit, c2.getCreditLimit());
      assertEquals(creditLimit, c2.availableCredit());

      c2.reserveCredit(1234L, amount);
    });

    transactionTemplate.executeWithoutResult(ts -> {
      Customer c2 = customerRepository.findById(customerId).get();
      assertEquals(expectedAvailableCredit, c2.availableCredit());

    });

  }

  @Test
  public void shouldSaveAndLoadOrder() {
    Money creditLimit = new Money("12.34");
    Money amount = new Money("10");
    Customer c = new Customer(customerName, creditLimit);

    transactionTemplate.executeWithoutResult( ts -> customerRepository.save(c) );

    Order order = new Order(c, amount);
    transactionTemplate.executeWithoutResult( ts -> orderRepository.save(order) );

    long orderId = order.getId();

    transactionTemplate.executeWithoutResult(ts -> {
      Order o = orderRepository.findById(orderId).get();
      assertEquals(OrderState.PENDING, o.getState());
      assertEquals(amount, o.getOrderTotal());
      assertEquals(c.getId(), o.getCustomer().getId());
      o.cancel();
    });

    transactionTemplate.executeWithoutResult(ts -> {
      Order o = orderRepository.findById(orderId).get();
      assertEquals(OrderState.CANCELLED, o.getState());
    });

  }
}
