# The mission of this project is help with debugging, how external service sends your request.
![test](https://github.com/svart63/http-request-debugger/actions/workflows/build.yml/badge.svg)
[![Coverage](.github/badges/jacoco.svg)](https://github.com/svart63/http-request-debugger/actions/workflows/build.yml)
## How to
1. Run project thru IDE or after building jar file with `java -jar http-request-debugger.jar`
1. Configure your external service, to send request to running http-request-debugger
1. Send request to `host:port/test` ⚠️all requests from you and other users will be accessable in `host:port/`, do not send sensitive data ⚠️
As response you will see the request fields, for example:
```Request: GET: /test
===========headers===============
Content-Length=183
Content-Type=multipart/form-data; boundary=--------------------------780819224858251688264719
...
===========headers============
===========multi parts============
name: form-data-key, size: 15, type: null, body: form-data-value
===========multi parts============
...
```
If you need response as JSON, just add query parameter `host:port/test?type=json`, or add `json` to request path `host:port/test/json`
```json
{
    "headers": {},
    "body": "",
    "parameters": {},
    "multipart": []
}
```
