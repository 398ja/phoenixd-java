# Configuration

The client reads settings from environment variables and an optional `app.properties` file. Environment variables take precedence over values in the properties file.

## Environment variables

The following variables configure access to a running `phoenixd` instance:

| Variable | Format | Default | Notes |
|----------|--------|---------|-------|
| `PHOENIXD_USERNAME` | String | _none_ | HTTP basic auth user. Keep this value secret and avoid committing it to source control. |
| `PHOENIXD_PASSWORD` | String | _none_ | HTTP basic auth password. Treat as a secret. |
| `PHOENIXD_BASE_URL` | URL (e.g. `https://localhost:9740`) | _none_ | Base address of the `phoenixd` REST API. Prefer HTTPS to protect credentials in transit. |

## Property file keys

Configuration can also be supplied through an `app.properties` file on the classpath. Keys are namespaced with the `phoenixd.` prefix and may be overridden by the environment variables above.

| Key | Format | Default | Notes |
|-----|--------|---------|-------|
| `phoenixd.username` | String | empty | Username for basic authentication. |
| `phoenixd.password` | String | empty | Password for basic authentication. Store securely. |
| `phoenixd.base_url` | URL | empty | Base address of the API endpoint. If no scheme is provided, `http://` is assumed. |
| `phoenixd.timeout` | Integer (ms) | `5000` | HTTP request timeout. |
| `phoenixd.webhook_secret` | String | empty | Optional secret used to validate webhook callbacks. Treat as a secret. |

### Example `app.properties`

```properties
phoenixd.username=alice
phoenixd.password=s3cr3t
phoenixd.base_url=https://localhost:9740
phoenixd.timeout=5000
phoenixd.webhook_secret=changeme
```

### Security considerations

- Prefer environment variables for secrets in CI/CD environments.
- Limit file permissions on configuration files and avoid committing credentials to version control.
- Use HTTPS in `PHOENIXD_BASE_URL` to protect basic authentication credentials.
