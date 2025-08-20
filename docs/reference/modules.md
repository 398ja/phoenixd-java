# Module Reference

All PhoenixD Java modules target **Java 21** and are published under the group ID `xyz.tcheeric`. Replace `<version>` with the version of PhoenixD Java that you wish to use.

## phoenixd-base
Core utilities and configuration helpers shared across higher level modules.

```xml
<dependency>
  <groupId>xyz.tcheeric</groupId>
  <artifactId>phoenixd-base</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

*Transitive dependencies:* `commons-configuration2`, `commons-beanutils`.

## phoenixd-model
Data transfer objects and request/response types. Depends on `phoenixd-base` and Jackson for JSON binding.

```xml
<dependency>
  <groupId>xyz.tcheeric</groupId>
  <artifactId>phoenixd-model</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

*Transitive dependencies:* `phoenixd-base`, `jackson-databind`, `lombok`.

## phoenixd-rest
HTTP client wrapping the phoenixd REST API. Includes the model and base modules and handles low level HTTP calls.

```xml
<dependency>
  <groupId>xyz.tcheeric</groupId>
  <artifactId>phoenixd-rest</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

*Transitive dependencies:* `phoenixd-model`, `phoenixd-base`, `commons-beanutils`, `jackson-databind`.
