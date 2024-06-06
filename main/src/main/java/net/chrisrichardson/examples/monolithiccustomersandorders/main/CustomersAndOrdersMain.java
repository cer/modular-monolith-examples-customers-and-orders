package net.chrisrichardson.examples.monolithiccustomersandorders.main;

import net.chrisrichardson.examples.monolithiccustomersandorders.domain.DomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.web.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({WebConfiguration.class, DomainConfiguration.class})
public class CustomersAndOrdersMain {

  public static void main(String[] args) {
    SpringApplication.run(CustomersAndOrdersMain.class, args);
  }
}
