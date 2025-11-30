package customer.data.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import customer.data.model.ErrorResponse;

import java.util.Map;

/**
 * Utility class for building API Gateway responses
 */
public class ResponseBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final Map<String, String> JSON_HEADERS = Map.of(
            "Content-Type", "application/json"
    );

    private static final Map<String, String> SECURE_JSON_HEADERS = Map.of(
            "Content-Type", "application/json",
            "Cache-Control", "no-store",
            "X-Content-Type-Options", "nosniff"
    );

    /**
     *  200 OK response with JSON body
     */
    public static APIGatewayProxyResponseEvent ok(Object body) {
        return buildResponse(200, SECURE_JSON_HEADERS, body);
    }

    /**
     * 400 Bad Request response with error body
     */
    public static APIGatewayProxyResponseEvent badRequest(ErrorResponse body) {
        return buildResponse(400, JSON_HEADERS, body);
    }

    /**
     * 500 Internal Server Error response with error body
     */
    public static APIGatewayProxyResponseEvent internalServerError(ErrorResponse body) {
        return buildResponse(500, JSON_HEADERS, body);
    }

    /**
     * Build a response with given status code, headers, and body
     */
    private static APIGatewayProxyResponseEvent buildResponse(int statusCode, Map<String, String> headers, Object body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(write(body));
    }

    /**
     * Serialize object to JSON string
     */
    private static String write(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"serialization failure\"}";
        }
    }
}
