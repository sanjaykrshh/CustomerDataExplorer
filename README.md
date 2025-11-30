# Customer Data Explorer

A Spring Cloud Function-based AWS Lambda that provides cursor-based pagination for customer data with request validation.

## Overview

This serverless application exposes a Lambda function that returns paginated customer data using cursor-based pagination. 
It demonstrates stateless pagination without requiring database persistence, this implementation is ideal for scalability and serverless architecture.

## Features

- ✅ **Cursor-Based Pagination** - Stateless continuation tokens for efficient page traversal
- ✅ **Request Validation** - Query parameter validation with sensible defaults and limits
- ✅ **REST Practices** - Proper HTTP status codes, headers, and error responses
- ✅ **Security Headers** - Cache-Control and X-Content-Type-Options for enhanced security
- ✅ **Service-Layer Sorting** - Customer data sorted by `customerId` ascending
- ✅ **No Database Required** - In-memory data generation from properties for demonstration purposes

## Architecture

```
API Gateway → Lambda (Spring Cloud Function) → CustomerDataFunction
                                                      ↓
                                              CustomerServiceCursorImpl
                                                      ↓
                                              Generate & Paginate Data
```

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
GET /listCustomers?limit=5
```

### Response (First Page)
```json
{
  "data": [
    {
      "customerId": 1,
      "fullName": "Bob 1",
      "email": "Bob1@cbussuper.com.au",
      "registrationDate": "22/10/2022"
    },
    {
      "customerId": 2,
      "fullName": "Bob 2",
      "email": "Bob2@cbussuper.com.au",
      "registrationDate": "04/12/1975"
    },
    ...
    {
      "customerId": 5,
      "fullName": "Bob 5",
      "email": "Bob5@cbussuper.com.au",
      "registrationDate": "06/01/1989"
    }
  ],
  "nextCursor": "eyJsYXN0SWQiOjV9",
  "limit": 5
}
```

### Request (Second Page)
```http
GET /listCustomers?limit=5&cursor=eyJsYXN0SWQiOjV9
```

### Response (Second Page)
```json
{
  "data": [
    {
      "customerId": 6,
      "fullName": "Bob 6",
      "email": "Bob6@cbussuper.com.au",
      "registrationDate": "22/08/1994"
    },
    ...
    {
      "customerId": 10,
      "fullName": "Bob 10",
      "email": "Bob10@cbussuper.com.au",
      "registrationDate": "24/11/1989"
    }
  ],
  "nextCursor": null,
  "limit": 5
}
```

**Note**: `nextCursor` is `null` when you've reached the last page.


## Request Validation

### Query Parameter Validation

The function validates and sanitizes all query parameters:

#### Limit Validation
- **Missing `limit`**: Defaults to `5`
- **Invalid integer**: Defaults to `5`
- **Out of range** (`< 1` or `> 10`): Clamped to `1` or `10`
- **Example**: `?limit=0` → automatically becomes `limit=1`

#### Cursor Validation
- **Missing `cursor`**: Treated as first page request
- **Invalid Base64**: Returns `400 Bad Request`
- **Malformed JSON**: Returns `400 Bad Request`

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

## Deployment

### AWS Lambda Configuration

- **Handler**: `org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest`
- **Runtime**: Java 17
- **Memory**: 512 MB (recommended)
- **Timeout**: 30 seconds

### Environment Variables

```
spring.cloud.function.definition=listCustomers
```

**Example URLs**:
```
https://your-api-gateway-url/prod/customers?limit=5
https://your-api-gateway-url/prod/customers?limit=5&cursor=eyJsYXN0SWQiOjV9
```


## Data Model

### Customer
```java
{
  "customerId": int,        // Unique identifier (1-10)
  "fullName": String,       // Customer full name
  "email": String,          // Customer email
  "registrationDate": String // Registration date (dd/MM/yyyy)
}
```

## Response Headers

### Success Response (200 OK)
```
Content-Type: application/json
Cache-Control: no-store
X-Content-Type-Options: nosniff
```

### Error Responses (400, 500)
```
Content-Type: application/json
```

## Security Considerations

- **No-store cache**: Prevents sensitive data caching
- **MIME type sniffing prevention**: X-Content-Type-Options header
- **Input validation**: All query parameters are validated and sanitized
- **Error handling**: Generic error messages prevent information leakage


## Author

#### This project is for demonstration purposes.

#### SanjayKrishnan Kannan
