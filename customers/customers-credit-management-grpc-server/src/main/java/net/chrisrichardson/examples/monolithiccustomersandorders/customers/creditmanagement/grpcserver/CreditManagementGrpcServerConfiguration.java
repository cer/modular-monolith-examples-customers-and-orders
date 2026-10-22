package net.chrisrichardson.examples.monolithiccustomersandorders.customers.creditmanagement.grpcserver;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc.CreditManagementInProcessServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("grpc")
public class CreditManagementGrpcServerConfiguration {

  @Bean
  public CreditManagementGrpcService creditManagementGrpcService(CreditManagement creditManagement) {
    return new CreditManagementGrpcService(creditManagement);
  }

  // directExecutor() runs each call on the caller's thread, so it joins the caller's transaction
  @Bean(destroyMethod = "shutdownNow")
  public Server creditManagementGrpcServer(CreditManagementGrpcService creditManagementGrpcService) throws IOException {
    return InProcessServerBuilder.forName(CreditManagementInProcessServer.NAME)
            .directExecutor()
            .addService(creditManagementGrpcService)
            .build()
            .start();
  }
}
