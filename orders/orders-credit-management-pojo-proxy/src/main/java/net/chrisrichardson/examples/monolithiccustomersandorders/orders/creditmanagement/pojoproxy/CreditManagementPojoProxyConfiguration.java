package net.chrisrichardson.examples.monolithiccustomersandorders.orders.creditmanagement.pojoproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.pojo.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.CustomerCreditService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!grpc")
public class CreditManagementPojoProxyConfiguration {

  @Bean
  public CustomerCreditService pojoCustomerCreditService(CreditManagement creditManagement) {
    return new CreditManagementPojoProxy(creditManagement);
  }
}
