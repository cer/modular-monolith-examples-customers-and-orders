package net.chrisrichardson.examples.monolithiccustomersandorders.domain;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.Customer;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomerRepository;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomersDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.assertEquals;

@DataJpaTest
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
  @Import({CustomersDomainConfiguration.class})
  static public class Config {
  }

  @Autowired
  private CustomerRepository customerRepository;

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

}
