package net.chrisrichardson.examples.monolithiccustomersandorders.customers.observer.grpcserver;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserverServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.CustomerDomainObserversServiceGrpc;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.RegisterObserverRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc.RegisterObserverResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.pojo.CustomerDomainObservers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registers a CustomerDomainObserverGrpcProxy for each observer's in-process gRPC server.
 * The channel uses directExecutor(), so an observer runs on the publisher's thread and joins its transaction.
 */
public class CustomerDomainObserversGrpcService extends CustomerDomainObserversServiceGrpc.CustomerDomainObserversServiceImplBase {

  private final CustomerDomainObservers customerDomainObservers;
  private final List<ManagedChannel> observerChannels = new CopyOnWriteArrayList<>();

  public CustomerDomainObserversGrpcService(CustomerDomainObservers customerDomainObservers) {
    this.customerDomainObservers = customerDomainObservers;
  }

  @Override
  public void registerObserver(RegisterObserverRequest request, StreamObserver<RegisterObserverResponse> responseObserver) {
    ManagedChannel channel = InProcessChannelBuilder.forName(request.getInProcessServerName()).directExecutor().build();
    observerChannels.add(channel);
    customerDomainObservers.registerObserver(new CustomerDomainObserverGrpcProxy(CustomerDomainObserverServiceGrpc.newBlockingStub(channel)));
    responseObserver.onNext(RegisterObserverResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }

  public void shutdown() {
    observerChannels.forEach(ManagedChannel::shutdownNow);
  }
}
