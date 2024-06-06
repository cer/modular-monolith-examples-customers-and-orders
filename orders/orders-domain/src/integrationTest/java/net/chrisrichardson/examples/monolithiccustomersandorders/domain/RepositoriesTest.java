package net.chrisrichardson.examples.monolithiccustomersandorders.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.Customer;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomersDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.NotificationsDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.Order;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrderRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrderState;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrdersDomainConfiguration;
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
  @Import({OrdersDomainConfiguration.class, CustomersDomainConfiguration.class, NotificationsDomainConfiguration.class})
  static public class Config {
  }

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

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
