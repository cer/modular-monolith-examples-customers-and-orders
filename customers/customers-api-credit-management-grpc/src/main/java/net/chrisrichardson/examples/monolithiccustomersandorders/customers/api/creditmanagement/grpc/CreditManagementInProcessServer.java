package net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.grpc;

/**
 * The service runs on an in-process gRPC server. Both the server and the client channel use
 * directExecutor() and the client uses a blocking stub, so a call runs on the caller's thread
 * and joins the caller's Spring transaction.
 */
public final class CreditManagementInProcessServer {

  public static final String NAME = "customers-credit-management";

  private CreditManagementInProcessServer() {
  }
}
