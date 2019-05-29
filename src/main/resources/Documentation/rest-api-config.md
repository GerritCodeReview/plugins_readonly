@PLUGIN@ - /config/ REST API
==============================

This page describes the REST endpoints that are added by the @PLUGIN@
plugin.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="config-endpoints" />
Readonly Endpoints
------------------------------------------

<a id="get-readonly-status" />
### Get Readonly Status
_GET /config/server/@PLUGIN@~readonly_

Get the Readonly status.

#### Request

```
  GET /config/server/@PLUGIN@~readonly HTTP/1.0
```

As response a string is returned with the Readonly status, either `on` or `off`.

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8

  )]}'
  "on"
```

<a id="set-readonly" />
### Set Readonly Status
_PUT /config/server/@PLUGIN@~readonly_

Set the system in Readonly status.

#### Request

```
  PUT /config/server/@PLUGIN@~readonly HTTP/1.0
```

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8

  )]}'
```

<a id="delete-readonly" />
### Remove Readonly Status
_DELETE /config/server/@PLUGIN@~readonly_

Set the system in Read-Write status.

#### Request

```
  DELETE /config/server/@PLUGIN@~readonly HTTP/1.0
```

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8

  )]}'
```
