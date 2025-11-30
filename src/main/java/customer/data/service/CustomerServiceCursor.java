package customer.data.service;
import customer.data.model.Customer;
public interface CustomerServiceCursor {
    PageChunk<Customer> list(String cursor, int limit);
}
