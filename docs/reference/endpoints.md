# REST API Endpoints

This document describes the REST endpoints exposed by phoenixd.

## POST /createinvoice
### Parameters
| Name | Type | Required | Description |
|------|------|----------|-------------|
| description | string | Yes | Invoice description. |
| amountSat | integer | Yes | Amount in satoshis. |
| expirySeconds | integer | No | Expiry in seconds. |
| externalId | string | No | External identifier. |
| webhookUrl | URL | No | URL to receive webhook callbacks. |

### Response
| Field | Type | Description |
|-------|------|-------------|
| amountSat | integer | Amount of the invoice in satoshis. |
| paymentHash | string | SHA-256 hash of the payment preimage. |
| serialized | string | BOLT11 invoice string. |

### Errors
- `400 Bad Request` – invalid parameters.
- `500 Internal Server Error` – processing failure.

### Example
**Request**

```
POST /createinvoice
Content-Type: application/x-www-form-urlencoded

description=Coffee&amountSat=10&expirySeconds=3600
```

**Response**

```json
{
  "amountSat": 10,
  "paymentHash": "hash",
  "serialized": "invoice"
}
```

## POST /decodeinvoice
### Parameters
| Name | Type | Required | Description |
|------|------|----------|-------------|
| invoice | string | Yes | BOLT11 invoice to decode. |

### Response
| Field | Type | Description |
|-------|------|-------------|
| amount | integer | Invoice amount in millisatoshis. |
| description | string | Description from the invoice. |

### Errors
- `400 Bad Request` – invoice cannot be decoded.
- `500 Internal Server Error` – processing failure.

### Example
**Request**

```
POST /decodeinvoice
Content-Type: application/x-www-form-urlencoded

invoice=lnbc1...
```

**Response**

```json
{
  "amount": 1000,
  "description": "1 Blockaccino"
}
```

## GET /getlnaddress
### Parameters
None.

### Response
Plain text Lightning address string.

### Errors
- `404 Not Found` – address unavailable.
- `500 Internal Server Error` – processing failure.

### Example
**Response**

```
398ja@strike.me
```

## POST /payinvoice
### Parameters
| Name | Type | Required | Description |
|------|------|----------|-------------|
| invoice | string | Yes | BOLT11 invoice to pay. |
| amountSat | integer | Yes | Amount to pay in satoshis. |

### Response
| Field | Type | Description |
|-------|------|-------------|
| recipientAmountSat | integer | Amount delivered to the recipient. |
| routingFeeSat | integer | Lightning routing fees, if any. |
| paymentId | string | Identifier of the payment. |
| paymentHash | string | Payment hash. |
| paymentPreimage | string | Payment preimage (present on success). |
| reason | string | Error reason when the payment fails. |

### Errors
- `400 Bad Request` – invalid invoice or amount.
- `402 Payment Required` – payment rejected.
- `500 Internal Server Error` – processing failure.

### Example
**Request**

```
POST /payinvoice
Content-Type: application/x-www-form-urlencoded

amountSat=10&invoice=lnbc1...
```

**Response**

```json
{
  "recipientAmountSat": 10,
  "routingFeeSat": 1,
  "paymentId": "123",
  "paymentHash": "hash",
  "paymentPreimage": "preimage"
}
```

## POST /paylnaddress
### Parameters
| Name | Type | Required | Description |
|------|------|----------|-------------|
| address | string | Yes | Lightning address to pay (e.g., user@domain.com). |
| amountSat | integer | Yes | Amount to pay in satoshis. |
| message | string | No | Optional memo to send to the recipient. |

### Response
Same as `/payinvoice` response.

### Errors
- `400 Bad Request` – invalid address or amount.
- `402 Payment Required` – payment rejected.
- `500 Internal Server Error` – processing failure.

### Example
**Request**

```
POST /paylnaddress
Content-Type: application/x-www-form-urlencoded

amountSat=10&address=398ja@strike.me&message=Thanks
```

**Response**

```json
{
  "recipientAmountSat": 10,
  "paymentHash": "hash"
}
```

