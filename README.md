# edge-inn-reach

Copyright (C) 2021 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0.
See the file "[LICENSE](LICENSE)" for more information.

## Introduction

A Spring based shared library/framework for edge APIs to INN-REACH system.

## Overview

The purpose of this edge API is to bridge interaction between FOLIO system and external INN-REACH system..
There are two modules involved in InnReach workflow: mod-inn-reach, which interacts with other Folio modules and edge-inn-reach, 
which acts as a gate between InnReach and Folio:

`InnReach <–> edge-inn-reach <–> mod-inn-reach <–> Folio`

## Additional information
### Security

The edge-inn-reach API rely on facilitied of OAUTH2 when interacts with external system.
It uses JWT tokens to verify incoming InnReach requests.

### API Details
API provides the following URLs:

| Method | URL | Headers | Description | 
|---|---|---|---|
| POST | /v2/oauth2/token?grant_type=client_credentials&scope=innreach_tp | authorization | Creates a new JWT token |

### Deployment information
#### InnReach Central Server setup
1. InnReach Central Server connection should be established from the InnReach edge Folio module. Therefore InnReach edge module
   needs to know the name of all the tenants mappings between InnReach Central Server and Folio tenant, which has InnReach Central Server connection. For the ephemeral configuration these mappings locate in the
   `ephemeral.properties` (key `tenantsMappings`). In order to provide it before the deployment the list of tenant mappings (by local server keys) should be put to AWS parameters store (as String). The tenant mappings list separated by
   coma (e.g. 5858f9d8-1558-4513-aa25-bad839eb803a:diku, 62031473-09b9-4617-8bca-9da16ee546a0:someothertenantname) should be stored in AWS param store in the variable with
   key: `innreach_tenants_mappings` by default or could be provided its own key through `innreach_tenants_mappings` parameter of starting module.
2. For each tenant using InnReach the corresponding user should be added
   to the AWS parameter store with key in the following format `{{username}}_{{tenant}}_{{username}}` (where salt and username are the same - `{{username}}`) with value of corresponding `{{password}}` (as Secured String).
   This user should work as ordinary edge institutional user with the only one difference
- his username and salt name are - `{{username}}`.
  By default the value of `{{username}}` is `innreachClient`. It could be changed through `innreach_client` parameter of starting module.
3. User `{{username}}` with password `{{password}}` and inn-reach.all permissions should be created on FOLIO.

##### Create InnReach Central Server configuration
1. Log in to Folio, go to "Settings" -> "INN-Reach" -> "Central server configuration", click "New" button.
2. Fill in all the required fields
3. Press Generate keypair for Local server key and local server secret generation
4. Press save & close

*Note: InnReach Central Server configuration settings applied only upon module startup, so in case of their changes, edge-inn-reach service must be restarted.*

### Required Permissions
The following permissions should be granted to institutional users (as well as InnReach tenants) in order to use this edge API:
- `inn-reach.all`
                              
### Configuration

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
