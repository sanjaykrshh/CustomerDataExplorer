package customer.data.function;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import customer.data.model.CursorResponse;
import customer.data.model.Customer;
import customer.data.model.ErrorResponse;
import customer.data.service.CustomerServiceCursor;
import customer.data.service.PageChunk;
import customer.data.util.RequestUtils;
import customer.data.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;


/**
 * Lambda handler - Spring Cloud Function bean
 * Accepts query params: ?limit=20&cursor=eyJ..
 * Returns JSON body with data + nextCursor.
 */
@Component("listCustomers")
public class CustomerDataFunction implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDataFunction.class);
    private final CustomerServiceCursor service;

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;

    public CustomerDataFunction(CustomerServiceCursor service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent requestEvent) {

        Map<String, String> qs = requestEvent.getQueryStringParameters();
        logger.info("listCustomers with params: {}", qs);

        try {
            // Using only single value query params for fetching CustomerList
            String cursor = RequestUtils.first(qs, "cursor").orElse(null);
            int limit = RequestUtils.parseInt(
                    RequestUtils.first(qs, "limit").orElse(null),
                    DEFAULT_LIMIT, 1, MAX_LIMIT);

            logger.info("cursor: {}, limit: {}", cursor, limit);

            PageChunk<Customer> chunk = service.list(cursor, limit);

            CursorResponse<Customer> body = new CursorResponse<>();
            body.setData(chunk.items());
            body.setNextCursor(chunk.nextCursor());
            body.setLimit(limit);

            logger.info("Returning {} customers, status: 200", chunk.items().size());

            return ResponseBuilder.ok(body);
        }

         catch (IllegalArgumentException iae) {
            //invalid cursor decode
            logger.error("Invalid cursor: {}", iae.getMessage());
            return ResponseBuilder.badRequest(new ErrorResponse("Bad Request", "Invalid cursor"));
        } catch (Exception ex) {
            logger.error("Unexpected error", ex);
            return ResponseBuilder.internalServerError(new ErrorResponse("Internal Server Error", "Unexpected error occurred"));
        }

    }
}
