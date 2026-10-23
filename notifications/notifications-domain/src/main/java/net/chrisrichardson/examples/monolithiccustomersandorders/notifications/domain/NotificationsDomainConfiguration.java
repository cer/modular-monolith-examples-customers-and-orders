package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EntityScan
@ComponentScan
public class NotificationsDomainConfiguration {

}
