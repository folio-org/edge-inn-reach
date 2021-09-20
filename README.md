# edge-inn-reach

Copyright (C) 2021 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0.
See the file "[LICENSE](LICENSE)" for more information.

## Introduction

A Spring based shared library/framework for edge APIs to INN-REACH system.

The edge-inn-reach module provides API for:
* receiving and verifying JWT tokens
* set of endpoints for integration with the Inn-Reach system, such as circulation.

## Overview

The purpose of this edge API is to bridge interaction between FOLIO system and external INN-REACH system.

## Additional information
### Security

The edge-inn-reach API rely on facilitied of OAUTH2 when interacts with external system.

### API Details
At the moment it only provides one endpoint to request a new JWT token.

| Method | URL| Description | Permissions |
|---|---|---|---|
| POST | /v2/oauth2/token | Request a new JWT token | Doesn't require any permissions

### Environment variables:

| Name                          | Default value             | Description                                                       |
| :-----------------------------| :------------------------:|:------------------------------------------------------------------|
| OKAPI_URL                     | https://volaris-okapi.ci.folio.org/               | Okapi hostname                            |
| SYSTEM_USER_USERNAME          | edge-innreach-2                                   | System user username                      |
| SYSTEM_USER_USERNAME          | Edge-innreach-1-0-0                               | System user password                      |

### Configuration
At the moment, the module does not require any specific configuration. 
Most of the configuration is kept in code for convenience and will be moved to separate configuration files or environment variables later.

At the moment, the module does not require the creation of a separate institutional user, 
since the module independently creates such a user during initialization and invocation of Tenant API ([POST] /_/tenant).

This module should be deployed and run in the same way as other EDGE modules.

Please refer to the [Configuration](https://github.com/folio-org/edge-inn-rach/blob/master/README.md#configuration) 
section in the [edge-inn-reach](https://github.com/folio-org/edge-inn-reach/blob/master/README.md) documentation to see all available system properties and their default values.

For example, to enable HTTP compression based on `Accept-Encoding` header the `-Dresponse_compression=true` should be specified as VM option.

### Issue tracker

See project [EDGINNREACH](https://issues.folio.org/browse/EDGEINNREACH)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation

Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at
[dev.folio.org](https://dev.folio.org/)
