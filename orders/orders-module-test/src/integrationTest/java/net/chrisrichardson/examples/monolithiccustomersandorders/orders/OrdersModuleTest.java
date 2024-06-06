package net.chrisrichardson.examples.monolithiccustomersandorders.orders;

import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.CustomerService;
import net.chrisrichardson.examples.monolithiccustomersandorders.customers.api.creditmanagement.CustomerInfo;
import net.chrisrichardson.examples.monolithiccustomersandorders.money.domain.Money;
import net.chrisrichardson.examples.monolithiccustomersandorders.notifications.api.NotificationService;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.web.CreateOrderRequest;
import net.chrisrichardson.examples.monolithiccustomersandorders.orders.web.CreateOrderResponse;
import net.chrisrichardson.examples.monolithiccustomersandorders.testcontainerutil.PropertyProvidingPostgresSQLContainer;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrdersModuleTest {

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

    @MockBean
    private CustomerService customerInfoService;

    @MockBean
    private NotificationService notificationService;

    @LocalServerPort
    private int port;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder().baseUrl(String.format("http://localhost:" + port)).build();
    }

    @Test
    public void shouldStart() {
    }

    @Test
    public void shouldCreateOrder() {
        long customerId = System.currentTimeMillis();
        Money orderTotal = new Money("12.34");

        String emailAddress = "fred@example.com";

        var custInfo = new CustomerInfo(customerId, "Fred", emailAddress, new Money("100"));

        when(customerInfoService.getCustomerInfo(customerId)).thenReturn(custInfo);

        var response = post(new CreateOrderRequest(customerId, orderTotal), CreateOrderResponse.class, "/orders");

        verify(customerInfoService).reserveCredit(customerId, response.getOrderId(), orderTotal);

        verify(notificationService).sendEmail(emailAddress,
                "OrderConfirmation",
                Map.of("orderId", response.getOrderId()));
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
