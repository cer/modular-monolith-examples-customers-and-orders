package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.grpc;

/**
 * A bean of this type exists once the customers module's in-process CustomerDomainObserversService server has started.
 * An observer injects it, so that it registers only after the server has started.
 */
public record CustomerDomainObserversInProcessServer(String name) {

  public static final String NAME = "customers-domain-observers";
}
