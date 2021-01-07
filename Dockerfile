FROM maven:3.6-jdk-8 as builder

RUN mkdir -p /build

WORKDIR /build

COPY pom.xml /build

RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src /build/src

RUN mvn -DskipTests clean package

FROM jboss/wildfly:latest

COPY --from=builder /build/target/*.war /opt/jboss/wildfly/standalone/deployments/app.war