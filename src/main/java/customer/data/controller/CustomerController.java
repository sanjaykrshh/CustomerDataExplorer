/*
package customer.data.controller;

import customer.data.model.CursorResponse;
import customer.data.model.Customer;
import customer.data.model.ErrorResponse;
import customer.data.service.CustomerServiceCursor;
import customer.data.service.PageChunk;
import customer.data.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

*/
/**
 * REST Controller for local development and testing
 * Exposes the same functionality as the Lambda function via HTTP endpoints
 *//*

@RestController
@RequestMapping("/api")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerServiceCursor service;

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;

    public CustomerController(CustomerServiceCursor service) {
        this.service = service;
    }

    @GetMapping("/customers")
    public ResponseEntity<?> listCustomers(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String limit) {

        logger.info("REST Request IN - /api/customers - cursor: {}, limit: {}", cursor, limit);

        try {
            int parsedLimit = RequestUtils.parseInt(limit, DEFAULT_LIMIT, 1, MAX_LIMIT);

            PageChunk<Customer> chunk = service.list(cursor, parsedLimit);

            CursorResponse<Customer> response = new CursorResponse<>();
            response.setData(chunk.items());
            response.setNextCursor(chunk.nextCursor());
            response.setLimit(parsedLimit);

            logger.info("REST Request OUT - Returning {} customers, status: 200", chunk.items().size());

            return ResponseEntity.ok()
                    .header("Cache-Control", "no-store")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(response);

        } catch (IllegalArgumentException iae) {
            logger.error("REST Request FAILED - Invalid cursor: {}", iae.getMessage());
            ErrorResponse error = new ErrorResponse("Bad Request", "Invalid cursor");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (Exception ex) {
            logger.error("REST Request FAILED - Unexpected error", ex);
            ErrorResponse error = new ErrorResponse("Internal Server Error", "Unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
*/
