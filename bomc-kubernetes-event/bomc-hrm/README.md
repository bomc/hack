# Getting Started

## Build and run

Build: /*mvn clean install*/

Start app with embedded Tomcat: /*mvn spring-boot:run*/

Swagger-UI: /*http://localhost:8080/bomc-hrm/swagger-ui.html*/
NOTE: see tomcat startup sequence for port:
...
 ProtocolHandler ["http-nio-8080"]
...

### Reference Documentation

## Find all customer

curl -v -X GET "http://localhost:8080/bomc-hrm/customer" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8"

## createCustomer

curl -v -X POST "http://localhost:8080/bomc-hrm/customer" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8/json;charset=UTF-8" -H "Content-Type: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -d "{ \"city\": \"brexit-city\", \"country\": \"UK\", \"dateOfBirth\": \"2000-12-24\", \"emailAddress\": \"boris@brexit.org\", \"firstName\": \"boris\", \"houseNumber\": \"42\", \"id\": 0, \"lastName\": \"brexit\", \"phoneNumber\": \"0424242\", \"postalCode\": \"4242\", \"street\": \"Downingstreet\"}"

{
  "id": 1,
  "emailAddress": "boris@brexit.org",
  "phoneNumber": "0424242",
  "firstName": "boris",
  "lastName": "brexit",
  "dateOfBirth": "2000-12-24",
  "city": "brexit-city",
  "postalCode": "4242",
  "street": "Downingstreet",
  "houseNumber": "42",
  "country": "UK"
}


## updateCustomer

curl -v -X PUT "http://localhost:8080/bomc-hrm/customer" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -H "Content-Type: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -d "{ \"id\": 1, \"emailAddress\": \"bomc@bomc.org\", \"phoneNumber\": \"042-4242424\", \"firstName\": \"michi\", \"lastName\": \"bomcy\", \"dateOfBirth\": \"2011-11-13\", \"city\": \"Honululu\", \"postalCode\": \"42424\", \"street\": \"Downing Street\", \"houseNumber\": \"42\", \"country\": \"CH\"}"

{
  "id": 1,
  "emailAddress": "bomc@bomc.org",
  "phoneNumber": "042-4242424",
  "firstName": "michi",
  "lastName": "bomcy",
  "dateOfBirth": "2011-11-13",
  "city": "Honululu",
  "postalCode": "42424",
  "street": "Downing Street",
  "houseNumber": "42",
  "country": "CH"
}


## getCustomerById

curl -v -X GET "http://localhost:8080/bomc-hrm/customer/1" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8"


## deleteCustomer

curl -v -X DELETE "http://localhost:8080/bomc-hrm/customer/1" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8"


## getCustomerByEmailAddress

curl -v -X POST "http://localhost:8080/bomc-hrm/customer/email-address" -H "accept: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -H "Content-Type: application/vnd.hrm-v1+json;charset=UTF-8;charset=UTF-8" -d "{ \"emailAddress\": \"bomc@bomc.org\"}"

{
  "emailAddress": "bomc@bomc.org"
}

# ____________________________________________________
# Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties),
# used by src/main/java/de/bomc/poc/hrm/PersistencePostgreSqlConfig.java
datasource.driver-class-name=org.postgresql.Driver
datasource.url=jdbc:postgresql://localhost:5432/postgres
datasource.username=sa
datasource.password=
datasource.platform=postgresql
# ____________________________________________________
# Connection pool (see https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
# ----------------------------------------------------
datasource.hikari.poolName=${spring.application.name}
# 10 minutes
datasource.hikari.maxLifetime=600000
# 5 minutes
datasource.hikari.idleTimeout=300000
datasource.hikari.minimumIdle=5
datasource.hikari.maximumPoolSize=50
# Test query for H2, PostgreSQL
#datasource.hikari.connection-test-query=select 1

# Keep the connection alive if idle for a long time (needed in production)
datasource.testWhileIdle=true
datasource.validationQuery=SELECT 1

# Allows Hibernate to generate SQL optimized for a particular DBMS
jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
# Hibernate ddl auto (create, create-drop, validate, update)
jpa.hibernate.ddl-auto = create-drop
# The SQL dialect makes Hibernate generate better SQL for the chosen database
jpa.properties.hibernate.dialect = org.hibernate.dialect.H2Dialect
jpa.hibernate.show_sql = true