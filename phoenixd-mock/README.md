# phoenixd-mock

A mock Lightning node server that simulates phoenixd behavior for testing purposes. It provides HTTP endpoints compatible with phoenixd's REST API, enabling development and testing without a real Lightning node.

## Overview

`phoenixd-mock` is designed for:
- Unit and integration testing
- Local development without a real Lightning node
- End-to-end testing of payment flows
- Simulating various payment scenarios (including overpayment)

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PHOENIXD_AUTOPAY_ENABLED` | `true` | Enable automatic invoice settlement |
| `PHOENIXD_AUTO_SETTLE_DELAY_SECONDS` | `2` | Delay before auto-settling (when enabled) |
| `PHOENIXD_WEBHOOK_BASE_URL` | `http://cashu-gateway-rest:8080` | Gateway webhook URL |
| `phoenixd_mock_port` | `9740` | Server port |

### Autopay Modes

#### Development Mode (default)

```bash
PHOENIXD_AUTOPAY_ENABLED=true
```

Invoices are automatically settled after the configured delay. Use for fast development and testing where manual payment simulation is not needed.

#### Production-Like Mode

```bash
PHOENIXD_AUTOPAY_ENABLED=false
```

Invoices remain pending until paid via the `/mockpay` endpoint. Use for testing realistic payment flows where clients must handle invoices and poll for payment status.

## Endpoints

### Standard phoenixd Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/createinvoice` | POST | Create a new invoice |
| `/getinvoice` | GET | Check invoice status |
| `/payinvoice` | POST | Pay an external invoice |
| `/decodeinvoice` | GET | Decode a BOLT11 invoice |

### Mock-Specific Endpoints

#### `/mockpay` - Simulate Payment

When autopay is disabled, use this endpoint to simulate receiving a Lightning payment.

**Request:**

```bash
# Pay exact quoted amount
curl -X POST "http://localhost:9740/mockpay?paymentHash=<hash>"

# Simulate overpayment (e.g., tip scenario)
curl -X POST "http://localhost:9740/mockpay?paymentHash=<hash>&amountSat=1500"
```

**Parameters:**

| Parameter | Required | Description |
|-----------|----------|-------------|
| `paymentHash` | Yes | The payment hash from invoice creation |
| `amountSat` | No | Override amount to simulate over/underpayment |

**Response (Success):**

```json
{
  "status": "paid",
  "paymentHash": "hash123...",
  "quotedAmountSat": 1000,
  "paidAmountSat": 1000,
  "paymentType": "exact"
}
```

**Response (Overpayment):**

```json
{
  "status": "paid",
  "paymentHash": "hash123...",
  "quotedAmountSat": 1000,
  "paidAmountSat": 1500,
  "paymentType": "overpayment"
}
```

**Response (Already Paid):**

```json
{
  "status": "already_paid",
  "paymentHash": "hash123..."
}
```

**Error Responses:**

| Status | Response | Cause |
|--------|----------|-------|
| 400 | `{"error":"paymentHash parameter required"}` | Missing paymentHash |
| 400 | `{"error":"Invalid amountSat value"}` | Non-numeric amountSat |
| 404 | `{"error":"Invoice not found","paymentHash":"..."}` | Unknown invoice |

## Usage Examples

### Development Mode (Auto-Settlement)

```bash
# Start server with default settings
docker run -p 9740:9740 docker.398ja.xyz/phoenixd-mock

# Create invoice - it will auto-settle after 2 seconds
curl -X POST "http://localhost:9740/createinvoice" \
  -d "amountSat=1000&externalId=order123"
```

### Production-Like Mode (Manual Settlement)

```bash
# Start server with autopay disabled
docker run -p 9740:9740 \
  -e PHOENIXD_AUTOPAY_ENABLED=false \
  docker.398ja.xyz/phoenixd-mock

# Create invoice
RESPONSE=$(curl -s -X POST "http://localhost:9740/createinvoice" \
  -d "amountSat=1000&externalId=order123")

# Extract payment hash
PAYMENT_HASH=$(echo $RESPONSE | jq -r '.paymentHash')

# Check invoice status (should be PENDING)
curl "http://localhost:9740/getinvoice?paymentHash=$PAYMENT_HASH"

# Simulate payment received
curl -X POST "http://localhost:9740/mockpay?paymentHash=$PAYMENT_HASH"

# Check invoice status (should be PAID)
curl "http://localhost:9740/getinvoice?paymentHash=$PAYMENT_HASH"
```

### Testing Overpayment Scenarios

```bash
# Create invoice for 1000 sats
RESPONSE=$(curl -s -X POST "http://localhost:9740/createinvoice" \
  -d "amountSat=1000")
PAYMENT_HASH=$(echo $RESPONSE | jq -r '.paymentHash')

# Simulate overpayment of 1500 sats (500 sat tip)
curl -X POST "http://localhost:9740/mockpay?paymentHash=$PAYMENT_HASH&amountSat=1500"
```

## Docker Compose

### Development Configuration

```yaml
services:
  phoenixd-mock:
    image: docker.398ja.xyz/phoenixd-mock:latest
    environment:
      PHOENIXD_AUTOPAY_ENABLED: "true"
      PHOENIXD_AUTO_SETTLE_DELAY_SECONDS: "2"
      PHOENIXD_WEBHOOK_BASE_URL: "http://cashu-gateway-rest:8080"
    ports:
      - "9740:9740"
```

### Staging Configuration

```yaml
services:
  phoenixd-mock:
    image: docker.398ja.xyz/phoenixd-mock:latest
    environment:
      PHOENIXD_AUTOPAY_ENABLED: "false"
      PHOENIXD_WEBHOOK_BASE_URL: "http://cashu-gateway-rest:8080"
    ports:
      - "9740:9740"
```

## Payment Behavior

### How Payments Work in Production

In a production Lightning environment:

| Scenario | BOLT11 Behavior | Result |
|----------|-----------------|--------|
| **Exact payment** | Invoice PAID | Success - tokens issued for quoted amount |
| **Overpayment** | Invoice PAID (extra sats received) | Success - tokens issued for quoted amount only |
| **Underpayment** | Payment FAILS (atomic) | No payment - BOLT11 requires exact or overpayment |

### MockLnServer Simulation

The `/mockpay` endpoint simulates these scenarios:

- **No `amountSat`**: Exact payment at quoted amount
- **`amountSat` > quoted**: Overpayment simulation (excess kept by mint)
- **`amountSat` < quoted**: Underpayment simulation (for testing only)

## Related Documentation

- [phoenixd REST API](https://phoenix.acinq.co/server/api)
- [BOLT11 Invoice Specification](https://github.com/lightning/bolts/blob/master/11-payment-encoding.md)
