package net.chrisrichardson.examples.monolithiccustomersandorders.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.NotificationService;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.Order;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrderRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrderState;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrdersDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes=OrdersDomainConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class RepositoriesTest {


  private static PropertyProvidingPostgresSQLContainer<?> postgres = new PropertyProvidingPostgresSQLContainer<>();

  @DynamicPropertySource
  static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
    postgres.startAndRegisterProperties(registry);
  }

  @Configuration
  @Import({OrdersDomainConfiguration.class})
  static public class Config {
  }

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @MockBean
  private CreditManagement customerInfoService;

  @MockBean
  private NotificationService notificationService;


  @Test
  public void shouldSaveAndLoadOrder() {
    Money amount = new Money("10");
    long customerId = System.currentTimeMillis();

    Order order = new Order(customerId, amount);

    executeInTransaction( () -> {
        orderRepository.save(order);
    });

    long orderId = order.getId();

    executeInTransaction(() -> {
      Order o = orderRepository.findById(orderId).get();
      assertEquals(OrderState.PENDING, o.getState());
      assertEquals(amount, o.getOrderTotal());
      assertEquals(customerId, o.getCustomerId());
      o.cancel();
    });

    executeInTransaction(() -> {
      Order o = orderRepository.findById(orderId).get();
      assertEquals(OrderState.CANCELLED, o.getState());
    });

  }

  private void executeInTransaction(Runnable runnable) {
    withTimer(() -> transactionTemplate.executeWithoutResult(ts -> runnable.run()));
  }

  private void withTimer(Runnable runnable) {
    long startTime = System.currentTimeMillis();
    runnable.run();
    long endTime = System.currentTimeMillis();
    System.out.println("Elapsed time: " + (endTime - startTime));
  }
}
