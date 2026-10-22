package net.chrisrichardson.examples.monolithiccustomersandorders.orders.creditmanagement.javaproxy;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.CustomerCreditService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!grpc")
public class CreditManagementJavaProxyConfiguration {

  @Bean
  public CustomerCreditService javaCustomerCreditService(CreditManagement creditManagement) {
    return new CreditManagementJavaProxy(creditManagement);
  }
}
