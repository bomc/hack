# spring-boot-2-ping

## Intro

A simple ping endpoint using Spring Boot 2 Router Functions

## Build

```sh
./mvnw clean verify

```

## Run

#### Using Maven

```sh
./mvnw spring-boot:run
```

#### Using Docker

```sh
docker run -p 8080:8080 ping
```

## Usage

```sh
curl http://localhost:8080
```

You should see a "pong" response