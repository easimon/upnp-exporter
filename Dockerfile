ARG BUILD_IMAGE=openjdk:8
ARG TEST_IMAGE=adoptopenjdk/openjdk14:alpine
ARG RUNTIME_IMAGE=adoptopenjdk/openjdk14:alpine-jre

FROM $BUILD_IMAGE as builder

WORKDIR /build

COPY .mvn /build/.mvn/
COPY mvnw pom.xml /build/
COPY upnp-util/pom.xml /build/upnp-util/pom.xml
COPY upnp-exporter/pom.xml /build/upnp-exporter/pom.xml

RUN ./mvnw -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY upnp-util/src /build/upnp-util/src
RUN ./mvnw -B -pl upnp-util -am install

COPY upnp-exporter/src /build/upnp-exporter/src
RUN ./mvnw -B package

# Integration tests
FROM $TEST_IMAGE as test

WORKDIR /build

COPY --from=builder /root/.m2/repository /root/.m2/repository
COPY --from=builder /build /build

RUN ./mvnw -B surefire:test failsafe:integration-test failsafe:verify

# Build runtime image
FROM $RUNTIME_IMAGE

COPY --from=builder /build/upnp-exporter/target/upnp-exporter-*.jar upnp-exporter.jar
ENV JAVA_OPTS -Xmx64m -Xms64m
EXPOSE 8080
USER 65535:65535
CMD java ${JAVA_OPTS} -jar upnp-exporter.jar
