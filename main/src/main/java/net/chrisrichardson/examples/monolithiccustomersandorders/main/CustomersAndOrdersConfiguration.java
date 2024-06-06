package net.chrisrichardson.examples.monolithiccustomersandorders.main;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.domain.CustomersDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.NotificationsDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.OrdersDomainConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.web.CustomersWebConfiguration;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.web.OrdersWebConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CustomersWebConfiguration.class, CustomersDomainConfiguration.class, OrdersWebConfiguration.class,
        OrdersDomainConfiguration.class, NotificationsDomainConfiguration.class})
public class CustomersAndOrdersConfiguration {
}
