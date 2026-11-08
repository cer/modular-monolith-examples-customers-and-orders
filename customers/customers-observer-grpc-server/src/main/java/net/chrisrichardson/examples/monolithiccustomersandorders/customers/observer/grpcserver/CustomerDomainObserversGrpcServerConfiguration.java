package net.chrisrichardson.examples.monolithiccustomersandorders.customers.observer.grpcserver;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserversInProcessServer;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObservers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("grpc")
public class CustomerDomainObserversGrpcServerConfiguration {

  @Bean(destroyMethod = "shutdown")
  public CustomerDomainObserversGrpcService customerDomainObserversGrpcService(CustomerDomainObservers customerDomainObservers) {
    return new CustomerDomainObserversGrpcService(customerDomainObservers);
  }

  @Bean(destroyMethod = "shutdownNow")
  public Server customerDomainObserversGrpcServer(CustomerDomainObserversGrpcService customerDomainObserversGrpcService) throws IOException {
    return InProcessServerBuilder.forName(CustomerDomainObserversInProcessServer.NAME)
            .directExecutor()
            .addService(customerDomainObserversGrpcService)
            .build()
            .start();
  }

  // Observers inject this bean, so that they register only after customerDomainObserversGrpcServer has started
  @Bean
  public CustomerDomainObserversInProcessServer customerDomainObserversInProcessServer(
          @Qualifier("customerDomainObserversGrpcServer") Server customerDomainObserversGrpcServer) {
    return new CustomerDomainObserversInProcessServer(CustomerDomainObserversInProcessServer.NAME);
  }
}
