# UPnP exporter

| Build status | Vulnerabilities |
|--------------|-----------------|
| [![Docker Build](https://img.shields.io/github/workflow/status/easimon/upnp-exporter/Docker%20container%20build?label=docker%20build&logo=docker&cacheSeconds=300)](https://github.com/easimon/upnp-exporter/packages) [![JDK Compatibility](https://img.shields.io/github/workflow/status/easimon/upnp-exporter/JDK%20compatibility%20tests?label=jdk%20compatibility%20tests&logo=java&cacheSeconds=300)](https://github.com/easimon/upnp-exporter/actions?query=workflow%3A%22JDK%20compatibility%20tests%22) | [![Snyk Vulnerabilities for UPNP Exporter](https://img.shields.io/snyk/vulnerabilities/github/easimon/upnp-exporter/upnp-exporter/pom.xml?label=upnp-exporter&logo=snyk&cacheSeconds=300)](https://snyk.io/test/github/easimon/upnp-exporter?targetFile=upnp-exporter/pom.xml) [![Snyk Vulnerabilities for UPnP Utils](https://img.shields.io/snyk/vulnerabilities/github/easimon/upnp-exporter/upnp-util/pom.xml?label=upnp-util&logo=snyk&cacheSeconds=300)](https://snyk.io/test/github/easimon/upnp-exporter?targetFile=upnp-util/pom.xml) [![Snyk Vulnerabilities for Parent POM](https://img.shields.io/snyk/vulnerabilities/github/easimon/upnp-exporter/pom.xml?label=parent-pom&logo=snyk&cacheSeconds=300)](https://snyk.io/test/github/easimon/upnp-exporter?targetFile=pom.xml) |

## Introduction

This is a Prometheus exporter for discoverable UPnP devices, scraping all state variables that are available by calling
a argument-less getter Action, and presenting them in a Prometheus compatible format.

This is an early work in progress, yet enough to auto-discover metrics exported by an AVM Fritz!Box.
I also have other devices announced via SSDP on my network (FireTV, Logitech Harmony), but none of those
seem to export any useful SOAP operations.

## Limitations

UPnP SSDP discovery requires Multicast networking. Especially for Docker, this requires `--net host`, and this in turn
is not supported on Docker for Mac.

## Building

Either install a JDK (tested with OpenJDK 8) and run `./mvnw package` to create an executable JAR only
(to be found in unp-exporter/target then), or install Docker and use
`docker build . -t upnp-exporter:latest` to create a Docker image.

## Usage

By default, the exporter runs on port 19133, which can be overridden by setting the `UPNP_SERVER_PORT` env
variable. The server then listens on this port (plain HTTP), prometheus metrics are
available at `http://host:$port/prometheus`.

### Configuration

For complete list of configurable items and their defaults, see the
[application.yml](./upnp-exporter/src/main/resources/application.yml)

| Environment variable | Description             | Default | Required |
|----------------------|-------------------------|---------|----------|
| UPNP_SERVER_PORT     | HTTP Server port        | 19133   | no       |

### Prometheus scraping config

```yaml
scrape_configs:
  - job_name: upnp-exporter
    metrics_path: /prometheus
    scheme: http
    static_configs:
      - targets:
        - upnp-exporter:19133
```

### Grafana dashboard

TODO

### Available metrics

TODO

### How it works

The exporter discovers all UPnP devices announcing themselves via Multicast SSDP. It then registers Meters for all
numeric or boolean state variables that are retrievable by calling an argument-less getter SOAP action.

## References

- [UPnP™ Device Architecture 1.1](http://upnp.org/specs/arch/UPnP-arch-DeviceArchitecture-v1.1.pdf)

## Known Issues and TODOs

- Discovery does not work without Multicast, e.g. in Docker without `--net host`, esp. Docker for Mac.
- Does not work on OpenJ9 variants of OpenJDK (Runs into stack overflows in tests, Jackson serialization yields empty Strings)
