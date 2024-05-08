# edge-dcb

Copyright (C) 2021-2023 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Introduction
The purpose of this edge API is to bridge the gap between DCB provider and FOLIO.

Primarily, there are two modules involved in DCB workflow: mod-dcb, which interacts with other Folio modules and edge-dcb, which acts as a gate between DCB and Folio:

`FOLIO <–> mod-dcb <–> edge-dcb <–> DCB`


## Additional information

### API Details
API provides the following URLs for working with DCB :

| Method | URL                                                | Description |
|---|----------------------------------------------------|---|
| GET | /dcbService/transactions/{dcbTransactionId}/status | Get transaction status across circulation institutions |
| PUT | /dcbService/transactions/{dcbTransactionId}/status | Update transaction status across circulation institutions |
| POST | /dcbService/transactions/{dcbTransactionId}        | Create a dcb transaction for circulation institutions  |
| GET    | /dcbService/transactions/status                    | get list of transaction updated between a given query range |

# Security
The edge-fqm API is secured via the facilities provided by edge-common. More specifically, via API Key. See edge-common for additional details.See [edge-common-spring](https://github.com/folio-org/edge-common-spring)

## Required Permissions
Institutional users should be granted the following permissions in order to use this edge API:
- `dcb.all`

# Installation/Deployment

## Configuration

* See [edge-common](https://github.com/folio-org/edge-common) for a description of how configuration works.

***System properties***

| Property             | Default                                   | Description                                                         |
|----------------------|-------------------------------------------|---------------------------------------------------------------------|
| `server.port`        | `8081`                                    | Server port to listen on                                            |
| `okapi_url`          | `http://okapi:9130`	                      | Okapi (URL)                                                         |
| `secure_store`       | `Ephemeral`                               | Type of secure store to use.  Valid: `Ephemeral`, `AwsSsm`, `Vault` |
| `secure_store_props` | `src/main/resources/ephemeral.properties` | Path to a properties file specifying secure store configuration     |

### Ephemeral properties for Karate runs.
For Karate Tests to run successfully the `ephemeral.properties` values would be as mentioned below -
```
secureStore.type=Ephemeral
tenants=testedgedcb
testedgedcb=dcbClient,password
```
*Note: The value `testedgedcb` is the test tenant needed will be used by Karate test cases to execute all the scenarios.


### Configuring spring-boot

Spring boot properties can be overridden using the specified environment variables, if it is not it can be done using
one of the following approaches (see also the
documentation [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/html/boot-features-external-config.html)):

1. Using the environment variable `SPRING_APPLICATION_JSON` (example: `SPRING_APPLICATION_JSON='{"foo":{"bar":"spam"}}'`)
2. Using the system variables within the `JAVA_OPTIONS` (example: `JAVA_OPTIONS=-Xmx400m -Dserver.port=1234`)

### Issue tracker
See project [EDGEDCB](https://issues.folio.org/browse/EDGEDCB)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation
Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at
[dev.folio.org](https://dev.folio.org/)
