package net.chrisrichardson.examples.monolithiccustomersandorders.orders.creditmanagement.grpcproxy;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementInProcessServer;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.domain.CustomerCreditService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("grpc")
public class CreditManagementGrpcProxyConfiguration {

  @Bean(destroyMethod = "shutdownNow")
  public ManagedChannel creditManagementChannel() {
    return InProcessChannelBuilder.forName(CreditManagementInProcessServer.NAME).directExecutor().build();
  }

  @Bean
  public CustomerCreditService grpcCustomerCreditService(ManagedChannel creditManagementChannel) {
    return new CreditManagementGrpcProxy(CreditManagementServiceGrpc.newBlockingStub(creditManagementChannel));
  }
}
