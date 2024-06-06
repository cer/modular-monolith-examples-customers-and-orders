package net.chrisrichardson.examples.monolithiccustomersandorders.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CustomersAndOrdersConfiguration.class)
public class CustomersAndOrdersMain {

  public static void main(String[] args) {
    SpringApplication.run(CustomersAndOrdersMain.class, args);
  }
}
