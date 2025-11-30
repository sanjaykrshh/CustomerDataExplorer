package customer.data.service.impl;

import customer.data.config.CustomerProperties;
import customer.data.model.Customer;
import customer.data.service.CustomerServiceCursor;
import customer.data.service.PageChunk;
import customer.data.util.CursorCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerServiceCursorImpl implements CustomerServiceCursor {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceCursorImpl.class);
    private final CustomerProperties customerProperties;

    public CustomerServiceCursorImpl(CustomerProperties customerProperties) {
        this.customerProperties = customerProperties;
    }

    @Override
    public PageChunk<Customer> list(String cursor, int limit) {
        logger.info("list() called with cursor: {}, limit: {}", cursor, limit);

        String lastId = decodeLastId(cursor);

        // Get all customers from properties and sort by customerId
        List<Customer> allCustomers = customerProperties.getCustomers().stream()
                .sorted(Comparator.comparingInt(Customer::getCustomerId))
                .collect(Collectors.toList());

        logger.info("Loaded customers from properties", allCustomers.size());

        // Filter customers based on cursor
        int startIndex = 0;
        if (lastId != null) {
            int lastCustomerId = parseIntSafe(lastId);
            startIndex = findStartIndex(allCustomers, lastCustomerId);
        }

        // Get the page of customers
        int endIndex = Math.min(startIndex + limit, allCustomers.size());
        List<Customer> items = allCustomers.subList(startIndex, endIndex);

        // Generate next cursor if there are more items
        String nextCursor = null;
        boolean hasNext = endIndex < allCustomers.size();
        if (hasNext && !items.isEmpty()) {
            Customer lastCustomer = items.get(items.size() - 1);
            nextCursor = CursorCodec.encode(Map.of("lastId", lastCustomer.getCustomerId()));
        }

        logger.info("Returning {} items, hasNext: {}", items.size(), hasNext);

        return new PageChunk<>(items, nextCursor);
    }

    private int findStartIndex(List<Customer> customers, int lastCustomerId) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId() > lastCustomerId) {
                return i;
            }
        }
        return customers.size();
    }

    private String decodeLastId(String cursor) {
        Map<String, Object> payload = CursorCodec.decode(cursor);
        if (payload == null) return null;
        Object lastId = payload.get("lastId");
        return lastId == null ? null : String.valueOf(lastId);
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
