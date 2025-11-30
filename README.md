# Customer Data Explorer

A Spring Cloud Function-based AWS Lambda that provides cursor-based pagination for customer data with request validation.

## Overview

This serverless application exposes a Lambda function that returns paginated customer data using cursor-based pagination. It demonstrates stateless pagination without requiring database persistence. This implementation is ideal for scalability and serverless architecture.

## Features

- ✅ **Cursor-Based Pagination** - Stateless continuation tokens for efficient page traversal
- ✅ **Request Validation** - Query parameter validation with sensible defaults and limits
- ✅ **REST Practices** - Proper HTTP status codes, headers, and error responses
- ✅ **Security Headers** - Cache-Control and X-Content-Type-Options for enhanced security
- ✅ **Configuration-Based Data** - Customer data loaded from application.properties (no database required)
- ✅ **CORS Support** - Cross-origin requests enabled for frontend integration


## Query Parameters

### `limit` (optional)
- **Description**: Number of customers to return per page
- **Type**: Integer
- **Default**: `5`
- **Min**: `1`
- **Max**: `10`
- **Validation**: Values outside the range are clamped to min/max
- **Example**: `?limit=5`

### `cursor` (optional)
- **Description**: Base64-encoded cursor token for pagination continuation
- **Type**: String (Base64-encoded JSON)
- **Format**: `eyJsYXN0SWQiOjV9` (decodes to `{"lastId":5}`)
- **Behavior**: 
  - If **null** or **omitted**: Returns the first page (customers 1-5)
  - If **provided**: Returns the next page starting after the `lastId`
- **Example**: `?cursor=eyJsYXN0SWQiOjV9`

### Combined Example
```
?limit=5&cursor=eyJsYXN0SWQiOjV9
```

## API Request/Response

### Request (First Page)
```http
GET /api/customers?limit=5
```
**Note**: `nextCursor` is `null` when you've reached the last page.

### Pagination Flow

**Step 1: First Request** (no cursor)
```
Request:  GET /api/customers?limit=5
Process:  Fetch customers where customerId > 0
Result:   Returns customers [1, 2, 3, 4, 5]
Cursor:   eyJsYXN0SWQiOjV9 (lastId = 5)
```

**Step 2: Second Request** (with cursor)
```
Request:  GET /api/customers?limit=5&cursor=eyJsYXN0SWQiOjV9
Decode:   cursor = {"lastId": 5}
Process:  Fetch customers where customerId > 5
Result:   Returns customers [6, 7, 8, 9, 10]
Cursor:   null (no more data)
```

## Request Validation

### Limit Validation

The `RequestUtils.parseInt()` method handles all limit validation with clamping:

| Input Value | Parsed Value | Reason |
|-------------|--------------|--------|
| `null` (not provided) | `5` | Default value applied |
| `""` (empty string) | `5` | Default value applied |
| `"0"` | `1` | Clamped to minimum (MIN=1) |
| `"1"` | `1` | Valid, within range |
| `"5"` | `5` | Valid, matches default |
| `"10"` | `10` | Valid, at maximum |
| `"20"` | `10` | Clamped to maximum (MAX=10) |
| `"abc"` (non-numeric) | `5` | Invalid, default applied |
| `"-5"` (negative) | `1` | Clamped to minimum |

**All limit values are validated and sanitized** - no invalid values reach the service layer.

### Cursor Validation

| Cursor Value | Behavior | HTTP Status |
|--------------|----------|-------------|
| `null` (not provided) | Treated as first page | 200 OK |
| Valid Base64 + Valid JSON | Decoded and processed | 200 OK |
| Invalid Base64 | Error response | 400 Bad Request |
| Valid Base64 but invalid JSON | Error response | 400 Bad Request |


### Error Responses

#### 400 Bad Request (Invalid Cursor)
```json
{
  "title": "Bad Request",
  "detail": "Invalid cursor"
}
```

#### 500 Internal Server Error
```json
{
  "title": "Internal Server Error",
  "detail": "Unexpected error occurred"
}
```
## Configuration

### application.properties

Customer data is configured in `src/main/resources/application.properties`:

```properties
customers[0].customerId=1
customers[0].fullName=XXX
customers[0].email=***.***@***.**
customers[0].registrationDate=15/01/2023

customers[1].customerId=2
customers[1].fullName=***
...
```

**To add more customers**, simply add additional entries with incremented indices.


## Data Model

## Response Headers

### Success Response (200 OK)
```
Content-Type: application/json
Cache-Control: no-store
X-Content-Type-Options: nosniff
Access-Control-Allow-Origin: http://localhost:5173
```

### Error Responses (400, 500)
```
Content-Type: application/json
```

### Environment Variables

```properties
spring.cloud.function.definition=listCustomers
```

## Data Model

### Customer Object

```java
public class Customer {
    private int customerId;        // Unique identifier (used for cursor pagination)
    private String fullName;       // Customer full name
    private String email;          // Customer email address
    private String registrationDate; // Registration date (dd/MM/yyyy format)
}
```

## Logging

The application includes comprehensive logging for transaction tracking:

## Security Considerations

- **No-store cache**: Prevents sensitive data caching
- **MIME type sniffing prevention**: X-Content-Type-Options header
- **Input validation**: All query parameters are validated and sanitized
- **Error handling**: Generic error messages prevent information leakage
- **CORS**: Configured for specific origins only

## Author

**SanjayKrishnan Kannan**

This project is for demonstration purposes.
