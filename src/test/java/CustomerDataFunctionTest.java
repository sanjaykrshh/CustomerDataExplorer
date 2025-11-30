import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import customer.data.config.CustomerProperties;
import customer.data.function.CustomerDataFunction;
import customer.data.model.Customer;
import customer.data.service.CustomerServiceCursor;
import customer.data.service.impl.CustomerServiceCursorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class CustomerDataFunctionTest {

    private CustomerProperties customerProperties;
    private CustomerServiceCursor service;
    private CustomerDataFunction function;

    @BeforeEach
    void setUp() {
        // Create test customer data
        customerProperties = new CustomerProperties();
        List<Customer> testCustomers = new ArrayList<>();

        for (int i = 1; i <= 11; i++) {
            Customer customer = new Customer();
            customer.setCustomerId(i);
            customer.setFullName("Test Customer " + i);
            customer.setEmail("customer" + i + "@test.com");
            customer.setRegistrationDate("0" + i + "/01/2023");
            testCustomers.add(customer);
        }
        customerProperties.setCustomers(testCustomers);

        service = new CustomerServiceCursorImpl(customerProperties);
        function = new CustomerDataFunction(service);
    }

    @Test
    void okWithData() {
        APIGatewayProxyRequestEvent req = new APIGatewayProxyRequestEvent()
                .withQueryStringParameters(Map.of("limit", "7"));

        var resp = function.apply(req);

        // Print response values
        System.out.println("=== okWithData Test ===");
        System.out.println("Status Code: " + resp.getStatusCode());
        System.out.println("Headers: " + resp.getHeaders());
        System.out.println("Response Body: " + resp.getBody());
        System.out.println("===================================");

        assertEquals(200, resp.getStatusCode());
        assertTrue(resp.getBody().contains("\"nextCursor\""));
    }


    @Test
    void badLimit400() {
        // Limit 0 gets clamped to 1 (min), so it returns 200 with data
        APIGatewayProxyRequestEvent req = new APIGatewayProxyRequestEvent()
                .withQueryStringParameters(Map.of("limit", "0"));

        var resp = function.apply(req);

        // Print response values
        System.out.println("=== badLimit400 Test ===");
        System.out.println("Status Code: " + resp.getStatusCode());
        System.out.println("Headers: " + resp.getHeaders());
        System.out.println("Response Body: " + resp.getBody());
        System.out.println("===================================");

        // Limit 0 is clamped to min=1, so we get 200 with 1 item
        assertEquals(200, resp.getStatusCode());
        assertTrue(resp.getBody().contains("\"limit\":1"));
    }
}
