# edge-inn-reach

Copyright (C) 2021 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0.
See the file "[LICENSE](LICENSE)" for more information.

## Introduction

A Spring based shared library/framework for edge APIs to INN-REACH system.

## Overview

The purpose of this edge API is to bridge interaction between FOLIO system and external INN-REACH system.

## Additional information
### Security

The edge-inn-reach API rely on facilitied of OAUTH2 when interacts with external system.

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
