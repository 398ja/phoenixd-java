# API Models

This document outlines the request parameters and response payloads used by Phoenixd-Java.

## Request Classes

### CreateBolt11InvoiceRequest
Uses `CreateInvoiceParam`.

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| description | String | No | `null` | None |
| amountSat | Integer | No | `null` | None |
| expirySeconds | Integer | No | `null` | None |
| externalId | String | No | `null` | None |
| webhookUrl | URL | No | `null` | Must be a valid URL |

**Sample JSON**
```json
{
  "description": "desc",
  "amountSat": 100,
  "expirySeconds": 60,
  "externalId": "id123",
  "webhookUrl": "https://example.com/hook"
}
```

### DecodeInvoiceRequest
Uses `DecodeInvoiceParam`.

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| invoice | String | No | `null` | None |

**Sample JSON**
```json
{
  "invoice": "lnbc1example"
}
```

### GetLightningAddressRequest
Uses `VoidRequestParam` and has no fields.

### PayBolt11InvoiceRequest
Uses `PayBolt11InvoiceParam`.

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| amountSat | Integer | No | `0` | None |
| invoice | String | No | "" | None |

**Sample JSON**
```json
{
  "amountSat": 150,
  "invoice": "lnbc1payinvoice"
}
```

### PayLightningAddressRequest
Uses `PayLightningAddressParam`.

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| amountSat | Integer | No | `0` | None |
| address | String | No | `null` | None |
| message | String | No | `null` | Omitted if empty |

**Sample JSON**
```json
{
  "amountSat": 200,
  "address": "name@domain",
  "message": "hello"
}
```

## Response Classes

### CreateInvoiceResponse

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| amountSat | Integer | No | `null` | None |
| paymentHash | String | No | `null` | None |
| serialized | String | No | `null` | None |

**Sample JSON**
```json
{
  "amountSat": 100,
  "paymentHash": "hash",
  "serialized": "lnbc1serialized"
}
```

### DecodeInvoiceResponse

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| amount | Integer | No | `null` | None |
| description | String | No | `null` | None |
| chain | String | No | `null` | Ignored during JSON serialization |
| paymentHash | String | No | `null` | Ignored during JSON serialization |
| minFinalCltvExpiryDelta | Integer | No | `null` | Ignored during JSON serialization |
| paymentSecret | String | No | `null` | Ignored during JSON serialization |
| paymentMetadata | String | No | `null` | Ignored during JSON serialization |
| extraHops | List<List<ExtraHop>> | No | `null` | Ignored during JSON serialization |
| features | Features | No | `null` | Ignored during JSON serialization |
| timestampSeconds | Long | No | `null` | Ignored during JSON serialization |

`ExtraHop` has fields `nodeId`, `shortChannelId`, `feeBase`, `feeProportionalMillionths`, and `cltvExpiryDelta`.
`Features` contains `activated` and `unknown`. `Activated` exposes `var_onion_optin`, `payment_secret`, `basic_mpp`, `option_payment_metadata`, and `trampoline_payment_experimental`.

**Sample JSON**
```json
{
  "amount": 100,
  "description": "desc"
}
```

### GetLightningAddressResponse

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| lightningAddress | String | Yes | `null` | None |

**Sample JSON**
```json
{
  "lightningAddress": "name@domain"
}
```

### PayInvoiceResponse
Base response used by `PayBolt11InvoiceInvoiceResponse` and `PayLightningAddressInvoiceResponse`.

| Field | Java Type | Required | Default | Constraints |
|-------|-----------|----------|---------|-------------|
| recipientAmountSat | Integer | No | `null` | Omitted if null |
| routingFeeSat | Integer | No | `null` | Omitted if null |
| paymentId | String | No | `null` | Omitted if null |
| paymentHash | String | Yes | `null` | None |
| paymentPreimage | String | No | `null` | Omitted if null |
| reason | String | No | `null` | Omitted if null |

**Sample JSON**
```json
{
  "recipientAmountSat": 95,
  "routingFeeSat": 5,
  "paymentId": "id123",
  "paymentHash": "hash",
  "paymentPreimage": "preimage",
  "reason": null
}
```

### PayBolt11InvoiceInvoiceResponse
Extends `PayInvoiceResponse` without adding fields.

### PayLightningAddressInvoiceResponse
Extends `PayInvoiceResponse` without adding fields.

### VoidResponse
Empty response with no fields.
