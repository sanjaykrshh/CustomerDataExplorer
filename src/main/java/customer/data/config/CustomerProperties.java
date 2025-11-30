package customer.data.config;

import customer.data.model.Customer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class to bind customer data from application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "")
public class CustomerProperties {

    private List<Customer> customers = new ArrayList<>();

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}

