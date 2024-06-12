package net.chrisrichardson.examples.monolithiccustomersandorders.customers;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.CustomerService;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CreditManagement;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerDomainObserver;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerDomainObservers;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.observer.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomersModuleTest {

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan
    public static class Config {
    }

    private static PropertyProvidingPostgresSQLContainer<?> postgres = new PropertyProvidingPostgresSQLContainer<>();

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        postgres.startAndRegisterProperties(registry);
    }


    @LocalServerPort
    private int port;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder().baseUrl(String.format("http://localhost:" + port)).build();
    }

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CreditManagement creditManagement;

    @Autowired
    private CustomerDomainObservers customerDomainObservers;

    @MockBean
    private CustomerDomainObserver customerDomainObserver;

    private static boolean registered;

    @BeforeEach
    public void registerObserver() {
        if (!registered) {
            customerDomainObservers.registerObserver(customerDomainObserver);
            registered = true;
        }
    }

    @Test
    public void shouldStart() {
    }

    @Test
    public void shouldCreateCustomerAndReserveCredit() {
        Money creditLimit = new Money("12.34");
        long orderId = 123L;

        var customer = customerService.createCustomer("Fred", creditLimit);

        assertObserversNotified(customer);

        creditManagement.reserveCredit(customer.customerId(), orderId, creditLimit);

        var customerInfo = creditManagement.getCustomerInfo(customer.customerId());
        assertEquals(Money.ZERO, customerInfo.availableCredit());
    }

    private void assertObserversNotified(net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo customer) {
        var arg = ArgumentCaptor.forClass(CustomerInfo.class);
        verify(customerDomainObserver).noteCustomerCreated(arg.capture());
        CustomerInfo capturedArg = arg.getValue();
        assertEquals(customer.customerId(), capturedArg.customerId());
        assertEquals(customer.creditLimit(), capturedArg.creditLimit());
    }

    @Nullable
    private <Request, Response> Response post(Request request, Class<Response> responseType, String path, Object... uriVariables) {
        return restClient.post()
                .uri(path, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(responseType);
    }
}
