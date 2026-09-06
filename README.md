# Resource Reservation System (RRS)

[![Java CI with Maven in Linux](https://github.com/ritajshakeel/resource-reservation-system/actions/workflows/maven.yml/badge.svg)](https://github.com/ritajshakeel/resource-reservation-system/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/ritajshakeel/resource-reservation-system/badge.svg?branch=main)](https://coveralls.io/github/ritajshakeel/resource-reservation-system?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ritajshakeel_resource-reservation-system&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ritajshakeel_resource-reservation-system)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ritajshakeel_resource-reservation-system&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ritajshakeel_resource-reservation-system)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ritajshakeel_resource-reservation-system&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ritajshakeel_resource-reservation-system)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ritajshakeel_resource-reservation-system&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ritajshakeel_resource-reservation-system)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ritajshakeel_resource-reservation-system&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ritajshakeel_resource-reservation-system)

A Java/Maven desktop application for managing shared resource bookings, built with Test-Driven Development, JPA/Hibernate, Google Guice, and a Swing GUI.

## Requirements

- Java 21
- Maven
- Docker and Docker Compose

## Running the application

Start a local Postgres instance:

```bash
docker compose up -d
```

Then run `com.ritajshakeel.rrs.Main` from Eclipse (Run As → Java Application).

## Building and testing

Full build, including unit and integration tests (requires Docker running):

```bash
mvn clean verify
```

Generate a local code coverage report:

```bash
mvn clean verify -Pjacoco
```
Report at `target/site/jacoco/index.html`.

Run mutation testing (scoped to `domain`, `service`, and `controller` — see report for justification):

```bash
mvn test org.pitest:pitest-maven:mutationCoverage
```
Report at `target/pit-reports/index.html`.