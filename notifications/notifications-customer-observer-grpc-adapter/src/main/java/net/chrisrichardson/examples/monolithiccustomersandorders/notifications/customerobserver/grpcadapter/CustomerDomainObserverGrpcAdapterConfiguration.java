package net.chrisrichardson.examples.monolithiccustomersandorders.notifications.customerobserver.grpcadapter;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserversInProcessServer;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserversServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.RegisterObserverRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.domain.CustomerEventHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("grpc")
public class CustomerDomainObserverGrpcAdapterConfiguration {

  static final String SERVER_NAME = "notifications-customer-domain-observer";

  @Bean
  public CustomerDomainObserverGrpcService customerDomainObserverGrpcService(CustomerEventHandler customerEventHandler) {
    return new CustomerDomainObserverGrpcService(customerEventHandler);
  }

  // Injecting CustomerDomainObserversInProcessServer ensures that the customers server has started
  @Bean(destroyMethod = "shutdownNow")
  public ManagedChannel customerDomainObserversChannel(CustomerDomainObserversInProcessServer customerDomainObserversInProcessServer) {
    return InProcessChannelBuilder.forName(customerDomainObserversInProcessServer.name()).directExecutor().build();
  }

  // directExecutor() runs each call on the caller's thread, so it joins the publisher's transaction
  @Bean(destroyMethod = "shutdownNow")
  public Server customerDomainObserverGrpcServer(CustomerDomainObserverGrpcService customerDomainObserverGrpcService,
                                                 @Qualifier("customerDomainObserversChannel") ManagedChannel customerDomainObserversChannel) throws IOException {
    Server server = InProcessServerBuilder.forName(SERVER_NAME)
            .directExecutor()
            .addService(customerDomainObserverGrpcService)
            .build()
            .start();
    CustomerDomainObserversServiceGrpc.newBlockingStub(customerDomainObserversChannel)
            .registerObserver(RegisterObserverRequest.newBuilder().setInProcessServerName(SERVER_NAME).build());
    return server;
  }
}
